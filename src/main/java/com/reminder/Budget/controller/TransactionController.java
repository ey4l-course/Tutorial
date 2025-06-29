package com.reminder.Budget.controller;

import com.reminder.Budget.model.Transaction;
import com.reminder.Budget.service.TransactionService;
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
            System.out.println(e); //TODO: add proper logging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //TODO: return a more fade message. suspected request manipulation.
        }catch (Exception e){
            System.out.println(e); //TODO: add proper logging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/comment")
    public ResponseEntity<?> addComment (@PathVariable int id,
                                         @RequestBody String comment)
    {
        try {
            transactionService.addComment(id, comment);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (IllegalArgumentException e){
            System.out.println(e); //TODO: add proper logging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //TODO: return a more fade message. suspected request manipulation.
        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
            System.out.println(e); //TODO: add proper logging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //TODO: return a more fade message. suspected request manipulation.
        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{category}")
    public ResponseEntity<?> GetTxnPerCategory (@PathVariable String category){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTxnPerCategory(category));
        }catch (Exception e){
            System.out.println(e); //TODO: add proper logging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllTransactions (){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.getAllTransactions());
        }catch (Exception e){
            System.out.println(e); //TODO: add proper logging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
