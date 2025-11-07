package com.publicNext.eCommercePlatform.scheduler;

import com.publicNext.eCommercePlatform.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusSchedulerTest {

    @Mock OrderService orderService;
    @InjectMocks OrderStatusScheduler scheduler;

    @Test
    void updatesOrdersWhenUnprocessedExist() {
        when(orderService.processOrders()).thenReturn(3);

        scheduler.processUnprocessedOrders();

        verify(orderService).processOrders();
        verifyNoMoreInteractions(orderService);
    }

    @Test
    void skipsUpdateWhenNoOrdersFound() {
        when(orderService.processOrders()).thenReturn(0);

        scheduler.processUnprocessedOrders();

        verify(orderService).processOrders();
    }

    @Test
    void logsErrorWhenExceptionOccurs() {
        when(orderService.processOrders()).thenThrow(new RuntimeException("DB down"));

        scheduler.processUnprocessedOrders();

        verify(orderService).processOrders();
    }
}
