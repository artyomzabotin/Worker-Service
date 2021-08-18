package epam.az.worker.service;

import epam.az.worker.dto.ReportView;
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
public class KafkaReportProducerServiceImpl implements KafkaProducerService<ReportView> {

    private static final String TOPIC_REPORTS = "workers-reports";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(ReportView reportView) {

        String key = reportView.getWorkerId().toString();
        ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC_REPORTS, key, reportView);
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
