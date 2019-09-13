package com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction;

public class TransferInConfirmedEvent extends AbstractTransferTransactionEvent {
    public TransferInConfirmedEvent() {
    }

    public TransferInConfirmedEvent(TransferTransactionInfo transactionInfo) {
        super(transactionInfo);
    }
}
