package net.thesieutoc;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import net.thesieutoc.utils.Transaction;

public class TransactionManager {
   public static List<Transaction> forDaily(List<Transaction> transactions, Date date) {
      return (List)transactions.stream().filter((transaction) -> {
         return isSameDay(transaction.getDate(), date);
      }).sorted(Comparator.comparingInt(Transaction::getAmount).reversed()).collect(Collectors.toList());
   }

   public static List<Transaction> forWeekly(List<Transaction> transactions, Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.set(7, calendar.getFirstDayOfWeek());
      calendar.set(11, 0);
      calendar.set(12, 0);
      calendar.set(13, 0);
      Date weekStartDate = calendar.getTime();
      return (List)transactions.stream().filter((transaction) -> {
         return transaction.getDate().after(weekStartDate);
      }).sorted(Comparator.comparingInt(Transaction::getAmount).reversed()).collect(Collectors.toList());
   }

   public static List<Transaction> forMonthly(List<Transaction> transactions, Date date) {
      Date now = new Date();
      return (List)transactions.stream().filter((transaction) -> {
         return transaction.getDate().getMonth() == now.getMonth();
      }).sorted(Comparator.comparingInt(Transaction::getAmount).reversed()).collect(Collectors.toList());
   }

   public static List<Transaction> forDate(List<Transaction> transactions, Date date) {
      return (List)transactions.stream().filter((transaction) -> {
         return isSameDay(transaction.getDate(), date);
      }).collect(Collectors.toList());
   }

   public static List<Transaction> forYear(List<Transaction> transactions, Date date) {
      return (List)transactions.stream().filter((transaction) -> {
         return transaction.getDate().getYear() == date.getYear();
      }).collect(Collectors.toList());
   }

   public static List<Transaction> forPlayer(List<Transaction> transactions, String playerName) {
      return (List)transactions.stream().filter((transaction) -> {
         return transaction.getPlayerName().equals(playerName);
      }).collect(Collectors.toList());
   }

   private static boolean isSameDay(Date date1, Date date2) {
      Calendar cal1 = Calendar.getInstance();
      cal1.setTime(date1);
      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(date2);
      return cal1.get(1) == cal2.get(1) && cal1.get(2) == cal2.get(2) && cal1.get(5) == cal2.get(5);
   }
}
