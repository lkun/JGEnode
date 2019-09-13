package com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction;

public class TransferTransactionStartedEvent extends AbstractTransferTransactionEvent {
    public TransferTransactionStartedEvent() {
    }

    public TransferTransactionStartedEvent(TransferTransactionInfo transactionInfo) {
        super(transactionInfo);
    }
}
