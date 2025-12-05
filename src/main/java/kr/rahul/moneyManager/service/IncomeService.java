package kr.rahul.moneyManager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kr.rahul.moneyManager.dto.IncomeDTO;
import kr.rahul.moneyManager.entity.CategoryEntity;
import kr.rahul.moneyManager.entity.IncomeEntity;
import kr.rahul.moneyManager.entity.ProfileEntity;
import kr.rahul.moneyManager.repository.CategoryRepository;
import kr.rahul.moneyManager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategory_id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));
        IncomeEntity newEntity = incomeRepository.save(toEntity(incomeDTO,profile,category));
        return toDTO(newEntity);
    }

    public List<IncomeDTO> getCurrentMonthIncomesOfCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> entities = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);

        return entities.stream().map(this::toDTO).toList();
    }

    //helper methods

    public IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .id(incomeDTO.getId())
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(category)
                .createdAt(incomeDTO.getCreatedAt())
                .updatedAt(incomeDTO.getUpdatedAt())
                .build();
    }

    public IncomeDTO toDTO(IncomeEntity incomeEntity){
        return IncomeDTO.builder()
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


    public IncomeDTO deleteIncomeOfCurrentUser(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity newEntity = incomeRepository.findById(incomeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Income not found"));
        if(!newEntity.getProfile().getId().equals(profile.getId())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized to delete this income");
        }
        incomeRepository.delete(newEntity);
        return toDTO(newEntity);
    }

    public List<IncomeDTO> getTop5IncomesOfCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> entities = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomeOfCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getId());

        return total!=null ? total : BigDecimal.ZERO;
    }

    //filter incomes
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> entities = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        if(entities==null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Mismatch fields");
        }
        return entities.stream().map(this::toDTO).toList();

    }
}