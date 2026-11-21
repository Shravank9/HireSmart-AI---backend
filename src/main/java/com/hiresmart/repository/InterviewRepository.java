package com.hiresmart.repository;

import com.hiresmart.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByCandidateId(Long candidateId);
    List<Interview> findByRecruiterId(Long recruiterId);
    List<Interview> findByApplicationId(Long applicationId);
    long countByRecruiterCompanyId(Long companyId);
}
