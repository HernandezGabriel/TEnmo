package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AccountRepository extends JpaRepository<Account, Integer> {
//
//    List<Account> findAll();
//
    Account findAccountByAccountId(int accountId);

    Account findAccountByUserId(int userId);
   // Integer findAccountIdByUserId(int userId);

//    @Modifying
//    @Transactional
//    @Query(
//            "Update account set balance = balance-?1 where account_id = ?2;" +
//            "Update account set balance = balance+?1 where account_id = ?3;")
//    void transferAmountFromAToB(long amount, int idA, int idB){}
////
    @Modifying
    @Query(value = "UPDATE account SET balance = ?1 WHERE account_id = ?2 ;", nativeQuery = true)
    void changeAmount(long amount, int id);
//    String sql = "UPDATE account SET amount = ? WHERE id = ?";
//    jdbc.update(sql, amount, id);
//}
//    boolean addBalance(Account account, long amount);
//
//    boolean subtractBalance(Account account, long amount);


}
