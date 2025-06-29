package com.reminder.Budget.service;

import com.reminder.Budget.model.Transaction;
import com.reminder.Budget.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public void newTransaction (Transaction transaction){
        transaction.setAmount(transaction.getAmount().setScale(2, RoundingMode.HALF_DOWN));
        validateFields(transaction);
        transaction.setTxnTime(LocalDate.now());
        transactionRepository.save(transaction);
        //TODO: handle exceptions
    }

    public void addComment (int id, String comment){
        if (comment.length() > 50)
            throw new IllegalArgumentException("Field comment too long (>50)");
        transactionRepository.addComment(id, comment);
    }

    public void changeCategory (int id, String category){
        if (category.length() > 10)
            throw new IllegalArgumentException("Field category too long (>10)");
        transactionRepository.changeCategory(id, category);
    }

    public List<Transaction> getTxnPerCategory (String category){
        return transactionRepository.getTxnPerCategory(category);
    }

    public List<Transaction> getAllTransactions(){
        return transactionRepository.getAllTransactions();
    }

    private void validateFields (Transaction transaction){
        if (transaction.getDescription() == null || transaction.getDescription().isEmpty())
            throw new IllegalArgumentException("description cannot be null");
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) == 0)
            throw new IllegalArgumentException("Amount cannot be empty or 0");
        if (transaction.getAmount().movePointRight(2).toBigInteger().toString().length() > 12)
            throw new IllegalArgumentException("Amount is out of range" + transaction.getAmount());
        if (transaction.getDescription().length() > 20)
            throw new IllegalArgumentException("Field description too long (>20)");
        if (transaction.getCategory().length() > 10)
            throw new IllegalArgumentException("Field category too long (>10)");
        if (transaction.getComment().length() > 50)
            throw new IllegalArgumentException("Field comment too long (>50)");
    }
}
