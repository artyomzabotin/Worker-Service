package epam.az.worker.integration;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import epam.az.worker.domain.Position;
import epam.az.worker.domain.Worker;
import epam.az.worker.dto.WorkerForm;
import epam.az.worker.helper.UrlParser;
import epam.az.worker.repository.WorkerRepository;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

class WorkerControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private WorkerRepository workerRepository;

    @BeforeEach
    void setUp() {

        super.setUp();
    }

    @Test
    void saveWorker_dataIsProvided_workerIsSaved() {

        // GIVEN
        String fullName = "Artem Zabotin";
        String shop = "Paris";
        String zone = "Europe/Paris";
        Position position = Position.ADMINISTRATOR;
        WorkerForm workerForm = WorkerForm.builder()
                .fullName(fullName)
                .shop(shop)
                .zone(zone)
                .isActive(true)
                .position(position)
                .build();

        // WHEN-THEN
        String location = given()
                .contentType("application/json")
                .when()
                .body(workerForm)
                .post("/workers")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .header("Location");

        Long workerId = UrlParser.getIdFromLocationUrl(location);

        Optional<Worker> optional = workerRepository.findById(workerId);
        assertThat(optional).isPresent();

        Worker created = optional.get();
        assertThat(created)
                .extracting(Worker::getId, Worker::getFullName, Worker::getPosition)
                .containsExactly(workerId, fullName, position);
    }
}
