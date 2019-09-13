package com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction;

public class TransferOutPreparationConfirmedEvent extends AbstractTransferTransactionEvent {
    public TransferOutPreparationConfirmedEvent() {
    }

    public TransferOutPreparationConfirmedEvent(TransferTransactionInfo transactionInfo) {
        super(transactionInfo);
    }
}
