package com.chouket370.apitimecapsule.repository;

import com.chouket370.apitimecapsule.DTO.MsgResponseDTO;
import com.chouket370.apitimecapsule.models.TimeCapsuleMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeCapsuleMessageRepository extends JpaRepository<TimeCapsuleMessage,Long> {
    @Query("SELECT new com.chouket370.apitimecapsule.DTO.MsgResponseDTO(" +
            "t.id,t.subject, t.content, t.scheduledDate, t.recipientEmail, t.status, t.attachmentPath, t.createdAt, t.updatedAt) " +
            "FROM TimeCapsuleMessage t " +
            "WHERE t.user.id = :userId")
    List<MsgResponseDTO> getAll(@Param("userId") Long userId);


    Optional<TimeCapsuleMessage> findByIdAndUserUsername(Long id, String username);

}
