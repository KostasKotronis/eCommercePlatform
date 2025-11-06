package com.publicNext.eCommercePlatform.repository;

import com.publicNext.eCommercePlatform.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Order o set o.status='processed' where o.status='unprocessed'")
    int processOrders();
}