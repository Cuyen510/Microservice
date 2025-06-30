package com.productservice.service.ProductService;

import com.productservice.dto.ProductDTO;
import com.productservice.dto.ProductImageDTO;
import com.productservice.exceptions.DataNotFoundException;
import com.productservice.exceptions.InvalidParamException;
import com.productservice.model.Product;
import com.productservice.model.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface IProductService {
    Product addProduct(ProductDTO productDTO) throws DataNotFoundException;

    Product getProductById(Long id) throws DataNotFoundException;

    Page<Product> getAllProducts(PageRequest pageRequest);

    Page<Product> searchProduct( Long categoryId, String keyword,PageRequest pageRequest);

    Product updateProduct(Long id, ProductDTO productDTO) throws DataNotFoundException;

    void deleteProduct(Long id);

    ProductImage addProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException;

    Page<Product> getProductByUserId(Long userId, Pageable pageable);

    List<Product> findProductByIds(List<Long> productIds);

}
