package de.yggdrasil128.factorial.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A service helper for synchronization.
 * <p>
 * In Factorial we use services, as common, for representing our business logic. One major aspect of this is the caching
 * of <i>engine objects</i>, that being instances of classes from the {@code engine} package. Generally, these group
 * information together for which we otherwise would have to consult a multitude of tables. By making these live across
 * sessions, we save a lot of database interaction.
 * <p>
 * However, we now can no longer rely on the database or hibernate to guarantee a transactional behavior of our backing
 * model, because simply put, the engine objects might be out-of-sync with the persistent entities. Therefore, we need
 * some kind of synchronization at which point we decided to simply have all service operations run in a single thread.
 * After all, we are not trying to improve throughputs but only single response times. In many cases, a server will even
 * serve only a single client.
 * <p>
 * The synchronization itself is realized by a single {@code ExecutorService taskExecutor} being declared as
 * {@link Bean} and used globally. This class {@link Autowired autowires} that very bean and offers further utility.
 */
@Service
public class AsyncHelper {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncHelper.class);

    private final ExecutorService taskExecutor;

    @Autowired
    public AsyncHelper(ExecutorService taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Submits a piece of code for execution in the service thread.
     * <p>
     * In contrast to {@link #submit(Consumer)}, this returned {@link CompletableFuture} will only be completed after
     * the code has run completely, as we are retrieving a value from it.
     * 
     * @param <T> the generic type of the returned {@link CompletableFuture CompletableFuture's} content
     * @param code the code to execute
     * @return a {@link CompletableFuture} with the executed code's result
     */
    public <T> CompletableFuture<T> submit(Supplier<T> code) {
        return CompletableFuture.supplyAsync(code, taskExecutor);
    }

    /**
     * Submits a piece of code for execution in the service thread.
     * <p>
     * This is a little different from how task are usually dispatched to an executor service in that the returned
     * {@link CompletableFuture} will be completed after a portion of the given code is executed. The exact amount is up
     * to the code itself, but the general contract is that it should be able to report either precondition errors or a
     * likely success in which case it will continue to process asynchronously.
     * <p>
     * To accomplish this, the given code must either
     * <ul>
     * <li>throw a {@link RuntimeException} to indicate failure or</li>
     * <li>call {@link #complete(CompletableFuture)} with the {@link CompletableFuture} given to the {@link Consumer}
     * invocation to indicate partial completion an likely success</li>
     * </ul>
     * 
     * @param <T> the generic type of the returned {@link CompletableFuture CompletableFuture's} content
     * @param code the code to execute
     * @return a {@link CompletableFuture} describing the partial completion of the given code
     */
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

    /**
     * Signals the partial completion to the given {@link CompletableFuture}.
     * 
     * @param future the given {@link CompletableFuture}
     * @see #submit(Consumer)
     */
    public static void complete(CompletableFuture<?> future) {
        future.complete(null);
    }

}
