package com.example.beQuanTri.repository.status;

import com.example.beQuanTri.entity.status.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, String> {
    Optional<Status> findByStatusName(String name);
}
