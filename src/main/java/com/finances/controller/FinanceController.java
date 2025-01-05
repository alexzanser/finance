package com.finances.controller;

import com.finances.model.Budget;
import com.finances.model.Transaction;
import com.finances.model.User;
import com.finances.service.FinanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    // Регистрация нового пользователя
    @PostMapping("/register")
    public User register(@RequestParam String login, @RequestParam String password) {
        return financeService.registerUser(login, password);
    }

    // "Авторизация" (упрощённая, без Spring Security)
    @PostMapping("/login")
    public User login(@RequestParam String login, @RequestParam String password) {
        return financeService.login(login, password);
    }

    // Добавить доход / расход
    @PostMapping("/users/{userId}/transactions")
    public Transaction addTransaction(@PathVariable Long userId,
                                      @RequestParam String type,
                                      @RequestParam String category,
                                      @RequestParam double amount) {
        return financeService.addTransaction(userId, type, category, amount);
    }

    // Установить (обновить) бюджет по категории
    @PostMapping("/users/{userId}/budget")
    public Budget setBudget(@PathVariable Long userId,
                            @RequestParam String category,
                            @RequestParam double amount) {
        return financeService.setBudget(userId, category, amount);
    }

    // Получить общую статистику
    @GetMapping("/users/{userId}/stats")
    public String getStats(@PathVariable Long userId) {
        return financeService.getStats(userId);
    }

    // Перевод между пользователями
    @PostMapping("/users/{fromUserId}/transfer")
    public String transfer(@PathVariable Long fromUserId,
                           @RequestParam String toLogin,
                           @RequestParam double amount) {
        financeService.transfer(fromUserId, toLogin, amount);
        return "Перевод выполнен!";
    }
}