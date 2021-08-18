package epam.az.worker.integration;

import epam.az.worker.dto.WorkerView;
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
public class KafkaWorkerConsumer {

    private ConsumerRecord<String, WorkerView> lastWorkerRecord;

    @KafkaListener(topics = "#{kafkaTestConfig.getTopicWorkers()}", groupId = "test-group-id2",
            containerFactory = "workerListenerContainerFactory")
    public void receive(ConsumerRecord<String, WorkerView> consumerRecord) {

        lastWorkerRecord = consumerRecord;
    }

    public void flush() {

        lastWorkerRecord = null;
    }
}
