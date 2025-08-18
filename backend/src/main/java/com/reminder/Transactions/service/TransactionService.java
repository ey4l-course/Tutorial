package com.reminder.Transactions.service;

import com.reminder.Transactions.model.CategorySource;
import com.reminder.Transactions.model.ClassUpdateDTO;
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
    private final TxnUtility txnUtil;

    public TransactionService (TransactionRepository repo,
                               TxnUtility txnUtil){
        this.repo = repo;
        this.txnUtil = txnUtil;
    }

    public void newTransaction (List<Transaction> transactions, Long userId){
        for (Transaction txn : transactions)
        {
            txn.setAmount(txn.getAmount().setScale(2, RoundingMode.HALF_DOWN));
            validateFields(txn);
            txn.setUserId(userId);
            if (txn.getCategory() == null) { //Means user did not choose category
                txnUtil.txnCategoryNotSet(txn);
            }else {
                txn.setCategorySource(CategorySource.SPECIAL_CLASSIFICATION);
                txn.setUniqueWeight(1);
            }
            repo.save(txn);
        }
    }

    public void addComment (Long id, String comment) throws IllegalAccessException {
        if (comment == null || comment.trim().isEmpty())
            throw new IllegalArgumentException("Comment is empty");
        if (comment.length() > 50)
            throw new IllegalArgumentException("Field comment too long (>50)");
        if (!txnUtil.validateAuthority(id))
            throw new IllegalAccessException (String.format("Transaction #%s does not belong to user", id));
        repo.addComment(id, comment.trim());
    }

    public void changeCategory (Long txnId, Long category, Boolean isPermanent){
        if (!txnUtil.validateAuthority(txnId))
            throw new SecurityException("Txn #" + txnId + " isn't owned by authenticated user");
        if (isPermanent){
            ClassUpdateDTO dto = new ClassUpdateDTO(txnId, null, null, category);
            dto.setUserId(txnUtil.getUserId());
            dto.setDescription(repo.getTxnDesc(txnId));
            updateUserClassification(dto);
        }
        repo.changeCategory(txnId, category);
    }

    private void updateUserClassification(ClassUpdateDTO dto) {
        if (repo.isClassifiedByUser(dto))
            repo.updateClassification(dto);
        else
            repo.firstClassification(dto);
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
        if (transaction.getPaymentMethod() == null || transaction.getPaymentMethod().isEmpty())
            throw new IllegalArgumentException("Payment method cannot be null");
    }

    private String inputLowerCaser (String str){
        return str.toLowerCase();
    }
}
