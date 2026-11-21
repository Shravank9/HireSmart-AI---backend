package com.hiresmart.repository;

import com.hiresmart.entity.Recruiter;
import com.hiresmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    Optional<Recruiter> findByUser(User user);
    Optional<Recruiter> findByUserId(Long userId);
    List<Recruiter> findByCompanyId(Long companyId);
}
