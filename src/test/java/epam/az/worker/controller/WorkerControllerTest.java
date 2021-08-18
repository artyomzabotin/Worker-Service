package epam.az.worker.controller;

import epam.az.worker.domain.Worker;
import epam.az.worker.dto.ReportForm;
import epam.az.worker.dto.WorkerForm;
import epam.az.worker.service.WorkerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import javax.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class WorkerControllerTest {

    private static final Long WORKER_ID = 1L;

    @Mock
    private WorkerService workerService;

    @Spy
    @InjectMocks
    private WorkerController workerController;

    @Mock
    private WorkerForm workerForm;

    @Mock
    private Worker worker;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Pageable pageable;

    @Mock
    private ReportForm reportForm;


    @Test
    void saveWorker_workerIsProvided_callIsProxiedToWorkerService() {

        // GIVEN
        Mockito.when(workerService.save(workerForm)).thenReturn(worker);
        Mockito.doNothing().when(workerController).setLocationHeaderForResource(Mockito.eq(response), Mockito.anyLong());

        // WHEN
        workerController.saveWorker(workerForm, response);

        // THEN
        Mockito.verify(workerService).save(workerForm);
    }

    @Test
    void getAllWorkers_pageableIsProvided_callIsProxiedToWorkerService() {

        // WHEN
        workerController.getAllWorkers(pageable);

        // THEN
        Mockito.verify(workerService).getAllWorkers(pageable);
    }

    @Test
    void exportActiveWorkers_anyState_callIsProxiedToWorkerService() {

        // WHEN
        workerController.exportActiveWorkers();

        // THEN
        Mockito.verify(workerService).exportActiveWorkers();
    }

    @Test
    void getById_idIsProvided_callIsProxiedToWorkerService() {

        // WHEN
        workerController.getById(WORKER_ID);

        // THEN
        Mockito.verify(workerService).getById(WORKER_ID);
    }

    @Test
    void reportTime_reportFormIsProvided_callIsProxiedToWorkerService() {

        // WHEN
        workerController.reportTime(reportForm);

        // THEN
        Mockito.verify(workerService).reportTime(reportForm);
    }
}
