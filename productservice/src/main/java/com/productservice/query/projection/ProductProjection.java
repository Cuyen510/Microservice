package com.productservice.query.projection;

import com.productservice.command.data.Product;
import com.productservice.command.data.ProductRepository;
import com.productservice.exceptions.DataNotFoundException;
import com.productservice.query.model.ProductResponseModel;
import com.productservice.query.queries.GetAllProductsQuery;
import com.productservice.query.queries.GetProductQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductProjection {
    private final ProductRepository productRepository;

    @QueryHandler
    public ProductResponseModel handle(GetProductQuery getProductQuery) throws DataNotFoundException {
        ProductResponseModel model = new ProductResponseModel();
        Product product = productRepository
                .findById(getProductQuery.getId()).orElseThrow(()-> new DataNotFoundException("Can't find product with id: "+getProductQuery.getId()));
        BeanUtils.copyProperties(product, model);
        return model;
    }

    @QueryHandler
    public List<ProductResponseModel> handle(GetAllProductsQuery getAllProductsQuery){
        List<Product> productList = productRepository.findAll();
        List<ProductResponseModel> productResponseModelList = new ArrayList<>();
        productList.forEach(product -> {
            ProductResponseModel productResponseModel = new ProductResponseModel();
            BeanUtils.copyProperties(product, productResponseModel);
            productResponseModelList.add(productResponseModel);
        });
        return productResponseModelList;
    }
}
