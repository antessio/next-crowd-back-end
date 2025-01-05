package nextcrowd.crowdfunding.infrastructure.transaction;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import nextcrowd.crowdfunding.common.TransactionalManager;


@Component
public class SpringTransactionAdapter implements TransactionalManager {

    private final TransactionTemplate transactionTemplate;

    public SpringTransactionAdapter(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void executeInTransaction(Runnable runnable) {
        transactionTemplate.executeWithoutResult(_ -> runnable.run());

    }

    @Override
    public <T> T executeInTransaction(Supplier<T> supplier) {
        return transactionTemplate.execute(_ -> supplier.get());
    }

}
