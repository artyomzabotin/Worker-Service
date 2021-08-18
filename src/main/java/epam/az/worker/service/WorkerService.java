package epam.az.worker.service;

import epam.az.worker.domain.Worker;
import epam.az.worker.dto.ReportForm;
import epam.az.worker.dto.ReportView;
import epam.az.worker.dto.WorkerForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkerService {

    Worker save(WorkerForm workerForm);

    Worker getById(Long workerId);

    ReportView reportTime(ReportForm reportForm);

    Page<Worker> getAllWorkers(Pageable pageable);

    void exportActiveWorkers();
}
