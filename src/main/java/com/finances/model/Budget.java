package com.finances.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private double amount;  // лимит по категории

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    public Budget() {}

    public Budget(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    // getters / setters
    public Long getId() {
        return id;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public Wallet getWallet() {
        return wallet;
    }
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}