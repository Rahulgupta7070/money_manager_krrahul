package kr.rahul.moneyManager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.rahul.moneyManager.entity.IncomeEntity;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity,Long> {

    //select * from tbl_expenses where profile_id=?1 order by date desc;
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profile_id);

    //select * from tbl_expenses where profile_id=?1 order by date desc limit 5;
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profile_id);

    @Query("select sum(i.amount) from IncomeEntity i where i.profile.id = :profile_id ")
    BigDecimal findTotalExpenseByProfileId(Long profile_id);

    //select * from tbl_expenses where profile_id = ?1 and date between ?2 and ?3 and name like %?4%;
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId,LocalDate startDate,LocalDate endDate);
}