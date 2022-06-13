package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findAccountByAccountId(int accountId);
    Account findAccountByUserId(int userId);

    @Query(value="SElECT account_id from account WHERE user_id = ?1", nativeQuery = true)
    int findAccountIdByUserId(int userId);

    //not working from just name
    @Query(value = "SELECT user_id from account where account_id = ?1", nativeQuery = true)
    int findUserIdByAccountId(int accountId);

    @Modifying
    @Query(value = "UPDATE account SET balance = ?1 WHERE account_id = ?2 ;", nativeQuery = true)
    void changeAmount(long amount, int id);


//    @Transactional
//    @Query(
//            "Update account set balance = balance-?1 where account_id = ?2;" +
//            "Update account set balance = balance+?1 where account_id = ?3;")
//    void transferAmountFromAToB(long amount, int idA, int idB){}


}
