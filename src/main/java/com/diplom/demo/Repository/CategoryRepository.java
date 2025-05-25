package com.diplom.demo.Repository;

import com.diplom.demo.Entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    CategoryEntity findByName(String name);
}
