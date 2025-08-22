package com.chouket370.apitimecapsule.controller;



import com.chouket370.apitimecapsule.DTO.WhatsAppRequestDTO;
import com.chouket370.apitimecapsule.service.WhatsAppMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/whatsapp")
@AllArgsConstructor
public class WhatsAppController {

    private final WhatsAppMessageService whatsAppMessageService;



    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleMessage(@RequestBody WhatsAppRequestDTO dto) {
        whatsAppMessageService.scheduleWhatsAppMessage(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("WhatsApp message scheduled successfully for " + dto.getScheduledDate());
    }
}
