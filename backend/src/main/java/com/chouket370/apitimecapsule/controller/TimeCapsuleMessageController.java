package com.chouket370.apitimecapsule.controller;


import com.chouket370.apitimecapsule.DTO.EmailResponse;
import com.chouket370.apitimecapsule.DTO.MsgRequestDTO;
import com.chouket370.apitimecapsule.DTO.MsgResponseDTO;
import com.chouket370.apitimecapsule.DTO.UpdatedMsgDTO;
import com.chouket370.apitimecapsule.models.MessageStatus;
import com.chouket370.apitimecapsule.models.TimeCapsuleMessage;
import com.chouket370.apitimecapsule.models.User;
import com.chouket370.apitimecapsule.service.CurrentUserService;
import com.chouket370.apitimecapsule.service.FileStorageService;
import com.chouket370.apitimecapsule.service.TimeCapsuleMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/timeCapsuleMessage")
@AllArgsConstructor
public class TimeCapsuleMessageController {

    final private TimeCapsuleMessageService timeCapsuleMessageService;
    final private CurrentUserService currentUserService;


    @PostMapping(value = "/save", consumes = {"multipart/form-data"})
    public ResponseEntity<EmailResponse> save(
            @RequestPart("message") MsgRequestDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {


        if (file != null) {
            System.out.println("File received: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("File content type: " + file.getContentType());
        } else {
            System.out.println("No file received");
        }

        EmailResponse response = timeCapsuleMessageService.save(convertToEntity(dto), file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("X-Job-Id", "12345")
                .body(response);
    }



    @GetMapping
    public ResponseEntity<List<MsgResponseDTO>> getAll(){
        return ResponseEntity.ok(timeCapsuleMessageService.getAll());

    }
    @PutMapping("/updateMsg/{id}")
    public ResponseEntity<MsgResponseDTO> updateMsg(@PathVariable Long id, @RequestBody UpdatedMsgDTO updatedMsgDTO){
        return ResponseEntity.ok(convertToResponseDTO(timeCapsuleMessageService.updateMsg(id,updatedMsgDTO)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        timeCapsuleMessageService.deleteMsg(id);
        System.out.println("Deleted Message");
        return ResponseEntity.noContent().build();

    }



    public MsgResponseDTO convertToResponseDTO(TimeCapsuleMessage timeCapsuleMessage){
        return MsgResponseDTO.builder()
                .id(timeCapsuleMessage.getId())
                .subject(timeCapsuleMessage.getSubject())
                .scheduledDate(timeCapsuleMessage.getScheduledDate())
                .createdAt(timeCapsuleMessage.getCreatedAt())
                .updatedAt(timeCapsuleMessage.getUpdatedAt())
                .status(timeCapsuleMessage.getStatus())
                .content(timeCapsuleMessage.getContent())
                .attachmentPath(timeCapsuleMessage.getAttachmentPath())
                .recipientEmail(timeCapsuleMessage.getRecipientEmail())
                .build();
    }
    public TimeCapsuleMessage convertToEntity(MsgRequestDTO dto){
        User user = currentUserService.getCurrentUser();
        return TimeCapsuleMessage.builder()
                .content(dto.getContent())
                .subject(dto.getSubject())
                .recipientEmail(dto.getRecipientEmail())
                .status(MessageStatus.PENDING)
                .scheduledDate(dto.getScheduledDate())
                .user(user)
                .build();
    }




}
