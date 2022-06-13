package com.techelevator.tenmo.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @NotNull(message = "AccountId Cannot be empty")
    int accountId;

    @NotNull
    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    User user;
//    int userId;
//
    @NotNull(message = "balance cannot be negative or empty")
    long balance;

    public Account() {
    }

    public Account(int accountId, User user) {
        this.accountId = accountId;
        this.user = user;
    }

    public Account(int accountId, User user, long balance) {
        this.accountId = accountId;
        this.user=user;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getUserId() {
        return user.getUserId();
    }
    public String getUsername(){return user.getUsername();}


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
                ", userId=" + user.getUserId() +
                ", balance=" + balance +
                '}';
    }
}
