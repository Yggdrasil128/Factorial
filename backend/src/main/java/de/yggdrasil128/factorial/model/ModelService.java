package de.yggdrasil128.factorial.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ModelService<E, R extends CrudRepository<E, Integer>> {

    protected final R repository;

    protected ModelService(R repository) {
        this.repository = repository;
    }

    public Stream<E> stream() {
        return StreamSupport.stream(repository.findAll().spliterator(), false);
    }

    public E get(int id) {
        return repository.findById(id).orElseThrow(ModelService::reportNotFound);
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

    public List<E> get(List<Integer> ids) {
        return StreamSupport.stream(repository.findAllById(ids).spliterator(), false).toList();
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

}
