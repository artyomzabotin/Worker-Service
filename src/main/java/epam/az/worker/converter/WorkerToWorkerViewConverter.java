package epam.az.worker.converter;

import epam.az.worker.domain.Worker;
import epam.az.worker.dto.WorkerForm;
import epam.az.worker.dto.WorkerView;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkerToWorkerViewConverter implements Converter<Worker, WorkerView> {

    private final ModelMapper modelMapper;

    @Override
    public WorkerView convert(@NonNull Worker worker) {

        return modelMapper.map(worker, WorkerView.class);
    }
}
