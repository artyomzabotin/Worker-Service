package epam.az.worker.integration;

import static epam.az.worker.integration.KafkaIntegrationTest.container;

import epam.az.worker.dto.ReportView;
import epam.az.worker.dto.WorkerView;
import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.List;
import java.util.Map;

@Configuration
@Getter
@Profile("kafka-test")
public class KafkaTestConfig {

    final String topicReports = "workers-reports";
    final String topicWorkers = "active-workers";

    @Bean
    @Primary
    public ProducerFactory<String, Object> testProducerFactory() {

        Map<String, Object> properties = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, container.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> testKafkaTemplate() {

        return new KafkaTemplate<>(testProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, ReportView> reportConsumerFactory() {

        JsonDeserializer<ReportView> deserializer = new JsonDeserializer<>(ReportView.class);
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> properties = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, container.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-group-id",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer
        );

        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReportView> reportListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ReportView> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reportConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, WorkerView> workerConsumerFactory() {

        JsonDeserializer<WorkerView> deserializer = new JsonDeserializer<>(WorkerView.class);
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> properties = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, container.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-group-id2",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer
        );

        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WorkerView> workerListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, WorkerView> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(workerConsumerFactory());

        return factory;
    }

    public void createTopics() {

        NewTopic newTopicReports = new NewTopic(topicReports, 1, (short) 1);
        NewTopic newTopicWorkers = new NewTopic(topicWorkers, 1, (short) 1);

        Map<String, Object> properties = Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, container.getBootstrapServers()
        );

        try (AdminClient admin = AdminClient.create(properties)) {
            admin.createTopics(List.of(newTopicReports, newTopicWorkers));
        }
    }
}
