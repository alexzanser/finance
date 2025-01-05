package com.finances.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь 1:1 c User
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Список транзакций
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    // Список бюджетов по категориям
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets = new ArrayList<>();

    public Wallet() {}

    public Wallet(User user) {
        this.user = user;
    }

    // getters / setters
    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public List<Budget> getBudgets() {
        return budgets;
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
        t.setWallet(this);
    }

    public void addBudget(Budget b) {
        budgets.add(b);
        b.setWallet(this);
    }
}