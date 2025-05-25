package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.CategoryDTO;
import com.diplom.demo.Entity.CategoryEntity;
import com.diplom.demo.Service.CategoryService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Публичный доступ
    @GetMapping("/categories")
    @PermitAll
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    // Админ — создание
    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')") // Только для админа
    public ResponseEntity<CategoryEntity> createCategory(
            @RequestParam String name,
            @RequestParam MultipartFile image) throws IOException {
        return ResponseEntity.ok(categoryService.create(name, image));
    }

    // Админ — редактирование
    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Только для админа
    public ResponseEntity<CategoryEntity> updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(categoryService.update(id, name, image));
    }

    // Админ — удаление
    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Только для админа
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
