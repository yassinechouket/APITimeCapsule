package com.chouket370.apitimecapsule.DTO;

import com.chouket370.apitimecapsule.models.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MsgResponseDTO {
    private Long id;
    private String subject;
    private String content;
    private LocalDateTime scheduledDate;
    private String recipientEmail;
    private MessageStatus status;
    private String attachmentPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;




}
