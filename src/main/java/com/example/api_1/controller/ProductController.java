package com.example.api_1.controller;

import com.example.api_1.entity.Category;
import com.example.api_1.entity.Product;
import com.example.api_1.pojo.ProductBody;
import com.example.api_1.repo.CategoryRepository;
import com.example.api_1.repo.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Product>> getProducts() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping
    public ResponseEntity<Long> add(@RequestBody ProductBody productBody) {
        Long productId = (Long) entityManager.createNativeQuery(
                        "SELECT inf_sys_el_shop.insert_into_all_info_about_product(?::character varying, ?::double precision, ?::bigint, ?::json, ?::character varying, ?::bigint)")
                .setParameter(1, productBody.getProductName())
                .setParameter(2, productBody.getCost())
                .setParameter(3, productBody.getIdCompany())
                .setParameter(4, productBody.getProperties() != null ? productBody.getProperties() : "{}")
                .setParameter(5, productBody.getPhoto())
                .setParameter(6, 1L)
                .getSingleResult();

        return ResponseEntity.ok(productId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody ProductBody productBody) {
        Product updateProduct = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not exist with id: " + id));

        updateProduct.setProductName(productBody.getProductName());
        updateProduct.setCost(productBody.getCost());
        updateProduct.setIdCompany(productBody.getIdCompany());
        updateProduct.setProperties(productBody.getProperties());
        updateProduct.setPhoto(productBody.getPhoto());

        repository.save(updateProduct);
        return ResponseEntity.ok(updateProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok("Product deleted");
    }

    @PostMapping("/{id}/categories/{categoryId}")
    public ResponseEntity<Product> addCategoryToProduct(@PathVariable Long id, @PathVariable Long categoryId) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        if (product.getCategories() == null) {
            product.setCategories(new ArrayList<>());
        }

        if (!product.getCategories().contains(category)) {
            product.getCategories().add(category);
        }

        repository.save(product);
        return ResponseEntity.ok(product);
    }
}