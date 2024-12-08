package com.example.beQuanTri.repository.collaborator;

import com.example.beQuanTri.entity.commission.Commission;
import com.example.beQuanTri.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, String> {
    Optional<Commission> findByOrder(Order order);
    List<Commission> findByCollaboratorId(String collaboratorId);
    Page<Commission> findByCollaboratorId(
            String collaboratorId,
            Pageable pageable
    );
}
