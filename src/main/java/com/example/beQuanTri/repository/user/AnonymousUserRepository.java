package com.example.beQuanTri.repository.user;

import com.example.beQuanTri.entity.user.AnonymousUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnonymousUserRepository extends JpaRepository<AnonymousUser, String> {
    Optional<AnonymousUser> findByNameAndEmailAndPhoneNumber(
            String name,
            String email,
            String phoneNumber
    );

    Optional<AnonymousUser> findByIpAddress(String ipAddress);
}
