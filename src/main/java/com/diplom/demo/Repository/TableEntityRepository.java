package com.diplom.demo.Repository;

import com.diplom.demo.Entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableEntityRepository extends JpaRepository<TableEntity, Long> {

    List<TableEntity> findByRoomId(Long roomId);
}
