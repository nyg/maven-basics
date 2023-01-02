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
    private final BigDecimal quantity;

    @Default
    private final TransactionStatus status = TransactionStatus.NEW;

    public Transaction validated() {

        if (status == TransactionStatus.NEW) {
            return transitSuccess();
        }

        throw new IllegalStateException("Transaction is not NEW");
    }

    public Transaction rejected() {

        if (status == TransactionStatus.NEW) {
            return transiteFailure();
        }

        throw new IllegalStateException("Transaction is not NEW");
    }

    public Transaction executed() {

        if (status == TransactionStatus.VALIDATED) {
            return transitSuccess();
        }

        throw new IllegalStateException("Transaction is not VALIDATED");
    }

    private Transaction transitSuccess() {
        return toBuilder().status(status.nextSuccess()).build();
    }

    private Transaction transiteFailure() {
        return toBuilder().status(status.nextFailure()).build();
    }
}
