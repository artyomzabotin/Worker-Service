package epam.az.worker.integration;

import com.jayway.restassured.RestAssured;
import epam.az.worker.WorkerServiceApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest (
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = WorkerServiceApplication.class)
@DirtiesContext
@ActiveProfiles("integration-test")
abstract class IntegrationTest {

    @LocalServerPort
    private int port;

    void setUp() {

        RestAssured.port = this.port;
    }
}
