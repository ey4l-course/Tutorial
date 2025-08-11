package com.reminder.Transactions.utilities;

import com.reminder.Transactions.model.CategorySource;
import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.repository.TransactionRepository;

public class TxnUtility {
    private final TransactionRepository repo;

    public TxnUtility (TransactionRepository repo){
        this.repo = repo;
    }

    public void txnCategoryNotSet(Transaction txn) {
        Long userDefinedCategory = repo.userDefinedCategory(txn.getUserId(), txn.getDescription());
        if (userDefinedCategory == null) {
            txn.setCategory(0L);
            txn.setCategorySource(CategorySource.GLOBAL_DEFAULT);
            txn.setUniqueWeight(1);
        } else {
            txn.setCategory(userDefinedCategory);
            txn.setCategorySource(CategorySource.PERMANENT_CLASSIFICATION);
            txn.setUniqueWeight(0);
        }
    }
}
