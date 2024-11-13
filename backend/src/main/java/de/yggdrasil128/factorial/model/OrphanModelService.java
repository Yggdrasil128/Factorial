package de.yggdrasil128.factorial.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class OrphanModelService<E, S, R extends CrudRepository<E, Integer>> extends ModelService<E, S, R> {

    protected OrphanModelService(R repository) {
        super(repository);
    }

    public Stream<E> stream() {
        return StreamSupport.stream(repository.findAll().spliterator(), false);
    }

    @Transactional
    public void create(List<S> standalones, CompletableFuture<Void> result) {
        List<E> entities = new ArrayList<>();
        for (S standalone : standalones) {
            entities.add(prepareCreate(standalone));
        }
        AsyncHelper.complete(result);
        handleBulkCreate(repository.saveAll(entities));
    }

    protected abstract E prepareCreate(S standalone);

    protected abstract void handleBulkCreate(Iterable<E> entities);

    public void delete(List<Integer> ids, CompletableFuture<Void> result) {
        AsyncHelper.complete(result);
        repository.deleteAllById(ids);
        for (Integer id : ids) {
            handleDelete(id);
        }
    }

    protected abstract void handleDelete(int id);

}
