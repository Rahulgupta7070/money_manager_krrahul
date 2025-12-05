package kr.rahul.moneyManager.service;



import static java.util.stream.Stream.concat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import kr.rahul.moneyManager.dto.ExpenseDTO;
import kr.rahul.moneyManager.dto.IncomeDTO;
import kr.rahul.moneyManager.dto.RecentTransactionDTO;
import kr.rahul.moneyManager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String,Object> getDashboardData(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String,Object> returnValues = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getTop5IncomesOfCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.get5LatestExpensesOfCurrentUser();

        List<RecentTransactionDTO> recentTransactions = concat(latestIncomes.stream().map(income->
                RecentTransactionDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .name(income.getName())
                        .icon(income.getIcon())
                        .amount(income.getAmount())
                        .type("income")
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .build()),
                latestExpenses.stream().map(expense->
                RecentTransactionDTO.builder()
                        .id(expense.getId())
                        .profileId(profile.getId())
                        .name(expense.getName())
                        .createdAt(expense.getCreatedAt())
                        .date(expense.getDate())
                        .amount(expense.getAmount())
                        .icon(expense.getIcon())
                        .type("expense")
                        .updatedAt(expense.getUpdatedAt())
                        .build()

                )).sorted((a,b)->{
                    int cmp = b.getDate().compareTo(a.getDate());
                    if(cmp==0 && b.getCreatedAt()!=null && a.getCreatedAt()!=null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
        }).collect(Collectors.toList());

        returnValues.put("totalBalance",incomeService.getTotalIncomeOfCurrentUser().subtract(expenseService.getTotalExpenseOfCurrentUser()));
        returnValues.put("totalIncome",incomeService.getTotalIncomeOfCurrentUser());
        returnValues.put("totalExpense",expenseService.getTotalExpenseOfCurrentUser());
        returnValues.put("recent5Incomes",latestIncomes);
        returnValues.put("recent5Expenses",latestExpenses);
        returnValues.put("recentTransactions",recentTransactions);

        return returnValues;







    }
}
