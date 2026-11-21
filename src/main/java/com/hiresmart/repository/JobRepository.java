package com.hiresmart.repository;

import com.hiresmart.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(String status);
    List<Job> findByStatusAndApprovalStatus(String status, String approvalStatus);
    List<Job> findByApprovalStatus(String approvalStatus);
    List<Job> findByCompanyId(Long companyId);
    List<Job> findByRecruiterId(Long recruiterId);

    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND (" +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Job> searchActiveJobs(@Param("query") String query);

    long countByCompanyId(Long companyId);
}
