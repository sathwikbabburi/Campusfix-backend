package com.campusfix.repository;

import com.campusfix.model.Role;
import com.campusfix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByRoleAndDepartment(Role role, String department);
    List<User> findByRoleIn(List<Role> roles);
}
