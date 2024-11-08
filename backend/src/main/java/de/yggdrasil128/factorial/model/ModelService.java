package de.yggdrasil128.factorial.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public abstract class ModelService<E, R extends CrudRepository<E, Integer>> {

    protected final R repository;

    protected ModelService(R repository) {
        this.repository = repository;
    }

    protected E create(E entity) {
        return repository.save(entity);
    }

    public Stream<E> stream() {
        return StreamSupport.stream(repository.findAll().spliterator(), false);
    }

    public E get(int id) {
        return repository.findById(id).orElseThrow(ModelService::reportNotFound);
    }

    protected E update(E entity) {
        return repository.save(entity);
    }

    protected void delete(int id) {
        repository.deleteById(id);
    }

    protected static ResponseStatusException reportNotFound() {
        return report(NOT_FOUND, "");
    }

    public static ResponseStatusException report(HttpStatus status, String message) {
        // TODO find out how to report more info
        // we should establish something custom here, so we can handle proprietary stuff, like model conflicts
        // transparently
        ResponseStatusException response = new ResponseStatusException(status, message);
        response.printStackTrace();
        return response;
    }
}
