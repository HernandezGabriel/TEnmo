package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional //in the event of error, it should roll back all changes made
    public boolean transferMoney(int accountIdSender, int accountIdReceiver, long amount) {

        try{
//            Account sender = accountRepository.findAccountByAccountId(accountIdSender);
//            Account receiver = accountRepository.findAccountByAccountId(accountIdReceiver);
//
//            if(accountIdReceiver==accountIdSender){
//                return false;
//            }

            long senderBalance= accountRepository.findBalanceByAccountId(accountIdSender);
            long receiverBalance= accountRepository.findBalanceByAccountId(accountIdReceiver);

            long senderNewAmount = senderBalance - (amount);
            long receiverNewAmount = receiverBalance + (amount);

//            if(senderNewAmount<0){
//                return false;
//            }
            accountRepository.changeAmount( senderNewAmount,accountIdSender);
//            if(senderNewAmount!=accountRepository.findBalanceByAccountId(accountIdSender)){
//                return false;
//            }
            accountRepository.changeAmount( receiverNewAmount, accountIdReceiver);
//            if(receiverNewAmount!=accountRepository.findBalanceByAccountId(accountIdSender)){
//                return false;
//            }


        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }

        return true;

    }
}