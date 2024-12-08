package com.example.beQuanTri.repository.survey;

import com.example.beQuanTri.entity.survey.Survey;
import com.example.beQuanTri.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, String> {
    List<Survey> findByUser(User user);
    Page<Survey> findByUser(User user, Pageable pageable);
}
