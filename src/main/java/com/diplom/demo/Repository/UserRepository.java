package com.diplom.demo.Repository;

import com.diplom.demo.Entity.User;
import com.diplom.demo.Enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findByRoleNot(UserRole role);
    Optional<User> findByEmail(String email);

}
