package com.publicNext.eCommercePlatform.service;

import com.publicNext.eCommercePlatform.entity.Order;
import com.publicNext.eCommercePlatform.entity.OrderLine;
import com.publicNext.eCommercePlatform.repository.OrderRepository;
import com.publicNext.eCommercePlatform.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @InjectMocks OrderService orderService;

    private Order existing;

    @BeforeEach
    void setup() {
        existing = TestDataFactory.newOrder("Existing", "unprocessed", 1);
        existing.setId(1L);
        existing.setOrderDate(LocalDateTime.now().minusMinutes(5));
    }

    @Test
    void findByIdNotFound() {
        when(orderRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(42L))
                .isInstanceOf(ResponseStatusException.class)
                .matches(ex -> ((ResponseStatusException)ex).getStatusCode().value() == HttpStatus.NOT_FOUND.value());
    }

    @Test
    void createBindsLinesBackToOrder() {
        Order incoming = TestDataFactory.newOrder("Create", "unprocessed", 2);

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order saved = inv.getArgument(0);
            saved.setId(10L);
            long lid = 100L;
            for (OrderLine l : saved.getLines()) l.setId(lid++);
            return saved;
        });

        Order saved = orderService.create(incoming);

        assertThat(saved.getId()).isEqualTo(10L);
        assertThat(saved.getLines()).hasSize(2);
        assertThat(saved.getLines()).allSatisfy(l -> assertThat(l.getOrder()).isSameAs(saved));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateReplacesAllLines() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order incoming = new Order();
        incoming.setCustomerName("Updated Name");
        incoming.setStatus("processed");
        OrderLine nl = new OrderLine(null, 999L, 5, new BigDecimal("12.34"), null);
        incoming.setLines(new ArrayList<>(List.of(nl)));

        Order updated = orderService.update(1L, incoming);

        assertThat(updated.getLines()).hasSize(1);
        assertThat(updated.getLines().get(0).getProductId()).isEqualTo(999L);
        assertThat(updated.getLines().get(0).getOrder()).isSameAs(updated);

        assertThat(updated.getStatus()).isEqualTo(existing.getStatus());

        assertThat(updated.getOrderDate()).isEqualTo(existing.getOrderDate());

        verify(orderRepository).save(existing);
    }

    @Test
    void delete() {
        orderService.delete(123L);
        verify(orderRepository).deleteById(123L);
    }
}