package kr.rahul.moneyManager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.rahul.moneyManager.entity.ExpenseEntity;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {

    //select * from tbl_expenses where profile_id=?1 order by date desc;
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profile_id);

    //select * from tbl_expenses where profile_id=?1 order by date desc limit 5;
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profile_id);

    @Query("select sum(e.amount) from ExpenseEntity e where e.profile.id = :profileId ")
    BigDecimal findTotalExpenseByProfileId(Long profileId);

    //select * from tbl_expenses where profile_id = ?1 and date between ?2 and ?3 and name like %?4%;
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId,LocalDate startDate,LocalDate endDate);

    //select * from tbl_expenses where profileId=?1 and date = ?2;
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId,LocalDate date);
}