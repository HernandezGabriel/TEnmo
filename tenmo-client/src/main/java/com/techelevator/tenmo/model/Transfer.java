package com.techelevator.tenmo.model;

public class Transfer {

    int transferId;
//    int transferStatusId;
//    int transferTypeId;

    TransferStatus transferStatus;
    TransferType transferType;
    int accountFrom;
    int accountTo;
    long amount;

    public Transfer() {
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
//
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
//        public int getTransferTypeId() {
//            return transferTypeId;
//        }
//
//        public void setTransferTypeId(int transferTypeId) {
//            this.transferTypeId = transferTypeId;
//        }
//
//        @Override
//        public String toString() {
//            return "Transfer{" +
//                    "transferId=" + transferId +
//                    ", transferStatusId=" + transferStatusId +
//                    ", transferTypeId=" + transferTypeId +
//                    ", accountFrom=" + accountFrom +
//                    ", accountTo=" + accountTo +
//                    ", amount=" + amount +
//                '}';
//    }
//
//    public Transfer(int transferId, int transferStatusId, int transferTypeId, int accountFrom, int accountTo, long amount) {
//        this.transferId = transferId;
//        this.transferStatusId = transferStatusId;
//        this.transferTypeId = transferTypeId;
//        this.accountFrom = accountFrom;
//        this.accountTo = accountTo;
//        this.amount = amount;
//    }

}



