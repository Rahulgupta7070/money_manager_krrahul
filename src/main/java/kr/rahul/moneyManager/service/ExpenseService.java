package kr.rahul.moneyManager.service;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kr.rahul.moneyManager.dto.ExpenseDTO;
import kr.rahul.moneyManager.entity.CategoryEntity;
import kr.rahul.moneyManager.entity.ExpenseEntity;
import kr.rahul.moneyManager.entity.ProfileEntity;
import kr.rahul.moneyManager.repository.CategoryRepository;
import kr.rahul.moneyManager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

   //add Expenses Of Current user
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(expenseDTO.getCategory_id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));

        ExpenseEntity entity = expenseRepository.save(toEntity(expenseDTO,profile,category));

        return toDTO(entity);

    }
   //get all expenses of current user
    public List<ExpenseDTO> getCurrentMonthExpensesOfCurrentUser() {
       ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> entities = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);

        return entities.stream().map(this::toDTO).toList();
    }

    //delete expense of current user
    public ExpenseDTO deleteExpense(Long expenseId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Expense not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized to delete this expense");
        }
        expenseRepository.delete(entity);
        return toDTO(entity);
    }

    //get latest five expenses of current user
    public List<ExpenseDTO> get5LatestExpensesOfCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> entities = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    //get total expenses of current user
    public BigDecimal getTotalExpenseOfCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total!=null ? total : BigDecimal.ZERO;
    }

    //filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
       List<ExpenseEntity> entities =  expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
       if(entities==null){
           throw new ResponseStatusException(HttpStatus.CONFLICT,"Mismatch fields");
       }
       return entities.stream().map(this::toDTO).toList();
    }

    //Notifications
    public List<ExpenseDTO> getExpensesOfUserOnDate(Long profileId,LocalDate date){

        List<ExpenseEntity> entities = expenseRepository.findByProfileIdAndDate(profileId,date);

        return entities.stream().map(this::toDTO).toList();

    }



    //helper methods

    public ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .id(expenseDTO.getId())
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(category)
                .createdAt(expenseDTO.getCreatedAt())
                .updatedAt(expenseDTO.getUpdatedAt())
                .build();
    }

    public ExpenseDTO toDTO(ExpenseEntity incomeEntity){
        return ExpenseDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .amount(incomeEntity.getAmount())
                .icon(incomeEntity.getIcon())
                .date(incomeEntity.getDate())
                .category_id(incomeEntity.getCategory()!=null ? incomeEntity.getCategory().getId() : null)
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .category_name(incomeEntity.getCategory().getName())
                .build();
    }



}
