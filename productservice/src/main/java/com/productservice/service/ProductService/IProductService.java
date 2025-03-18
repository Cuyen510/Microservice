package com.productservice.service.ProductService;

import com.productservice.dto.ProductDTO;
import com.productservice.dto.ProductImageDTO;
import com.productservice.exceptions.DataNotFoundException;
import com.productservice.exceptions.InvalidParamException;
import com.productservice.model.Product;
import com.productservice.model.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;

    Product getProductById(Long id) throws DataNotFoundException;

    Page<Product> getAllProducts(PageRequest pageRequest);

    Page<Product> searchProduct( Long categoryId, String keyword,PageRequest pageRequest);

    Product updateProduct(Long id, ProductDTO productDTO) throws DataNotFoundException;

    void deleteProduct(Long id);

    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException;

    List<Product> getProductByUserId(Long userId);

}
