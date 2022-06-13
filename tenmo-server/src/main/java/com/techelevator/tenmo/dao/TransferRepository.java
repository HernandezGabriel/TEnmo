package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {

      @Query(value = "SELECT * FROM transfer" +
              " WHERE " +
              "(account_from = (Select account_id from account where user_id = (Select user_id from tenmo_user where username = ?1 ))) " +
              "OR " +
              "(account_to =(Select account_id from account where user_id = (Select user_id from tenmo_user where username = ?1 ))) ; ", nativeQuery = true)
      List<Transfer> findAllByUsername(String username);

      @Modifying
      Transfer save(Transfer t);

      Transfer findByTransferId(int id);

}
