package com.chouket370.apitimecapsule.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class MessageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long messageId;
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    private LocalDateTime timestamp;
    private String errorMessage;

}
