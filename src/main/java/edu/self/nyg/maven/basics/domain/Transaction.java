package edu.self.nyg.maven.basics.domain;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Transaction {

    @NonNull
    private final String currency;

    @NonNull
    private final BigDecimal amount;

    @Default
    private final TransactionStatus status = TransactionStatus.NEW;

    public Transaction validated() {

        if (status != TransactionStatus.NEW) {
            throw new IllegalStateException("Transaction is not NEW");
        }

        return transitSuccess();
    }

    public Transaction rejected() {

        if (status != TransactionStatus.NEW) {
            throw new IllegalStateException("Transaction is not NEW");
        }

        return transitFailure();
    }

    public Transaction executed() {

        if (status != TransactionStatus.VALIDATED) {
            throw new IllegalStateException("Transaction is not VALIDATED");
        }

        return transitSuccess();
    }

    private Transaction transitSuccess() {
        return toBuilder().status(status.nextSuccess()).build();
    }

    private Transaction transitFailure() {
        return toBuilder().status(status.nextFailure()).build();
    }
}
