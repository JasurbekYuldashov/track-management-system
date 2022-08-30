package uz.binart.trackmanagementsystem.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
public class LastSaturday {
      public static void main(String[] args) {
        
          LocalDate CurrentDate = LocalDate.now();  
          
          YearMonth ym = YearMonth.of(CurrentDate.getYear(), CurrentDate.getMonth());
          //YearMonth ym = YearMonth.of(2018, 9);
          LocalDate endDate = ym.atEndOfMonth();
          
          DayOfWeek day = endDate.getDayOfWeek();
          int lastDay = day.getValue();
          
          System.out.print("The last Saturday of the month falls on: ");
          
          if(lastDay < 6)
            endDate = endDate.minusDays(lastDay+1);
          else if(lastDay > 6)
            endDate = endDate.minusDays(1);
          
          System.out.println(endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
          
      }
}