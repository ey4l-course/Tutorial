package com.reminder.Transactions.service;

import com.reminder.Transactions.model.CategorySource;
import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.repository.TransactionRepository;
import com.reminder.Transactions.utilities.TxnUtility;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repo;
    private final Transaction txn;
    private final TxnUtility txnUtil;

    public TransactionService (TransactionRepository repo,
                               Transaction txn,
                               TxnUtility txnUtil){
        this.repo = repo;
        this.txn = txn;
        this.txnUtil = txnUtil;
    }

    public void newTransaction (List<Transaction> transactions, Long userId){
        for (Transaction txn : transactions)
        {
            validateFields(this.txn);
            txn.setUserId(userId);
            this.txn.setAmount(this.txn.getAmount().setScale(2, RoundingMode.HALF_DOWN));
            if (txn.getCategory() == null) { //Means user did not choose category
                txnUtil.txnCategoryNotSet(txn);
            }else {
                txn.setCategorySource(CategorySource.SPECIAL_CLASSIFICATION);
                txn.setUniqueWeight(1);
            }
            repo.save(this.txn);
        }
    }

    /*
    TODO: Document: if new transaction category is 'null' -> user did not manually defined it;
        if it has entry is user defined -> set it to current txn
        else -> register under 'unclassified' (category 0)
     */

    public void addComment (int id, String comment){
        if (comment.length() > 50)
            throw new IllegalArgumentException("Field comment too long (>50)");
        repo.addComment(id, comment);
    }

    public void changeCategory (int id, String category){
        if (category.length() > 10)
            throw new IllegalArgumentException("Field category too long (>10)");
        repo.changeCategory(id, category);
    }

    public List<Transaction> getTxnPerCategory (String category){
        category = inputLowerCaser(category);
        return repo.getTxnPerCategory(category);
    }

    public List<Transaction> getAllTransactions(){
        return repo.getAllTransactions();
    }

    private void validateFields (Transaction transaction){
        if (transaction.getDescription() == null || transaction.getDescription().isEmpty())
            throw new IllegalArgumentException("description cannot be null");
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) == 0)
            throw new IllegalArgumentException("Amount cannot be empty or 0");
        if (transaction.getAmount().movePointRight(2).toBigInteger().toString().length() > 12)
            throw new IllegalArgumentException("Amount is out of range " + transaction.getAmount());
        if (transaction.getComment().length() > 50)
            throw new IllegalArgumentException("Field comment too long (>50)");
        if (transaction.getPaymentMethod() == null || transaction.getPaymentMethod().isEmpty())
            throw new IllegalArgumentException("Payment method cannot be null");
    }

    private String inputLowerCaser (String str){
        return str.toLowerCase();
    }
}
