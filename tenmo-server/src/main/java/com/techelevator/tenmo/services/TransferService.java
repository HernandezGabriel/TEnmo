package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

//    @Autowired
//    private AccountRepository accountRepository;

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transferMoney(int idSender,
                              int idReceiver,
                              long amount) {

        //note being passed user id's not account id's

        Account sender =
                accountRepository.findAccountByAccountId(idSender);
        Account receiver =
                accountRepository.findAccountByAccountId(idReceiver);

        long senderNewAmount =
                sender.getBalance() - (amount);
        long receiverNewAmount =
                receiver.getBalance() + (amount);

        accountRepository
                .changeAmount( senderNewAmount,idSender);

        accountRepository
                .changeAmount( receiverNewAmount, idReceiver);
    }
}