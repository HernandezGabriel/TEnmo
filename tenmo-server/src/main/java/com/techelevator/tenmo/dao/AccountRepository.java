package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Integer> {
//
//    List<Account> findAll();
//
//    Account findByAccountId(int accountId);

    Account findIdByUserId(int userId);

//    boolean createAccount(int userId);
//
//    boolean addBalance(Account account, long amount);
//
//    boolean subtractBalance(Account account, long amount);


}
