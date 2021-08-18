package epam.az.worker.service;

import epam.az.worker.domain.Worker;
import epam.az.worker.dto.ReportForm;
import epam.az.worker.dto.ReportView;
import epam.az.worker.dto.WorkerForm;
import epam.az.worker.dto.WorkerView;
import epam.az.worker.error.ErrorCode;
import epam.az.worker.error.ServiceErrorCode;
import epam.az.worker.exception.DateFormatException;
import epam.az.worker.exception.NotFoundException;
import epam.az.worker.repository.WorkerRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.*;
import org.springframework.data.redis.connection.SortParameters;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceImplTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Long WORKER_ID = 1L;

    @Spy
    @InjectMocks
    private WorkerServiceImpl workerService;

    @Mock
    private Worker worker;

    @Mock
    private WorkerForm workerForm;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private KafkaReportProducerServiceImpl reportViewKafkaProducerService;

    @Mock
    private KafkaWorkersProducerServiceImpl workerViewKafkaProducerService;

    @Mock
    private ConversionService conversionService;

    @Mock
    private Pageable pageable;

    @Mock
    private WorkerView workerView;

    @Test
    void save_workerIsProvided_workerIsSaved() {

        // GIVEN
        Mockito.when(workerRepository.save(worker)).thenReturn(worker);
        Mockito.when(conversionService.convert(workerForm, Worker.class)).thenReturn(worker);

        // WHEN
        Worker actual = workerService.save(workerForm);

        // THEN
        Mockito.verify(workerRepository).save(worker);
        assertThat(actual).isEqualTo(worker);
    }

    @Test
    void getById_workerExists_workerIsReceived() {

        // GIVEN
        Mockito.when(workerRepository.findById(WORKER_ID)).thenReturn(Optional.of(worker));

        // WHEN
        Worker actual = workerService.getById(WORKER_ID);

        // THEN
        Mockito.verify(workerRepository).findById(WORKER_ID);
        assertThat(actual).isEqualTo(worker);
    }

    @Test
    void getById_workerDoesNotExist_notFoundExceptionIsThrown() {

        // GIVEN
        ErrorCode errorCode = ServiceErrorCode.WORKER_NOT_FOUND;

        // WHEN
        Throwable throwable = catchThrowable(() -> workerService.getById(WORKER_ID));

        // THEN
        assertThat(throwable).isInstanceOf(NotFoundException.class);
        NotFoundException notFoundException = (NotFoundException) throwable;
        assertThat(notFoundException.getErrorCode()).isEqualTo(errorCode);
        assertThat(notFoundException.getValues()).contains(WORKER_ID);
    }

    @Test
    void reportTime_reportFormIsCorrect_reportIsSentAndViewIsReceived() {

        // GIVEN
        String dateString = "23/05/2021";
        String workDescription = "coding";
        Integer hours = 8;
        String timezone = "Europe/Paris";

        ReportForm reportForm = ReportForm.builder()
                .workerId(WORKER_ID)
                .date(dateString)
                .description(workDescription)
                .hours(hours)
                .build();

        Mockito.when(workerRepository.findById(WORKER_ID)).thenReturn(Optional.of(worker));
        Mockito.when(worker.getZone()).thenReturn(timezone);
        LocalDate date = LocalDate.parse(dateString, FORMATTER);
        ReportView expectedView = ReportView.builder()
                .workerId(WORKER_ID)
                .workDescription(workDescription)
                .hours(hours)
                .date(date)
                .build();

        // WHEN
        ReportView actual = workerService.reportTime(reportForm);

        // THEN
        Mockito.verify(reportViewKafkaProducerService).send(actual);
        assertThat(actual).isEqualTo(expectedView);
    }

    @Test
    void reportTime_dateIsInvalid_dateFormatExceptionIsThrown() {

        // GIVEN
        String invalidDateString = "2333522021";
        String workDescription = "coding";
        Integer hours = 8;

        ErrorCode errorCode = ServiceErrorCode.INVALID_DATE;

        ReportForm reportForm = ReportForm.builder()
                .workerId(WORKER_ID)
                .date(invalidDateString)
                .description(workDescription)
                .hours(hours)
                .build();

        Mockito.when(workerRepository.findById(WORKER_ID)).thenReturn(Optional.of(worker));

        // WHEN
        Throwable throwable = catchThrowable(() -> workerService.reportTime(reportForm));

        // THEN
        assertThat(throwable).isInstanceOf(DateFormatException.class);
        DateFormatException dateFormatException = (DateFormatException) throwable;
        assertThat(dateFormatException.getErrorCode()).isEqualTo(errorCode);
        assertThat(dateFormatException.getValues()).contains(invalidDateString);
    }

    @Test
    void getAllWorkers_pageableIsProvided_pageIsReturned() {

        // GIVEN
        Worker secondWorker = Mockito.mock(Worker.class);
        Page<Worker> expectedPage = new PageImpl<>(List.of(worker, secondWorker));
        Mockito.when(workerRepository.findAll(pageable)).thenReturn(expectedPage);

        // WHEN
        Page<Worker> actual = workerService.getAllWorkers(pageable);

        // THEN
        assertThat(actual.getContent()).isEqualTo(expectedPage.getContent());
    }

    @Test
    void exportActiveWorkers_workersExist_kafkaMessagesAreSent() {

        // GIVEN
        Mockito.when(workerRepository.findAllByIsActiveTrue()).thenReturn(List.of(worker));
        Mockito.when(conversionService.convert(worker, WorkerView.class)).thenReturn(workerView);

        // WHEN
        workerService.exportActiveWorkers();

        // THEN
        Mockito.verify(workerRepository).findAllByIsActiveTrue();
        Mockito.verify(workerViewKafkaProducerService).send(workerView);
    }
}
