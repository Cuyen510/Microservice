package com.orderservice.controller;

import com.orderservice.dto.OrderDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO, BindingResult result) throws Exception{
        if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError:: getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        return ResponseEntity.ok().body(orderService.createOrder(orderDTO));
    }

    @GetMapping("")
    public ResponseEntity<?> getAllOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit){
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("orderDate").descending()
        );
        return ResponseEntity.ok().body(orderService.getAllOrders(pageRequest));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") Long buyerId,
            @RequestParam(defaultValue = "") Long sellerId) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("orderDate").descending()
        );
        return ResponseEntity.ok().body(orderService.searchOrders(keyword,buyerId,sellerId,pageRequest));
    }

    @GetMapping("/search/id")
    public ResponseEntity<?> searchOrderById(@PathVariable("id") Long id) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderService.findOrderById(id));
    }

    @PutMapping("/id")
    public ResponseEntity<?> updateOrder(@PathVariable("id") Long id, @RequestBody OrderDTO orderDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/id")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Long id){
        orderService.deleteOrder(id);
        return ResponseEntity.ok().body("order deleted");
    }
}
