package net.thesieutoc.utils;

import java.util.Date;

public class Transaction {
   private String playerName;
   private int amount;
   private Date date;

   public Transaction(String playerName, int amount, Date date) {
      this.playerName = playerName;
      this.amount = amount;
      this.date = date;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public int getAmount() {
      return this.amount;
   }

   public Date getDate() {
      return this.date;
   }
}
