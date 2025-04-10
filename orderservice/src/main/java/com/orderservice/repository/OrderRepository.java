package com.orderservice.repository;

import com.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);


    @Query("SELECT o FROM Order o WHERE o.sellerId =:sellerId OR o.buyerId =:buyerId AND (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullname LIKE %:keyword% " +
            "OR o.address LIKE %:keyword% " +
            "OR o.note LIKE %:keyword% " +
            "OR o.email LIKE %:keyword%)")
    Page<Order> searchOrders
            (@Param("buyerId") Long buyerId,
             @Param("sellerId") Long sellerId,
             @Param("keyword") String keyword,
             Pageable pageable);

    @Query("SELECT o FROM Order o")
    Page<Order> getAllOrders
            (Pageable pageable);
}
