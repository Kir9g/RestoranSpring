package com.diplom.demo.Service;

import com.diplom.demo.DTO.CategoryDTO;
import com.diplom.demo.Entity.CategoryEntity;
import com.diplom.demo.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    private final Path imageUploadPath = Paths.get("uploads/categories");

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        try {
            Files.createDirectories(imageUploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для изображений категорий", e);
        }
    }

    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDTO(cat.getId(), cat.getName(), cat.getImageUrl()))
                .toList();
    }

    public CategoryEntity create(String name, MultipartFile image) throws IOException {
        String imageUrl = saveImage(image);

        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setImageUrl(imageUrl);

        return categoryRepository.save(category);
    }

    public CategoryEntity update(Long id, String name, MultipartFile image) throws IOException {
        Optional<CategoryEntity> optional = categoryRepository.findById(id);
        if (optional.isEmpty()) throw new RuntimeException("Категория не найдена");

        CategoryEntity category = optional.get();
        category.setName(name);

        if (image != null && !image.isEmpty()) {
            category.setImageUrl(saveImage(image));
        }

        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path targetPath = imageUploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/categories/" + fileName;
    }
}
