package com.diplom.demo.Repository;

import com.diplom.demo.Entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableEntityRepository extends JpaRepository<TableEntity, Long> {
}
