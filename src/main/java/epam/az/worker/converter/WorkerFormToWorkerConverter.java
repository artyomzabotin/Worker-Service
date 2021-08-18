package epam.az.worker.converter;

import epam.az.worker.domain.Worker;
import epam.az.worker.dto.WorkerForm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkerFormToWorkerConverter implements Converter<WorkerForm, Worker> {

    private final ModelMapper modelMapper;

    @Override
    public Worker convert(@NonNull WorkerForm workerForm) {

        return modelMapper.map(workerForm, Worker.class);
    }
}
