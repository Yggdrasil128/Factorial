package de.yggdrasil128.factorial.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public abstract class ModelService<E, S, R extends CrudRepository<E, Integer>> {

    protected final R repository;

    protected ModelService(R repository) {
        this.repository = repository;
    }

    public E get(int id) {
        return repository.findById(id).orElseThrow(ModelService::reportNotFound);
    }

    protected abstract int getEntityId(E entity);

    protected abstract int getStandaloneId(S standalone);

    @Transactional
    public void update(List<S> standalones, CompletableFuture<Void> result) {
        Map<Integer, E> entities = StreamSupport
                .stream(repository.findAllById(standalones.stream().map(this::getStandaloneId).toList()).spliterator(),
                        false)
                .collect(toMap(this::getEntityId, Function.identity()));
        if (entities.size() != standalones.size()) {
            for (S standalone : standalones) {
                if (!entities.containsKey(getStandaloneId(standalone))) {
                    throw reportNotFound();
                }
            }
        }
        for (S standalone : standalones) {
            prepareUpdate(entities.get(getStandaloneId(standalone)), standalone);
        }
        AsyncHelper.complete(result);
        for (E entity : repository.saveAll(entities.values())) {
            handleUpdate(entity);
        }
    }

    protected abstract void prepareUpdate(E entity, S standalone);

    protected abstract void handleUpdate(E entity);

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
