package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface AccountDao {

    List<Account> findAll();

    Account findByAccountId(int accountId);

    Account findIdByUserId(int userId);

    boolean createAccount(int userId);

    boolean addBalance(Account account, long amount);

    boolean subtractBalance(Account account, long amount);


}
