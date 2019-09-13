package com.kunlv.ddd.j.enode.example.domain.bank.bankaccount;

public class TransactionPreparationNotExistException extends RuntimeException {
    public TransactionPreparationNotExistException(String accountId, String transactionId) {
        super();
    }
}
