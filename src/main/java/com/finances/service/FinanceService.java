package com.finances.service;

import com.finances.exception.ClientException;
import com.finances.model.Budget;
import com.finances.model.Transaction;
import com.finances.model.User;
import com.finances.model.Wallet;
import com.finances.repository.BudgetRepository;
import com.finances.repository.TransactionRepository;
import com.finances.repository.UserRepository;
import com.finances.repository.WalletRepository;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FinanceService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public FinanceService(UserRepository userRepository,
                          WalletRepository walletRepository,
                          TransactionRepository transactionRepository,
                          BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    // Проверка авторизации
    private User authorize(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new ClientException("Неверный логин или пароль!"));
    }

    // Регистрация пользователя
    @Transactional
    public User registerUser(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new ClientException("Логин уже занят!");
        }
        User user = new User(login, password);
        Wallet wallet = new Wallet(user);
        user.setWallet(wallet);

        return userRepository.save(user);
    }

    // Добавление дохода/расхода
    @Transactional
    public Transaction addTransaction(String userLogin, String userPassword, String type, String category, double amount) {
        User user = authorize(userLogin, userPassword);
        Wallet wallet = user.getWallet();

        if ("expense".equalsIgnoreCase(type)) {
            double totalIncome = getTotalIncome(wallet);
            double totalExpenses = getTotalExpenses(wallet) + amount;
            if (totalExpenses > totalIncome) {
                throw new ClientException("Расходы превышают доходы!");
            }

            Budget budget = wallet.getBudgets().stream()
                    .filter(b -> b.getCategory().equalsIgnoreCase(category))
                    .findFirst().orElse(null);
            if (budget != null) {
                double spentAlready = getExpenseByCategory(wallet, category);
                if ((spentAlready + amount) > budget.getAmount()) {
                    throw new ClientException("Лимит по категории \"" + category + "\" будет превышен!");
                }
            }
        }

        Transaction tx = new Transaction(type, category, amount);
        wallet.addTransaction(tx);
        walletRepository.save(wallet);
        return tx;
    }

    // Установить бюджет
    @Transactional
    public Budget setBudget(String userLogin, String userPassword, String category, double limit) {
        User user = authorize(userLogin, userPassword);
        Wallet wallet = user.getWallet();

        Optional<Budget> existing = wallet.getBudgets().stream()
                .filter(b -> b.getCategory().equalsIgnoreCase(category))
                .findFirst();

        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setAmount(limit);
        } else {
            budget = new Budget(category, limit);
            wallet.addBudget(budget);
        }

        walletRepository.save(wallet);
        return budget;
    }

    // Перевод между пользователями
    @Transactional
    public void transfer(String userLogin, String userPassword, String toLogin, double amount) {
        User fromUser = authorize(userLogin, userPassword);
        User toUser = userRepository.findByLogin(toLogin)
                .orElseThrow(() -> new ClientException("Получатель не найден."));

        double balance = getTotalIncome(fromUser.getWallet()) - getTotalExpenses(fromUser.getWallet());
        if (amount > balance) {
            throw new ClientException("Недостаточно средств для перевода!");
        }

        Transaction expenseTx = new Transaction("expense", "Перевод->" + toUser.getLogin(), amount);
        fromUser.getWallet().addTransaction(expenseTx);

        Transaction incomeTx = new Transaction("income", "Перевод от " + fromUser.getLogin(), amount);
        toUser.getWallet().addTransaction(incomeTx);

        walletRepository.save(fromUser.getWallet());
        walletRepository.save(toUser.getWallet());
    }

    // Получение статистики
    public String getStats(String userLogin, String userPassword) {
        User user = authorize(userLogin, userPassword);
        Wallet wallet = user.getWallet();

        double totalIncome = getTotalIncome(wallet);
        double totalExpenses = getTotalExpenses(wallet);

        // Create a map to hold the response data
        Map<String, Object> response = new HashMap<>();
        response.put("user", user.getLogin());
        response.put("totalIncome", totalIncome);
        response.put("totalExpenses", totalExpenses);

        // Income by category
        Map<String, Double> incomeByCategory = new HashMap<>();
        wallet.getTransactions().stream()
                .filter(t -> "income".equalsIgnoreCase(t.getType()))
                .forEach(t -> incomeByCategory.put(t.getCategory(), t.getAmount()));
        response.put("incomeByCategory", incomeByCategory);

        // Budgets information
        Map<String, Object> budgets = new HashMap<>();
        for (Budget b : wallet.getBudgets()) {
            double spent = getExpenseByCategory(wallet, b.getCategory());
            double remaining = b.getAmount() - spent;
            Map<String, Double> budgetInfo = new HashMap<>();
            budgetInfo.put("limit", b.getAmount());
            budgetInfo.put("spent", spent);
            budgetInfo.put("remaining", remaining);
            budgets.put(b.getCategory(), budgetInfo);
        }
        response.put("budgets", budgets);

        // Convert the map to a JSON string using Gson
        Gson gson = new Gson();
        return gson.toJson(response);
    }

    private double getTotalIncome(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> "income".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private double getTotalExpenses(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private double getExpenseByCategory(Wallet wallet, String category) {
        return wallet.getTransactions().stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getType()))
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
