package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {
      @Query(value = "Select * from transfer where account_from = ?1 ; ", nativeQuery = true)
      List<Transfer> findAllByAccountFrom(int accountFrom);

      @Query(value = "Select * from transfer where account_to = ?1 ; ", nativeQuery = true)
      List<Transfer> findAllByAccountTo(int accountTo);



      Transfer save(Transfer t);

//    TransferRepository getByTransferId(int id);
//    boolean createTransfer(TransferRepository newTransfer);
//    boolean updateTransferStatus(int id, TransferStatus ts);
//
//    TransferRepository getTransferByToAccountId(int id);
//    TransferRepository getTransferByFromAccountId(int id);
//
//    long getAmountByTransferId(int id);
}
