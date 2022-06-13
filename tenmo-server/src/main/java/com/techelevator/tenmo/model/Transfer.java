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
    @JoinColumn(name="transfer_status_id", nullable=false)
    TransferStatus transferStatus;
    //int transferTypeId;
    @NotNull
    @OneToOne
    @JoinColumn(name="transfer_type_id", nullable=false)
    TransferType transferType;
    //int transferStatusId;
    @NotNull
    int accountFrom;
    @NotNull
    int accountTo;
    @NotNull
    long amount;

    public Transfer() {
    }

    public String getTransferStatusDesc(){
        return transferStatus.transferStatusDesc;
    }

    public int getTransferStatusId(){
        return transferStatus.transferStatusId;
    }
    public String getTransferTypeDesc(){
        return transferType.transferTypeDesc;
    }

    public int getTransferTypeId(){
        return transferType.transferTypeId;
    }

    public Transfer(int transferId, TransferStatus transferStatus, TransferType transferType, int accountFrom, int accountTo, long amount) {
        this.transferId = transferId;
        this.transferStatus = transferStatus;
        this.transferType = transferType;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
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

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

//    public int getTransferStatusId() {
//        return transferStatusId;
//    }

//    public void setTransferStatusId(int transferStatusId) {
//        this.transferStatusId = transferStatusId;
//    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

//    public int getTransferTypeId() {
//        return transferTypeId;
//    }
//
//    public void setTransferTypeId(int transferTypeId) {
//        this.transferTypeId = transferTypeId;
//    }

//    public Transfer(int transferId, int transferTypeId, int transferStatusId, int accountFrom, int accountTo, long amount) {
//        this.transferId = transferId;
//        this.transferTypeId = transferTypeId;
//        this.transferStatusId = transferStatusId;
//        this.accountFrom = accountFrom;
//        this.accountTo = accountTo;
//        this.amount = amount;
//    }
//
//    @Override
//    public String toString() {
//        return "Transfer{" +
//                "transferId=" + transferId +
//                ", transferTypeId=" + transferTypeId +
//                ", transferStatusId=" + transferStatusId +
//                ", accountFrom=" + accountFrom +
//                ", accountTo=" + accountTo +
//                ", amount=" + amount +
//                '}';
//    }
}
