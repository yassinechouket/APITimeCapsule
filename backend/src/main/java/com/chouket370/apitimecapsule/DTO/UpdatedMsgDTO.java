package com.chouket370.apitimecapsule.DTO;


import com.chouket370.apitimecapsule.models.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatedMsgDTO {

    private Optional<String> content= Optional.empty();
    private Optional<LocalDateTime> scheduledDate= Optional.empty();
    private Optional<String> recipientEmail= Optional.empty();
    private Optional<String> attachmentPath= Optional.empty();
}
