package nextcrowd.crowdfunding.project.port;

import java.util.function.Supplier;

public interface TransactionalManager {
    void executeInTransaction(Runnable runnable);
    <T> T executeInTransaction(Supplier<T> supplier);

}
