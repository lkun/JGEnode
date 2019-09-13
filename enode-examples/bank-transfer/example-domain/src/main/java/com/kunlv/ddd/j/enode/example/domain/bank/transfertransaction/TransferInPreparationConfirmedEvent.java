package com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction;

public class TransferInPreparationConfirmedEvent extends AbstractTransferTransactionEvent {
    public TransferInPreparationConfirmedEvent() {
    }

    public TransferInPreparationConfirmedEvent(TransferTransactionInfo transactionInfo) {
        super(transactionInfo);
    }
}
