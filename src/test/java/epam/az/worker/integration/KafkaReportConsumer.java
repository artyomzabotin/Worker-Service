package epam.az.worker.integration;

import epam.az.worker.dto.ReportView;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Profile("kafka-test")
public class KafkaReportConsumer {

    private ConsumerRecord<String, ReportView> lastReportRecord;

    @KafkaListener(topics = "#{kafkaTestConfig.getTopicReports()}", groupId = "test-group-id",
            containerFactory = "reportListenerContainerFactory")
    public void receive(ConsumerRecord<String, ReportView> consumerRecord) {

        lastReportRecord = consumerRecord;
    }

    public void flush() {

        lastReportRecord = null;
    }
}
