package com.productservice.query.controller;

import com.productservice.query.model.ProductResponseModel;
import com.productservice.query.queries.GetAllProductsQuery;
import com.productservice.query.queries.GetProductQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final QueryGateway queryGateway;

    @GetMapping("/{id}")
    public ProductResponseModel getProductDetail(@PathVariable String id){
        GetProductQuery getProductQuery = new GetProductQuery();
        getProductQuery.setId(id);
        ProductResponseModel
                productResponseModel = queryGateway.query(getProductQuery, ResponseTypes.instanceOf(ProductResponseModel.class)).join();
        return productResponseModel;
    }

    @GetMapping
    public List<ProductResponseModel> getAllProduct(){
        GetAllProductsQuery getAllProductsQuery = new GetAllProductsQuery();
        List<ProductResponseModel>
                list = queryGateway.query(getAllProductsQuery, ResponseTypes.multipleInstancesOf(ProductResponseModel.class)).join();
        return list;
    }
}
