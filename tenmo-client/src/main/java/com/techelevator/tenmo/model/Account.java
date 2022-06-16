package com.techelevator.tenmo.model;

public class Account {

    int accountId;
    User user;

    public Account() {
    }

    public Account(int accountId, User user){
        this.accountId = accountId;
        this.user=user;
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

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", user=" + user +
//                ", balance=" + balance +
                '}';
    }
}

