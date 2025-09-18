package com.mfreimueller.springrader.persistence;

import com.mfreimueller.springrader.domain.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
}
