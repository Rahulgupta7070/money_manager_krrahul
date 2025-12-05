package kr.rahul.moneyManager.service;



import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import kr.rahul.moneyManager.dto.ExpenseDTO;
import kr.rahul.moneyManager.entity.ProfileEntity;
import kr.rahul.moneyManager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    public final ProfileRepository profileRepository;
    public final EmailService emailService;
    public final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    public String frontend_url;


    @Scheduled(cron = "0 0 22 * * *",zone = "IST")
    public void sendDailyIncomeExpenseReminder(){
          log.info("Job started: sendDailyIncomeExpenseRemainder()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile : profiles){
            String body = "Hi "+ profile.getFullName()+", <br><br>"+"This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"+"<a href="+frontend_url+" style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;'>Go to Money Manager</a>"+"<br><br>Best Regards,<br>Money Manager Team";
            emailService.sendEmail(profile.getEmail(), "Daily Remainder: Add your income and expense for today",body);
        }
         log.info("Job finished: sendDailyIncomeExpenseRemainder()");
    }

    @Scheduled(cron = "0 0 21 * * *",zone = "IST")
    public void sendDailyExpenseSummary(){
        log.info("Job started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles  = profileRepository.findAll();
        for(ProfileEntity profile : profiles){
             List<ExpenseDTO> todaysExpenses = expenseService.getExpensesOfUserOnDate(profile.getId(), LocalDate.now());
             if(!todaysExpenses.isEmpty()){
                 StringBuilder table = new StringBuilder();
                 table.append("<table style='border-collapse:collapse;width:100%;'>");
                 table.append("<tr style='background-color:#f2f2f2;'><th style='border:1px solid #ddd;padding:8px;'>S.No</th><th style='border:1px solid #ddd;padding:8px;'>Name</th><th style='border:1px solid #ddd;padding:8px;'>Amount</th><th style='border:1px solid #ddd;padding:8px;'>Category</th>");
                 int count = 1;
                 for(ExpenseDTO expense : todaysExpenses){
                     table.append("<tr>");
                     table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(count++).append("</td>");
                     table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>");
                     table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>");
                     table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategory_id()!=null ? expense.getCategory_name() : "N/A").append("</td>");
                     table.append("</tr>");
                 }
                 table.append("</table>");
                 String body = "Hi "+profile.getFullName()+",<br/><br/> Here is a summary of your expenses for today:<br/><br/>"+table+"<br/><br/>Best Regards,<br/>Money Manager Team.";
                 emailService.sendEmail(profile.getEmail(),"Summary of your today expenses",body);
             }
             log.info("Job finished: sendDailyExpenseSummary()");

        }
    }
}