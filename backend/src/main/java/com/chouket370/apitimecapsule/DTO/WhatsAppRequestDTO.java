package com.chouket370.apitimecapsule.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WhatsAppRequestDTO {
    private String recipientNumber;
    private String content;
    private LocalDateTime scheduledDate;
}
