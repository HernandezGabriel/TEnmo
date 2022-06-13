package com.techelevator.tenmo.model;

public class Account {

    int accountId;
    User user;

    //omitted to avoid getting the info from server
    //long balance;

    public Account() {
    }

    public Account(int accountId, User user){ //, long balance) {
        this.accountId = accountId;
        this.user=user;
        //this.balance = balance ;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

//    public long getBalance() {
//        return balance;
//    }
//
//    public void setBalance(long balance) {
//        this.balance = balance;
//    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", user=" + user +
//                ", balance=" + balance +
                '}';
    }
}

