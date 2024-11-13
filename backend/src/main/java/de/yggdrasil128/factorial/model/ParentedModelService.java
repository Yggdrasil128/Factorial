package de.yggdrasil128.factorial.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public abstract class ParentedModelService<E, S, P, R extends CrudRepository<E, Integer>>
        extends ModelService<E, S, R> {

    protected ParentedModelService(R repository) {
        super(repository);
    }

    @Transactional
    public void create(int parentId, List<S> standalones, CompletableFuture<Void> result) {
        P parent = getParentEntity(parentId);
        List<E> entities = new ArrayList<>();
        for (S standalone : standalones) {
            entities.add(prepareCreate(parent, standalone));
        }
        AsyncHelper.complete(result);
        handleBulkCreate(parent, repository.saveAll(entities));
    }

    protected abstract P getParentEntity(int parentId);

    protected abstract E prepareCreate(P parent, S standalone);

    protected abstract void handleBulkCreate(P parent, Iterable<E> entities);

    @Transactional
    public void delete(List<Integer> ids, CompletableFuture<Void> result) {
        Map<Integer, P> parents = ids.stream().collect(toMap(Function.identity(), this::findParentEntity));
        AsyncHelper.complete(result);
        repository.deleteAllById(ids);
        for (Integer id : ids) {
            handleDelete(parents.get(id), id);
        }
    }

    protected abstract P findParentEntity(int id);

    protected abstract void handleDelete(P parent, int id);

}
