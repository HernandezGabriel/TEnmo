package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.TransferRepository;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserDao userDao;

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/InitTransfer")
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer){

        //using TransferService to transfer money between 2 accounts
        boolean transferSuccessful = transferService.transferMoney(transfer.getAccountFrom(),transfer.getAccountTo(),transfer.getAmount());

        if (!transferSuccessful) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer Init failed.");
        }
        else{
            //set transfer status id to approved
            transfer.setTransferStatusId(2); //2==approved
        }
        return transferRepository.save(transfer); //return new transfer
//            //using TransferService to transfer money between 2 accounts
//            transferService.transferMoney(transfer.getAccountFrom(),transfer.getAccountTo(),transfer.getAmount());
    }

    @GetMapping("/MyTransfers")
    public List<Transfer> getTransfersByCurrentUser(Principal principal){

        //get userId from principal
        int userId= userDao.findIdByUsername(principal.getName());
        //get accountId from userID
        int accountId = accountRepository.findAccountByUserId(userId).getAccountId();
        //add 2 lists together (findAllByAccountFrom)(findAllByAccountTo)
        List<Transfer> myTransfersList = transferRepository.findAllByAccountFrom(accountId);
        myTransfersList.addAll(transferRepository.findAllByAccountTo(accountId));

        return myTransfersList;
    }

}
