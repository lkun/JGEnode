package com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction;

public class TargetAccountValidatePassedConfirmedEvent extends AbstractTransferTransactionEvent {
    public TargetAccountValidatePassedConfirmedEvent() {
    }

    public TargetAccountValidatePassedConfirmedEvent(TransferTransactionInfo transactionInfo) {
        super(transactionInfo);
    }
}
