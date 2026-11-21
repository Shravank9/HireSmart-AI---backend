package com.hiresmart.repository;

import com.hiresmart.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByJobIdOrderByOrderIndexAsc(Long jobId);
    List<InterviewQuestion> findByInterviewIdOrderByOrderIndexAsc(Long interviewId);
}
