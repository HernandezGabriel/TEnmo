package com.techelevator.tenmo.model;

public class TransferStatus {

    int transferStatusId;
    String transferStatusDesc;

    public TransferStatus() {
    }

    public TransferStatus(int transferStatusId, String transferStatusDesc) {
        this.transferStatusId = transferStatusId;
        this.transferStatusDesc = transferStatusDesc;
    }

    public TransferStatus(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    @Override
    public String toString() {
        return "TransferStatus{" +
                "TransferStatusId=" + transferStatusId +
                ", transferStatusDesc='" + transferStatusDesc + '\'' +
                '}';
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public String getTransferStatusDesc() {
        return transferStatusDesc;
    }

    public void setTransferStatusDesc(String transferStatusDesc) {
        this.transferStatusDesc = transferStatusDesc;
    }
}
