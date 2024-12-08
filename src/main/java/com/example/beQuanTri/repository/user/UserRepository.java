package com.example.beQuanTri.repository.user;

import com.example.beQuanTri.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Page<User> findByRole_RoleName(String roleName, Pageable pageable);
    Optional<User> findByEmail(String email);
}
