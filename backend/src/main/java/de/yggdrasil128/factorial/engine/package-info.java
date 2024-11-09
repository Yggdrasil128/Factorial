/**
 * Contains types that help to calculate the runtime information of Factorial, such as the amount of items produced and
 * consumed in a factory.
 * <p>
 * The types in this package are not meant for storage in a repository, but rather are cached by the respective
 * {@link org.springframework.stereotype.Service Service} implementations. Therefore, these <b>must not</b> keep
 * references to {@link jakarta.persistence.Entity Entity} objects, because those may become stale across multiple
 * transactions (while these cached objects are maintained).
 */
package de.yggdrasil128.factorial.engine;
