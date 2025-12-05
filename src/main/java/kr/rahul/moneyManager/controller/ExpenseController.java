package kr.rahul.moneyManager.controller;



import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.rahul.moneyManager.dto.ExpenseDTO;
import kr.rahul.moneyManager.service.ExpenseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
@CrossOrigin("*")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getCurrentMonthExpensesOfCurrentUser(){
        return new ResponseEntity<>(expenseService.getCurrentMonthExpensesOfCurrentUser(),HttpStatus.OK);
    }

    @GetMapping("/topFive")
    public ResponseEntity<List<ExpenseDTO>> getTop5ExpensesOfCurrentUser(){
        return new ResponseEntity<>(expenseService.get5LatestExpensesOfCurrentUser(),HttpStatus.FOUND);
    }

    @GetMapping("/totalExpense")
    public ResponseEntity<?> getTotalExpenseOfCurrentUser(){
        return new ResponseEntity<>(expenseService.getTotalExpenseOfCurrentUser(),HttpStatus.OK);
    }



    @PostMapping("addExpense")
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO){
        return new ResponseEntity<>(expenseService.addExpense(expenseDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("delete/{expenseId}")
    public ResponseEntity<ExpenseDTO> deleteExpenseOfCurrentUser(@PathVariable Long expenseId){
        return new ResponseEntity<>(expenseService.deleteExpense(expenseId),HttpStatus.OK);
    }
}
