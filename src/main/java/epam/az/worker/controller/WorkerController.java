package epam.az.worker.controller;

import epam.az.worker.domain.Worker;
import epam.az.worker.dto.ReportForm;
import epam.az.worker.dto.ReportView;
import epam.az.worker.dto.WorkerForm;
import epam.az.worker.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workers")
public class WorkerController extends BaseController {

    private final WorkerService workerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveWorker(@Valid @RequestBody WorkerForm workerForm, HttpServletResponse response) {

        Worker created = workerService.save(workerForm);
        setLocationHeaderForResource(response, created.getId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Worker> getAllWorkers(@PageableDefault(sort = "fullName", direction = Sort.Direction.ASC, size = 50)
                                                  Pageable pageable) {

        return workerService.getAllWorkers(pageable);
    }

    @GetMapping("/export")
    @ResponseStatus(HttpStatus.OK)
    public void exportActiveWorkers() {

        workerService.exportActiveWorkers();
    }

    @GetMapping("/{id}")
    public Worker getById(@PathVariable("id") Long workerId) {

        return workerService.getById(workerId);
    }

    @PostMapping("/time")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ReportView reportTime(@Valid @RequestBody ReportForm reportForm) {

        return workerService.reportTime(reportForm);
    }
}
