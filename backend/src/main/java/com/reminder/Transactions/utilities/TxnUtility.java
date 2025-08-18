package com.reminder.Transactions.utilities;

import com.reminder.Transactions.model.CategorySource;
import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.repository.TransactionRepository;
import com.reminder.security.CustomUserDetails;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
public class TxnUtility {
    private final TransactionRepository repo;

    public TxnUtility (TransactionRepository repo){
        this.repo = repo;
    }

    public void txnCategoryNotSet(Transaction txn) {
        Long userDefinedCategory = repo.userDefinedCategory(txn.getUserId(), txn.getDescription());
        Long globalDefinedCategory = repo.globalDefinedCategory(txn.getDescription());
        if (userDefinedCategory == null) {
            txn.setCategory(globalDefinedCategory == null ? 0L : globalDefinedCategory);
            txn.setCategorySource(CategorySource.GLOBAL_DEFAULT);
            txn.setUniqueWeight(1);
        } else {
            txn.setCategory(userDefinedCategory);
            txn.setCategorySource(CategorySource.PERMANENT_CLASSIFICATION);
            txn.setUniqueWeight(0);
        }
    }

    public boolean validateAuthority (Long txnId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ("admin".equals(userDetails.getRole()))
            return true;
        return userDetails.getUserId().equals(repo.getUserIdByTxnId(txnId));
    }

    public Long getUserId () {
        CustomUserDetails currentAuthenticatedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentAuthenticatedUser.getUserId();
    }
}
