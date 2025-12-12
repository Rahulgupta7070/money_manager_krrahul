package kr.rahul.moneyManager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.rahul.moneyManager.dto.CategoryDTO;
import kr.rahul.moneyManager.service.CategoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // If needed, replace with frontend URL
public class CategoryController {

    private final CategoryService categoryService;

    // GET ALL
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategoriesForCurrentUser());
    }

    // GET BY TYPE
    @GetMapping("/categories/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByType(@PathVariable String type){
        return ResponseEntity.ok(categoryService.getCategoriesByTypeForCurrentUser(type));
    }

    // CREATE
    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){
        return new ResponseEntity<>(categoryService.saveCategory(categoryDTO), HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryDTO));
    }

//    // DELETE (ADDED) //ye add nhi hai service me 
//    @DeleteMapping("/categories/{categoryId}")
//    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
//        categoryService.deleteCategory(categoryId);
//        return ResponseEntity.ok("Category deleted successfully");
//    }
}
