package com.techelevator.tenmo.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    int transferId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "transfer_status_id", nullable = false)
    TransferStatus transferStatus;

    @NotNull
    @OneToOne
    @JoinColumn(name = "transfer_type_id", nullable = false)
    TransferType transferType;

    @NotNull
    @OneToOne
    @JoinColumn(nullable = false, name="account_from")//,referencedColumnName = "account_id")
    Account accountFrom;

    @NotNull
    @OneToOne
    @JoinColumn(nullable = false, name="account_to" )
    Account accountTo;

    @NotNull
    long amount;

    public Transfer() {
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", transferStatus=" + transferStatus +
                ", transferType=" + transferType +
                ", accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                ", amount=" + amount +
                '}';
    }

    public Transfer(int transferId, TransferStatus transferStatus, TransferType transferType, Account accountFrom, Account accountTo, long amount) {
        this.transferId = transferId;
        this.transferStatus = transferStatus;
        this.transferType = transferType;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public TransferStatus getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}


