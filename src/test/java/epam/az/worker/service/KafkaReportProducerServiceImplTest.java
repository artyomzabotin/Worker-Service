package epam.az.worker.service;

import static org.assertj.core.api.Assertions.assertThat;

import epam.az.worker.dto.ReportView;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

@ExtendWith(MockitoExtension.class)
class KafkaReportProducerServiceImplTest {

    private static final String TOPIC = "workers-reports";

    @InjectMocks
    private KafkaReportProducerServiceImpl producerService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Captor
    private ArgumentCaptor<ProducerRecord<String, Object>> captor;

    @Mock
    private ListenableFuture<SendResult<String, Object>> future;

    @Mock
    private ReportView reportView;

    @Test
    @SuppressWarnings("unchecked")
    void send_reportViewIsProvided_kafkaMessageIsSent () {

        // GIVEN
        Long workerId = 5L;
        Mockito.when(reportView.getWorkerId()).thenReturn(workerId);
        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class))).thenReturn(future);

        // WHEN
        producerService.send(reportView);

        // THEN
        Mockito.verify(kafkaTemplate).send(captor.capture());
        ProducerRecord<String, Object> record = captor.getValue();
        assertThat(captor.getAllValues()).hasSize(1);
        assertThat(record).extracting(ProducerRecord::topic, ProducerRecord::key, ProducerRecord::value)
                .containsExactly(TOPIC, workerId.toString(), reportView);
    }
}
