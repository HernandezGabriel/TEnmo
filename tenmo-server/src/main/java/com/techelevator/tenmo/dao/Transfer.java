package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferStatus;

import java.util.List;

public interface Transfer {

    List<Transfer> getAll();
    Transfer getByTransferId(int id);
    boolean createTransfer(Transfer newTransfer);
    boolean updateTransferStatus(int id, TransferStatus ts);

    Transfer getTransferByToAccountId(int id);
    Transfer getTransferByFromAccountId(int id);

    long getAmountByTransferId(int id);
}
