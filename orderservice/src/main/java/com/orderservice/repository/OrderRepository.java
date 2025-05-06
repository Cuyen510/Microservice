package com.orderservice.repository;

import com.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);


    @Query("SELECT o FROM Order o " +
        "WHERE (o.userId = :userId OR :userId IS NULL) " +
        "AND (:keyword IS NULL OR :keyword = '' " +
        "OR o.fullname LIKE CONCAT('%', :keyword, '%') " +
        "OR o.address LIKE CONCAT('%', :keyword, '%') " +
        "OR o.note LIKE CONCAT('%', :keyword, '%') " +
        "OR o.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<Order> searchOrders
            (@Param("userId") Long userId,
             @Param("keyword") String keyword,
             Pageable pageable);

    @Query("SELECT o FROM Order o")
    Page<Order> getAllOrders
            (Pageable pageable);
}
