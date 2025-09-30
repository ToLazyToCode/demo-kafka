package trong.example.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final JavaMailSender mailSender;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BlockingDeque<String> replyQueue = new LinkedBlockingDeque<>();

    @KafkaListener(topics = "test-messaging", groupId = "demo-group" )
    public void listen(String message) {
        log.info("Received reply message: {}", message);
        String reply = "Processed: " + message + " successfully";
        replyQueue.offer(reply);
    }

    @KafkaListener(topics = "test-mailing", groupId = "demo-group" )
    public void listenMailing(String payload) {
        String[] parts = payload.split("\\|", 2);
        String to = parts[0];
        String message = parts[1];

        log.info("Delivering mailing message: {}", message);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Test Kafka Email");
        mail.setText(message);

        mailSender.send(mail);
    }

    public String waitForReply() {
        try {
            return replyQueue.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "No reply received";
        }
    }
}
