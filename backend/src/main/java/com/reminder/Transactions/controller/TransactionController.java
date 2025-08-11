package com.reminder.Transactions.controller;

import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.service.TransactionService;
import com.reminder.Users.model.RequestContextDTO;
import com.reminder.security.CustomUserDetails;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/txn")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @Autowired
    LogUtil logUtil;

    @PostMapping
    public ResponseEntity<?> newTransaction(@RequestBody List<Transaction> transactions,
                                            HttpServletRequest request) {
        RequestContextDTO contextDTO = (RequestContextDTO) contextHandler(request);
        try {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = userDetails.getUserId();
            if (transactions == null || transactions.isEmpty())
                throw new Exception("transactions is empty");
            transactionService.newTransaction(transactions, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            String uuid = logUtil.infoLog("User", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please ensure parameters. log ID: " + uuid); //UI should eliminate such instances so likely cause by misuse or threat actor
        } catch (Exception e) {
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PatchMapping("/{id}/comment")
    public ResponseEntity<?> addComment(@PathVariable int id,
                                        @RequestBody String comment) {
        try {
            transactionService.addComment(id, comment);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            String uuid = logUtil.infoLog("User", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please ensure parameters. log ID: " + uuid); //UI should eliminate such instances so likely cause by misuse or threat actor
        } catch (Exception e) {
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PatchMapping("/{id}/category")
    public ResponseEntity<?> changeCategory(@PathVariable int id,
                                            @RequestBody String category) {
        try {
            transactionService.changeCategory(id, category);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            String uuid = logUtil.infoLog("User", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please ensure parameters. log ID: " + uuid); //UI should eliminate such instances so likely cause by misuse or threat actor
        } catch (Exception e) {
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @GetMapping("/{category}")
    public ResponseEntity<?> GetTxnPerCategory(@PathVariable String category) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTxnPerCategory(category));
        } catch (Exception e) {
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.getAllTransactions());
        } catch (Exception e) {
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    private RequestContextDTO contextHandler(HttpServletRequest request) {
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        if (contextDTO != null) {
            contextDTO.setUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        }
        return contextDTO;
    }
}