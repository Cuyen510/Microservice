package com.productservice.service.ProductService;

import com.productservice.dto.ProductDTO;
import com.productservice.dto.ProductImageDTO;
import com.productservice.exceptions.DataNotFoundException;
import com.productservice.exceptions.InvalidParamException;
import com.productservice.model.Category;
import com.productservice.model.Product;
import com.productservice.model.ProductImage;
import com.productservice.repository.CategoryRepository;
import com.productservice.repository.ProductImageRepository;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product addProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->new DataNotFoundException("Category not found with id:" +productDTO.getCategoryId()));
        Product newProduct = new Product();
        newProduct.setName(productDTO.getName());
        newProduct.setPrice(productDTO.getPrice());
        newProduct.setThumbnail("");
        newProduct.setDescription(productDTO.getDescription());
        newProduct.setCategory(existingCategory);
        newProduct.setStock(productDTO.getStock());
        newProduct.setUserId(productDTO.getUserId());
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long id) throws DataNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Can not find product with id: "+id));
    }

    @Override
    public Page<Product> getAllProducts(PageRequest pageRequest) {
       return productRepository.findAll(pageRequest);
    }

    @Override
    public Page<Product> searchProduct( Long categoryId, String keyword, PageRequest pageRequest) {
        Page<Product> products = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return products;
    }
    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existingProduct = getProductById(id);
        if (existingProduct != null) {
            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found with id:" + productDTO.getCategoryId()));

            existingProduct.setName(productDTO.getName());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setCategory(existingCategory);
            existingProduct.setStock(productDTO.getStock());
            existingProduct.setUserId(productDTO.getUserId());

            return productRepository.save(existingProduct);
        }
        return null;
    }
    @Override
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent()) {
            productRepository.deleteById(id);
        }
        else{
            throw new DataAccessException("product not found") {
            };
        }
    }

    @Override
    public ProductImage addProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()->new DataNotFoundException("product not found with id:" +productImageDTO.getProductId()));

        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        int size = productImageRepository.findByProductId(productId).size();
        if(size == 0){
            existingProduct.setThumbnail(productImageDTO.getImageUrl());
        }
        if (size >= ProductImage.MAXIMUM_PER_PRODUCT){
            throw new InvalidParamException("number of image must be <=" +ProductImage.MAXIMUM_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public Page<Product> getProductByUserId(Long userId, Pageable pageable){
        return productRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<Product> findProductByIds(List<Long> productIds){
        List<Product> products = productRepository.findProductByIds(productIds);
        return products;
    }

}
