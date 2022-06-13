package com.techelevator.tenmo.model;

public class TransferStatus {

    int TransferStatusId;
    String transferStatusDesc;

    public TransferStatus() {
    }

    public TransferStatus(int transferStatusId, String transferStatusDesc) {
        TransferStatusId = transferStatusId;
        this.transferStatusDesc = transferStatusDesc;
    }

    public TransferStatus(int transferStatusId) {
        TransferStatusId = transferStatusId;
    }

    @Override
    public String toString() {
        return "TransferStatus{" +
                "TransferStatusId=" + TransferStatusId +
                ", transferStatusDesc='" + transferStatusDesc + '\'' +
                '}';
    }

    public int getTransferStatusId() {
        return TransferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        TransferStatusId = transferStatusId;
    }

    public String getTransferStatusDesc() {
        return transferStatusDesc;
    }

    public void setTransferStatusDesc(String transferStatusDesc) {
        this.transferStatusDesc = transferStatusDesc;
    }
}
