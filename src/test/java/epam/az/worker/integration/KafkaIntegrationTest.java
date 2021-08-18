package epam.az.worker.integration;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.restassured.RestAssured;
import epam.az.worker.WorkerServiceApplication;
import epam.az.worker.domain.Position;
import epam.az.worker.domain.Worker;
import epam.az.worker.dto.ReportForm;
import epam.az.worker.dto.ReportView;
import epam.az.worker.dto.WorkerView;
import epam.az.worker.repository.WorkerRepository;
import org.apache.http.HttpStatus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                WorkerServiceApplication.class,
                KafkaTestConfig.class}
)
@DirtiesContext
@ActiveProfiles("kafka-test")
class KafkaIntegrationTest {

    private static final Position POSITION = Position.ADMINISTRATOR;
    private static final String FULL_NAME = "Chuck Norris";
    private static final String TIMEZONE = "Europe/Paris";
    private static final String SHOP = "Paris";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @ClassRule
    public static WorkerKafkaContainer container = new WorkerKafkaContainer();

    @Autowired
    private KafkaReportConsumer reportConsumer;

    @Autowired
    private KafkaWorkerConsumer workerConsumer;

    @Autowired
    private KafkaTestConfig kafkaTestConfig;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ConversionService conversionService;

    @LocalServerPort
    private int port;
    private Long workerId;
    private String topicWorkers;
    private String topicReports;

    @BeforeEach
    public void setUp() {

        RestAssured.port = this.port;
        kafkaTestConfig.createTopics();

        Worker worker = new Worker();
        worker.setPosition(POSITION);
        worker.setFullName(FULL_NAME);
        worker.setActive(false);
        worker.setZone(TIMEZONE);
        worker.setShop(SHOP);

        workerId = workerRepository.save(worker).getId();
        topicWorkers = kafkaTestConfig.getTopicWorkers();
        topicReports = kafkaTestConfig.getTopicReports();
    }

    @Test
    void exportActiveWorkers_workersExist_kafkaMessagesSent() {

        // GIVEN
        Worker worker = new Worker();
        worker.setShop(SHOP);
        worker.setPosition(POSITION);
        worker.setZone(TIMEZONE);
        worker.setFullName(FULL_NAME + "Active");
        worker.setActive(true);

        Long workerId = workerRepository.save(worker).getId();

        WorkerView expected = WorkerView.builder()
                .id(workerId)
                .fullName(FULL_NAME + "Active")
                .position(POSITION.toString())
                .shop(SHOP)
                .build();

        workerConsumer.flush();

        // WHEN-THEN
        given()
                .contentType("application/json")
                .when()
                .get("/workers/export")
                .then()
                .statusCode(HttpStatus.SC_OK);

        ConsumerRecord<String, WorkerView> actual = workerConsumer.getLastWorkerRecord();

        assertThat(actual)
                .extracting(ConsumerRecord::value, ConsumerRecord::topic)
                .containsExactly(expected, topicWorkers);
    }

    @Test
    void reportTime_kafkaBrokerExistsAndReportFormIsCorrect_reportIsSent() {

        // GIVEN
        String description = "new feature";
        Integer hours = 5;
        String stringDate = "14/04/2021";
        LocalDate date = LocalDate.parse(stringDate, FORMATTER);

        ReportForm reportForm = ReportForm.builder()
                .workerId(workerId)
                .date(stringDate)
                .description(description)
                .hours(hours)
                .build();

        ReportView expected = ReportView.builder()
                .workerId(workerId)
                .date(date)
                .workDescription(description)
                .hours(hours)
                .build();

        reportConsumer.flush();

        // WHEN-THEN
        given()
                .contentType("application/json")
                .when()
                .body(reportForm)
                .post("/workers/time")
                .then()
                .statusCode(HttpStatus.SC_ACCEPTED);

        ConsumerRecord<String, ReportView> actual = reportConsumer.getLastReportRecord();

        assertThat(actual)
                .extracting(ConsumerRecord::key, ConsumerRecord::value, ConsumerRecord::topic)
                .containsExactly(workerId.toString(), expected, topicReports);
    }

    @Test
    void reportTime_kafkaBrokerExistsAndDateIsInvalid_dateFormatExceptionIsThrownAndReportIsNotSent() {

        // GIVEN
        String description = "new feature";
        Integer hours = 5;
        String stringDate = "invalidDate";

        ReportForm reportForm = ReportForm.builder()
                .workerId(workerId)
                .date(stringDate)
                .description(description)
                .hours(hours)
                .build();

        reportConsumer.flush();

        // WHEN-THEN
        given()
                .contentType("application/json")
                .when()
                .body(reportForm)
                .post("/workers/time")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        ConsumerRecord<String, ReportView> actual = reportConsumer.getLastReportRecord();

        assertThat(actual).isNull();
    }
}
