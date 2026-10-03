package com.examly.springapp.repository;

import com.examly.springapp.model.ActivityLog;
import com.examly.springapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserOrderByTimestampDesc(User user);

    Page<ActivityLog> findByUserOrderByTimestampDesc(User user, Pageable pageable);

    Page<ActivityLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
