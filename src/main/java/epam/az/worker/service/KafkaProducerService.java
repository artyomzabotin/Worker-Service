package epam.az.worker.service;

public interface KafkaProducerService<T> {

    void send(T entity);
}
