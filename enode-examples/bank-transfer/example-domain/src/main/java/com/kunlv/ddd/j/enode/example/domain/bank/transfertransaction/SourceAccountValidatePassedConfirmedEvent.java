package com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction;

public class SourceAccountValidatePassedConfirmedEvent extends AbstractTransferTransactionEvent {
    public SourceAccountValidatePassedConfirmedEvent() {
    }

    public SourceAccountValidatePassedConfirmedEvent(TransferTransactionInfo transactionInfo) {
        super(transactionInfo);
    }
}
