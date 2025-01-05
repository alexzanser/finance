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
    public RegisterResponse register(@RequestParam String login, @RequestParam String password) {
        User user = financeService.registerUser(login, password);
        return new RegisterResponse(user.getLogin(), user.getPassword());
    }
    
    // Добавить доход / расход
    @PostMapping("/transactions")
    public Transaction addTransaction(@RequestHeader("X-User-Login") String login,
                                      @RequestHeader("X-User-Password") String password,
                                      @RequestParam String type,
                                      @RequestParam String category,
                                      @RequestParam double amount) {
        return financeService.addTransaction(login, password, type, category, amount);
    }

    // Установить (обновить) бюджет по категории
    @PostMapping("/budget")
    public Budget setBudget(@RequestHeader("X-User-Login") String login,
                            @RequestHeader("X-User-Password") String password,
                            @RequestParam String category,
                            @RequestParam double amount) {
        return financeService.setBudget(login, password, category, amount);
    }

    // Получить общую статистику
    @GetMapping("/stats")
    public String getStats(@RequestHeader("X-User-Login") String login,
                           @RequestHeader("X-User-Password") String password) {
        return financeService.getStats(login, password);
    }

    // Перевод между пользователями
    @PostMapping("/transfer")
    public String transfer(@RequestHeader("X-User-Login") String login,
                           @RequestHeader("X-User-Password") String password,
                           @RequestParam String toLogin,
                           @RequestParam double amount) {
        financeService.transfer(login, password, toLogin, amount);
        return "Перевод выполнен!";
    }
}
