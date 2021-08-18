package epam.az.worker.service;

import epam.az.worker.dto.WorkerView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaWorkersProducerServiceImpl implements KafkaProducerService<WorkerView> {

    private static final String TOPIC_WORKERS = "active-workers";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(WorkerView workerView) {

        ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC_WORKERS, workerView);

        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(record);
        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                log.info("Sent message: \n" +
                                "[{}] \n" +
                                "with next metadata \n" +
                                "-----------------------------------------------\n" +
                                "topic: [{}] \n" +
                                "partition: [{}] \n" +
                                "offset: [{}] \n" +
                                "timestamp: [{}] \n" +
                                "-----------------------------------------------",
                        record,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp());
            }

            @Override
            public void onFailure(Throwable ex) {

                log.warn("Unable to send message: [{}] : {}", record, ex.getMessage());
            }
        });
    }
}
