package epam.az.worker.service;

import epam.az.worker.domain.Worker;
import epam.az.worker.dto.ReportForm;
import epam.az.worker.dto.ReportView;
import epam.az.worker.dto.WorkerForm;
import epam.az.worker.dto.WorkerView;
import epam.az.worker.error.ServiceErrorCode;
import epam.az.worker.exception.DateFormatException;
import epam.az.worker.exception.NotFoundException;
import epam.az.worker.repository.WorkerRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {

    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private final WorkerRepository workerRepository;
    private final KafkaReportProducerServiceImpl reportViewKafkaProducerService;
    private final KafkaWorkersProducerServiceImpl workerViewKafkaProducerService;
    private final ConversionService conversionService;

    @Override
    public Worker save(WorkerForm workerForm) {

        Worker worker = conversionService.convert(workerForm, Worker.class);

        return workerRepository.save(worker);
    }

    @Override
    public Worker getById(Long workerId) {

        return workerRepository.findById(workerId).orElseThrow(() ->
                new NotFoundException(ServiceErrorCode.WORKER_NOT_FOUND, workerId));
    }

    @Override
    public ReportView reportTime(ReportForm reportForm) {

        Long workerId = reportForm.getWorkerId();
        Worker worker = getById(workerId);

        String stringDate = reportForm.getDate();
        if (!GenericValidator.isDate(stringDate, DATE_PATTERN, true)) {
            throw new DateFormatException(ServiceErrorCode.INVALID_DATE, stringDate);
        }

        String workDescription = reportForm.getDescription();

        Integer hours = reportForm.getHours();
        LocalDate date = LocalDate.parse(stringDate, FORMATTER);

        ZoneId zoneId = ZoneId.of(worker.getZone());

        ReportView reportView = ReportView.builder()
                .workerId(workerId)
                .date(date)
                .workDescription(workDescription)
                .hours(hours)
                .createdAt(ZonedDateTime.now(zoneId).toString())
                .build();

        reportViewKafkaProducerService.send(reportView);

        return reportView;
    }

    @Override
    public Page<Worker> getAllWorkers(Pageable page) {

        return workerRepository.findAll(page);
    }

    @Override
    public void exportActiveWorkers() {

        List<Worker> workers = workerRepository.findAllByIsActiveTrue();

            for (Worker worker : workers) {

                WorkerView workerView = conversionService.convert(worker, WorkerView.class);
                workerViewKafkaProducerService.send(workerView);
            }
    }
}
