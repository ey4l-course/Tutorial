package com.reminder.Transactions.unitTests;

import com.reminder.Transactions.model.CategorySource;
import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.repository.TransactionRepository;
import com.reminder.Transactions.service.TransactionService;
import com.reminder.Transactions.utilities.TxnUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class NewTxnClassificationTest {
    @Test
    void specialClassification () {
        TransactionRepository repo = Mockito.mock(TransactionRepository.class);
        TxnUtility util = Mockito.mock(TxnUtility.class);
        Transaction testTxn = new Transaction();
        testTxn.setUserId(1L);
        testTxn.setDescription("McDonald's");
        testTxn.setAmount(BigDecimal.valueOf(98.34));
        testTxn.setCategory(3L);
        testTxn.setPaymentMethod("Credit1234");
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        List<Transaction> list = new ArrayList<>();
        list.add(testTxn);

        Mockito.doNothing().when(repo).save(any(Transaction.class));
        Mockito.verify(util, Mockito.never()).txnCategoryNotSet(any());
        TransactionService service = new TransactionService(repo, util);
        service.newTransaction(list, 3L);
        Mockito.verify(repo, Mockito.times(1)).save(captor.capture());

        Transaction saved = captor.getValue();

        Assertions.assertEquals(CategorySource.SPECIAL_CLASSIFICATION, saved.getCategorySource());
        Assertions.assertEquals(1, saved.getUniqueWeight());
    }

    @Test
    void userDefinedCategory () {
        TransactionRepository repo = Mockito.mock(TransactionRepository.class);
        TxnUtility util = new TxnUtility(repo);
        TransactionService service = new TransactionService(repo, util);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        Transaction testTxn = new Transaction();
        testTxn.setDescription("McDonald's");
        testTxn.setAmount(BigDecimal.valueOf(98.34));
        testTxn.setPaymentMethod("Credit1234");
        List<Transaction> list = new ArrayList<>();
        list.add(testTxn);

        Mockito.doNothing().when(repo).save(any(Transaction.class));
        Mockito.when(repo.userDefinedCategory(any(), anyString())).thenReturn(2L);

        service.newTransaction(list, 1L);

        Mockito.verify(repo, Mockito.times(1)).save(captor.capture());

        Transaction saved = captor.getValue();

        Assertions.assertEquals(CategorySource.PERMANENT_CLASSIFICATION, saved.getCategorySource());
        Assertions.assertEquals(0, saved.getUniqueWeight());
    }

    @Test
    void globalDefinedCategory () {
        TransactionRepository repo = Mockito.mock(TransactionRepository.class);
        TxnUtility util = new TxnUtility(repo);
        TransactionService service = new TransactionService(repo, util);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        Transaction testTxn = new Transaction();
        testTxn.setDescription("McDonald's");
        testTxn.setAmount(BigDecimal.valueOf(98.34));
        testTxn.setPaymentMethod("Credit1234");
        List<Transaction> list = new ArrayList<>();
        list.add(testTxn);
        Mockito.when(repo.userDefinedCategory(any(), anyString())).thenReturn(null);
        Mockito.when(repo.globalDefinedCategory(anyString())).thenReturn(2L);
        Mockito.doNothing().when(repo).save(any());

        service.newTransaction(list, 1L);

        Mockito.verify(repo, Mockito.times(1)).save(captor.capture());
        Transaction saved = captor.getValue();
        Assertions.assertEquals(CategorySource.GLOBAL_DEFAULT, saved.getCategorySource());
        Assertions.assertEquals(2L, saved.getCategory());
        Assertions.assertEquals(1, saved.getUniqueWeight());
    }
}
