package com.example.api_1.controller;

import com.example.api_1.entity.Category;
import com.example.api_1.pojo.CategoryBody;
import com.example.api_1.repo.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoryRepository repository;


    public CategoriesController(CategoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Category>> getCategories(){return ResponseEntity.ok(repository.findAll());}

    @PostMapping
    public ResponseEntity<Category> add(@RequestBody CategoryBody categoryBody){
        Category category = new Category();
        category.setName(categoryBody.getName());
        category.setId_parent_category(categoryBody.getId_parent_category());

        repository.save(category);
        return  ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable long id,@RequestBody Category categoryDetails){
        Category updateCategory = repository.findById(id)
                .orElseThrow(()->new RuntimeException("Category not exist with id: "+ id));
        updateCategory.setName(categoryDetails.getName());
        updateCategory.setId_parent_category(categoryDetails.getId_parent_category());

        repository.save(updateCategory);
        return ResponseEntity.ok(updateCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable long id){
        repository.deleteById(id);
        return ResponseEntity.ok("Category deleted");
    }
}
