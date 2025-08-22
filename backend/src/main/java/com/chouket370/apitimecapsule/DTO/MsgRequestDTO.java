package com.chouket370.apitimecapsule.DTO;

import com.chouket370.apitimecapsule.models.MessageStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MsgRequestDTO {
    private String subject;

    private String content;

    @NotNull(message = "Scheduled date must not be null")
    private LocalDateTime scheduledDate;

    @NotEmpty(message = "Recipient email must not be empty")
    @Email(message = "Recipient email must be valid")
    private String recipientEmail;


}
