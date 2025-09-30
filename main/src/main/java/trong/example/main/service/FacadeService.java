package trong.example.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacadeService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaConsumerService kafkaConsumerService;

    public String testKafka(String email, String message) {
        log.info("The function now will return a message, and produce a mail to give email, through kafka");
        String payload = email + "|" + message;
        // Completable Future<SendResult<String, String>> will make the function async (non-blocking)
        kafkaTemplate.send("test-mailing", payload)
                .thenAccept(result -> log.info("Mailing sent: {}", result.getRecordMetadata().topic()))
                .exceptionally(ex -> {
                    log.error("Mailing send failed", ex);
                    throw new RuntimeException(ex);
                });
        kafkaTemplate.send("test-messaging", message)
                .thenAccept(result -> log.info("Messaging sent: {}", result.getRecordMetadata().topic()))
                .exceptionally(ex -> {
                    log.error("Messaging send failed", ex);
                    throw new RuntimeException(ex);
                });
        String reply = kafkaConsumerService.waitForReply();
        return reply;
    }
}
