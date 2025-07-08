package com.reminder.Budget.controller;

import com.reminder.Budget.model.Transaction;
import com.reminder.Budget.service.TransactionService;
import com.reminder.Budget.utilities.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/txn")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> newTransaction (@RequestBody Transaction transaction){
        try{
            transactionService.newTransaction(transaction);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (IllegalArgumentException e){
            String uuid = LogUtil.infoLog("User", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please ensure parameters. log ID: " + uuid); //UI should eliminate such instances so likely cause by misuse or threat actor
        }catch (Exception e){
            String uuid = LogUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PatchMapping("/{id}/comment")
    public ResponseEntity<?> addComment (@PathVariable int id,
                                         @RequestBody String comment)
    {
        try {
            transactionService.addComment(id, comment);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            String uuid = LogUtil.infoLog("User", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please ensure parameters. log ID: " + uuid); //UI should eliminate such instances so likely cause by misuse or threat actor        }catch (Exception e){
        }catch (Exception e){
            String uuid = LogUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PatchMapping("/{id}/category")
    public ResponseEntity<?> changeCategory (@PathVariable int id,
                                         @RequestBody String category)
    {
        try {
            transactionService.changeCategory(id, category);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (IllegalArgumentException e){
            String uuid = LogUtil.infoLog("User", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please ensure parameters. log ID: " + uuid); //UI should eliminate such instances so likely cause by misuse or threat actor
        }catch (Exception e){
            String uuid = LogUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @GetMapping("/{category}")
    public ResponseEntity<?> GetTxnPerCategory (@PathVariable String category){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTxnPerCategory(category));
        }catch (Exception e){
            String uuid = LogUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllTransactions (){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.getAllTransactions());
        }catch (Exception e){
            String uuid = LogUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }
}
