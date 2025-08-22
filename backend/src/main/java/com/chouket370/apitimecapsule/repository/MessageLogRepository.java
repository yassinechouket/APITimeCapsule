package com.chouket370.apitimecapsule.repository;


import com.chouket370.apitimecapsule.models.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
}
