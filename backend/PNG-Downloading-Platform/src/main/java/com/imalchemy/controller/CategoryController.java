package com.imalchemy.controller;

import com.imalchemy.model.dto.CategoryDTO;
import com.imalchemy.model.payload.response.Result;
import com.imalchemy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/category")
@Tag(name = "Category API", description = "Endpoints for category operations")
@SecurityRequirement(name = "BearerToken")
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create Category", description = """
            Create a new category with two possible scenarios:

            1. Parent Category Creation:
                - Set 'parent' to true
                - 'parentId' will be 0 by default

            2. Subcategory Creation:
                - Set 'parent' to false
                - Provide the parent category's ID in 'parentId'

            📌 Important Notes:
            - When creating a parent category, parentId is not required
            - When creating a subcategory, parentId must be specified
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Category Created Successfully - Parent Category",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "flag": true,
                                        "code": "CREATED",
                                        "message": "Category created successfully",
                                        "data": {
                                            "id": 2,
                                            "name": "string",
                                            "description": "string",
                                            "iconUrl": "string",
                                            "displayOrder": 1,
                                            "level": 0,
                                            "parentId": 0,
                                            "active": true,
                                            "parent": true
                                        }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Category Created Successfully - Subcategory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "flag": true,
                                        "code": "CREATED",
                                        "message": "Category created successfully",
                                        "data": {
                                            "id": 2,
                                            "name": "string",
                                            "description": "string",
                                            "iconUrl": "string",
                                            "displayOrder": 1,
                                            "level": 0,
                                            "parentId": 2,
                                            "active": true,
                                            "parent": false
                                        }
                                    }
                                    """)
                    )
            )
    })
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
    @Operation(summary = "Update an existing category", description = "Updates a category by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
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
    @Operation(summary = "Delete a parent category", description = "Deletes a parent category and all its subcategories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
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
    @Operation(summary = "Delete a subcategory", description = "Deletes a subcategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory deleted successfully",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
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
    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
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
    @Operation(summary = "Get subcategories", description = "Retrieves all subcategories of a parent category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of subcategories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Parent category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
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

    @GetMapping("/list/parent")
    public ResponseEntity<Result> listParentCategories() {
        List<CategoryDTO> parentCategories = this.categoryService.listParentCategories();

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(OK)
                .message("Category List")
                .data(parentCategories)
                .build()
        );
    }

    @PostMapping("/assign/{categoryName}/file/{fileName}")
    @Operation(summary = "Assign category to file", description = "Assigns a category to a specific file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category assigned successfully",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Category or file not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
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
