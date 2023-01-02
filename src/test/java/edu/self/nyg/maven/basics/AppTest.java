package edu.self.nyg.maven.basics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import edu.self.nyg.maven.basics.domain.Transaction;
import edu.self.nyg.maven.basics.domain.TransactionStatus;

class AppTest {

    private final App app = new App();

    @Test
    void testCreateTransaction() {

        // When
        Transaction tx = app.createTransaction();

        // Then
        assertThat(tx.getQuantity()).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.NEW);
    }

    @Test
    void testValidateTransaction() {

        // Given
        Transaction tx = Transaction.builder()
                .quantity(new BigDecimal("0.1"))
                .build();

        // When
        Transaction validatedTx = app.validate(tx);

        // Then
        assertThat(validatedTx.getStatus()).isEqualTo(TransactionStatus.VALIDATED);
    }

    @Test
    void testValidateTransactionWithWrongStatus() {

        // Given
        Transaction tx = Transaction.builder()
                .quantity(new BigDecimal("0.1"))
                .status(TransactionStatus.EXECUTED)
                .build();

        // When
        Throwable thrown = catchThrowable(() -> app.validate(tx));

        // Then
        then(thrown).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testExecuteTransaction() {

        // Given
        Transaction tx = Transaction.builder()
                .quantity(new BigDecimal("0.1"))
                .status(TransactionStatus.VALIDATED)
                .build();

        // When
        Transaction executedTx = app.execute(tx);

        // Then
        assertThat(executedTx.getStatus()).isEqualTo(TransactionStatus.EXECUTED);
    }
}
