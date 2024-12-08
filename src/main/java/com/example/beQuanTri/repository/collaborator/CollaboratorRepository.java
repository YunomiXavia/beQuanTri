package com.example.beQuanTri.repository.collaborator;

import com.example.beQuanTri.entity.collaborator.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, String> {
    // Find UserID From Collaborator
    Optional<Collaborator> findByUser_Id(String userId);
    Optional<Collaborator> findByReferralCode(String referralCode);

    void deleteByUserId(String userId);
}
