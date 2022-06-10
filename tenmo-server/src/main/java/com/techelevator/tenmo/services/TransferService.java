package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional //in the event of error, it should roll back all changes made
    public boolean transferMoney(int accountIdSender,
                              int accountIdReceiver,
                              long amount) {

        try{
            Account sender = accountRepository.findAccountByAccountId(accountIdSender);
            Account receiver = accountRepository.findAccountByAccountId(accountIdReceiver);

            long senderNewAmount = sender.getBalance() - (amount);
            long receiverNewAmount = receiver.getBalance() + (amount);

            accountRepository.changeAmount( senderNewAmount,accountIdSender);
            accountRepository.changeAmount( receiverNewAmount, accountIdReceiver);

        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }

        return true;

    }
}