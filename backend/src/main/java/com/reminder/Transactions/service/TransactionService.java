package com.reminder.Transactions.service;

import com.reminder.Transactions.model.*;
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

    public void changeCategory (Long txnId, CatChangeDto catChangeDto){
        if (!txnUtil.validateAuthority(txnId))
            throw new SecurityException("Txn #" + txnId + " isn't owned by authenticated user");
        if (catChangeDto.getPermanent()){
            ClassUpdateDTO dto = new ClassUpdateDTO(txnId, null, null, catChangeDto.getCategory());
            dto.setUserId(txnUtil.getUserId());
            dto.setDescription(repo.getTxnDesc(txnId));
            updateUserClassification(dto);
        }
        repo.changeCategory(txnId, catChangeDto.getCategory());
    }

    private void updateUserClassification(ClassUpdateDTO dto) {
        if (repo.isClassifiedByUser(dto))
            repo.updateClassification(dto);
        else
            repo.firstClassification(dto);
    }

    public List<Transaction> getTxnPerCategory (GetTransactions dto) throws IllegalAccessException{
        if ("admin".equals(dto.getUserRole()))
            if (dto.getWantedUser() == null) {
                return repo.getTxnPerCategory(dto.getCategory());
            }else {
                return repo.getUserTxnPerCategory(dto.getCategory(), dto.getWantedUser());
            }
        if (dto.getWantedUser() != null)
            throw new IllegalAccessException("Unauthorized query for other user transactions");
        return repo.getUserTxnPerCategory(dto.getCategory(), dto.getUserId());
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
