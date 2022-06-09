package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {

      List<Transfer> findAll();
//    TransferRepository getByTransferId(int id);
//    boolean createTransfer(TransferRepository newTransfer);
//    boolean updateTransferStatus(int id, TransferStatus ts);
//
//    TransferRepository getTransferByToAccountId(int id);
//    TransferRepository getTransferByFromAccountId(int id);
//
//    long getAmountByTransferId(int id);
}