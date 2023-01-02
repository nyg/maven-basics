package edu.self.nyg.maven.basics;

import java.math.BigDecimal;

import edu.self.nyg.maven.basics.domain.Transaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-4s] %5$s%n");
    }

    public static void main(String[] args) {

        App app = new App();

        Transaction tx = app.createTransaction("CHF", "0.1");
        log.info("New transaction: {}", tx);

        Transaction validatedTx = app.validate(tx);
        log.info("Validated transaction: {}", validatedTx);

        Transaction executedTx = app.execute(validatedTx);
        log.info("Executed transaction: {}", executedTx);
    }

    public Transaction createTransaction(String currency, String amount) {
        return Transaction.builder()
                .currency(currency)
                .amount(new BigDecimal(amount))
                .build();
    }

    public Transaction validate(Transaction transaction) {
        return transaction.validated();
    }

    public Transaction execute(Transaction transaction) {
        return transaction.executed();
    }
}
