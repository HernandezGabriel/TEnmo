package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
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
    @Autowired
    private TransferStatusRepository transferStatusRepository;
    @Autowired
    private TransferTypeRepository transferTypeRepository;

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

//TODO NOT WORKING
    //needs to be converted to better handle different types of transfers such as creating vs requesting!
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/InitTransfer")
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer){
//
//        if(transfer.equals(null)){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
//        else if()

        //if(transfer type = SEND) //sending money
        //      check account from = principal username
            //  process transfer of funds immediatly
        //add to transfer db a transfer with status completed

        //if(transfer type = REQUEST) // making a request
        // check account to = principal username
        // post it to db as pending? transfer type of request pending
        //

        //receive transfer //updating a pending request that belongs to you from pending to approved ->processing
        //if(type = REQUEST , status = pending, check account from == yours, account to != yours, transfer has to already exist based on id
        //new transfer has to have new status of either approved or denied not pending!


        System.out.println(transfer);
        //using TransferService to transfer money between 2 accounts
        boolean transferSuccessful = transferService.transferMoney(transfer.getAccountFrom().getAccountId(),transfer.getAccountTo().getAccountId(),transfer.getAmount());
       // boolean transferSuccessful = transferService.transferMoney(transfer.getAccountFrom(),transfer.getAccountTo(),transfer.getAmount());

        if (!transferSuccessful) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer Init failed.");
        }
        else{
          // set transfer status id to approved
           // transfer.setTransferStatusId(2); //2==approved
            TransferStatus transferStatus = new TransferStatus();
            transferStatus.setTransferStatusId(2);
            transfer.setTransferStatus(transferStatus);

        }

        System.out.println("fund transfer fine ");



        Transfer t = transferRepository.save(transfer);


        //for some reason it returns t with status and type description as null, but it does update id
        System.out.println(t);
        return t;

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
