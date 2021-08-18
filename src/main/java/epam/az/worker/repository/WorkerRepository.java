package epam.az.worker.repository;

import epam.az.worker.domain.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Page<Worker> findAll(Pageable pageable);

    List<Worker> findAllByIsActiveTrue();
}
