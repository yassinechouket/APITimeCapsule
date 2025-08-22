package com.chouket370.apitimecapsule.controller;


import com.chouket370.apitimecapsule.models.MessageLog;
import com.chouket370.apitimecapsule.repository.MessageLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/logs")
@AllArgsConstructor
public class MessageLogController {

    @Autowired
    private final MessageLogRepository messageLogRepository;

    @GetMapping
    public List<MessageLog> getAllLogs() {
        return messageLogRepository.findAll();
    }
}
