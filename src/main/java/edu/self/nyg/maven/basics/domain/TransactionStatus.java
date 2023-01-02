package edu.self.nyg.maven.basics.domain;

public enum TransactionStatus {

    NEW, VALIDATED, REJECTED, EXECUTED;

    private TransactionStatus onSuccess;
    private TransactionStatus onFailure;

    static {
        NEW.onSuccess = VALIDATED;
        NEW.onFailure = REJECTED;

        VALIDATED.onSuccess = EXECUTED;
    }

    public TransactionStatus nextSuccess() {

        if (onSuccess == null) {
            throw new IllegalStateException("Illegal state transition");
        }

        return onSuccess;
    }

    public TransactionStatus nextFailure() {

        if (onFailure == null) {
            throw new IllegalStateException("Illegal state transition");
        }

        return onFailure;
    }
}
