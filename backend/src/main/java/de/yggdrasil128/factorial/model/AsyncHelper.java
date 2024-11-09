package de.yggdrasil128.factorial.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class AsyncHelper {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncHelper.class);

    private final ExecutorService taskExecutor;

    @Autowired
    public AsyncHelper(ExecutorService taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public <T> CompletableFuture<T> submit(Supplier<T> code) {
        return CompletableFuture.supplyAsync(code, taskExecutor);
    }

    public <T> CompletableFuture<T> submit(Consumer<CompletableFuture<T>> code) {
        CompletableFuture<T> result = new CompletableFuture<>();
        taskExecutor.submit(() -> {
            try {
                code.accept(result);
                // if the given code forgot to do this
                if (result.complete(null)) {
                    LOG.warn("Service method did not mark future result as completed, code: {}", code);
                }
            } catch (RuntimeException exc) {
                result.completeExceptionally(exc);
            }
        });
        return result;
    }

    public static void complete(CompletableFuture<?> future) {
        future.complete(null);
    }

}
