package com.imalchemy.controller;

import com.imalchemy.model.dto.CategoryDTO;
import com.imalchemy.model.payload.response.Result;
import com.imalchemy.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/category")
@Tag(name = "Category API", description = "Endpoints for category operations")
@SecurityRequirement(name = "BearerToken")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Result> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = this.categoryService.createCategory(categoryDTO);

        return ResponseEntity.status(CREATED).body(Result.builder()
                .flag(true)
                .code(CREATED)
                .message("Category created successfully")
                .data(createdCategory)
                .build()
        );
    }

    @PutMapping("/{categoryName}")
    public ResponseEntity<Result> updateCategory(@PathVariable String categoryName, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = this.categoryService.updateCategory(categoryName, categoryDTO);

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(OK)
                .message("Category updated successfully")
                .data(createdCategory)
                .build()
        );
    }

    @DeleteMapping("/parent/{categoryName}")
    public ResponseEntity<Result> deleteParentCategory(@PathVariable String categoryName) {
        String deletedCategoryName = this.categoryService.deleteParentCategory(categoryName);

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(OK)
                .message("Category deleted successfully")
                .data(deletedCategoryName)
                .build()
        );
    }

    @DeleteMapping("/sub/{categoryName}")
    public ResponseEntity<Result> deleteSubCategory(@PathVariable String categoryName) {
        String deletedCategoryName = this.categoryService.deleteSubCategory(categoryName);

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(OK)
                .message("Category deleted successfully")
                .data(deletedCategoryName)
                .build()
        );
    }

    //TODO: pagination & sorting
    @GetMapping("/list")
    public ResponseEntity<Result> getAllCategories() {
        List<CategoryDTO> categories = this.categoryService.listCategories();

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(OK)
                .message("Category List")
                .data(categories)
                .build()
        );
    }

    //TODO: pagination & sorting
    @GetMapping("/sub/{parentName}")
    public ResponseEntity<Result> getSubcategories(@PathVariable String parentName) {
        List<CategoryDTO> subcategories = this.categoryService.getSubcategories(parentName);

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(OK)
                .message("SubCategory List")
                .data(subcategories)
                .build()
        );
    }

    @PostMapping("/assign/{categoryName}/file/{fileName}")
    public ResponseEntity<Result> assignCategoryToFile(@PathVariable String categoryName, @PathVariable String fileName) {
        this.categoryService.assignCategoryToFile(categoryName, fileName);
        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(OK)
                .message("Category assigned successfully")
                .data(null)
                .build()
        );
    }

}
