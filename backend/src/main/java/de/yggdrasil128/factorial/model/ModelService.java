package de.yggdrasil128.factorial.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ModelService<E, R extends CrudRepository<E, Integer>> {

    protected final R repository;

    protected ModelService(R repository) {
        this.repository = repository;
    }

    public E get(int id) {
        // TODO find out how to report more info
        // we should establish something custom here, so we can handle proprietary stuff, like model conflicts transparently
        return repository.findById(id).orElseThrow(ModelService::reportNotFound);
    }

    protected static ResponseStatusException reportNotFound() {
        return new ResponseStatusException(NOT_FOUND);
    }

    public List<E> get(List<Integer> ids) {
        return StreamSupport.stream(repository.findAllById(ids).spliterator(), false).toList();
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

}
