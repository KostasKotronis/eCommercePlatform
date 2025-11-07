package com.publicNext.eCommercePlatform.repository;

import com.publicNext.eCommercePlatform.entity.Order;
import com.publicNext.eCommercePlatform.entity.OrderLine;
import com.publicNext.eCommercePlatform.testutil.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager em;

    @Test
    void saveOrderWithLines() {
        Order order = TestDataFactory.newOrder("Maria", "unprocessed", 2);

        Order saved = orderRepository.saveAndFlush(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLines()).hasSize(2);
        assertThat(saved.getLines()).allSatisfy(line -> {
            assertThat(line.getId()).isNotNull();
            assertThat(line.getOrder()).isSameAs(saved);
        });
    }

    @Test
    void removeLineFromOrder() {
        Order order = TestDataFactory.newOrder("Nikos", "unprocessed", 2);
        Order saved = orderRepository.saveAndFlush(order);

        saved.getLines().remove(0);
        orderRepository.saveAndFlush(saved);
        em.clear();

        Order fetched = orderRepository.findById(saved.getId()).orElseThrow();
        assertThat(fetched.getLines()).hasSize(1);
    }

    @Test
    void replaceAllLinesInOrder() {
        Order order = TestDataFactory.newOrder("Eleni", "unprocessed", 2);
        Order saved = orderRepository.saveAndFlush(order);

        saved.getLines().clear();
        OrderLine newLine = new OrderLine();
        newLine.setProductId(999L);
        newLine.setQuantity(5);
        newLine.setPrice(new java.math.BigDecimal("12.34"));
        newLine.setOrder(saved);
        saved.getLines().add(newLine);

        orderRepository.saveAndFlush(saved);
        em.clear();

        Order fetched = orderRepository.findById(saved.getId()).orElseThrow();
        assertThat(fetched.getLines()).hasSize(1);
        assertThat(fetched.getLines().get(0).getProductId()).isEqualTo(999L);
    }

    @Test
    void updateUnprocessedOrdersToProcessed() {
        Order o1 = TestDataFactory.newOrder("A", "unprocessed", 1);
        Order o2 = TestDataFactory.newOrder("B", "unprocessed", 1);
        Order o3 = TestDataFactory.newOrder("C", "processed",   1);
        orderRepository.saveAll(List.of(o1, o2, o3));
        orderRepository.flush();

        int updatedCount = orderRepository.processOrders();
        assertThat(updatedCount).isEqualTo(2);

        em.clear();
        List<Order> all = orderRepository.findAll();
        assertThat(all).allSatisfy(o -> assertThat(o.getStatus()).isEqualTo("processed"));
    }
}