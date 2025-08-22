package com.chouket370.apitimecapsule.quartz.job;

import com.chouket370.apitimecapsule.models.MessageLog;
import com.chouket370.apitimecapsule.models.MessageStatus;
import com.chouket370.apitimecapsule.repository.MessageLogRepository;
import com.chouket370.apitimecapsule.repository.TimeCapsuleMessageRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Component
@AllArgsConstructor
public class EmailJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);


    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final TimeCapsuleMessageRepository messageRepository;
    private final MessageLogRepository logRepository;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String recipientEmail = jobDataMap.getString("email");
        Long messageId = jobDataMap.getLong("id");

        sendMail(mailProperties.getUsername(), recipientEmail, subject, body, messageId);
    }

    private void sendMail(String fromEmail, String toEmail, String subject, String body,Long messageId) {
        try {
            logger.info("Sending Email to {}", toEmail);
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            String processedBody = body.replace("\\n", "\n");
            messageHelper.setText(processedBody, true);


            mailSender.send(message);
            messageRepository.findById(messageId).ifPresent(msg -> {
                msg.setStatus(MessageStatus.SENT);
                messageRepository.save(msg);
            });
            MessageLog log = MessageLog.builder()
                    .messageId(messageId)
                    .status(MessageStatus.SENT)
                    .timestamp(LocalDateTime.now())
                    .errorMessage(null)
                    .build();
            logRepository.save(log);


        } catch (MessagingException ex) {
            logger.error("Failed to send email to {}", toEmail, ex);
            messageRepository.findById(messageId).ifPresent(msg->{
                msg.setStatus(MessageStatus.FAILED);
                messageRepository.save(msg);
            });
            MessageLog log = MessageLog.builder()
                    .messageId(messageId)
                    .status(MessageStatus.FAILED)
                    .timestamp(LocalDateTime.now())
                    .errorMessage(ex.getMessage())
                    .build();
            logRepository.save(log);

        }

    }
}

