package net.thesieutoc.database;

import java.util.Date;
import java.util.List;
import net.thesieutoc.api.Card;
import net.thesieutoc.utils.Transaction;
import org.bukkit.entity.Player;

public class Database {
   private DatabaseType database;

   public Database(DatabaseType database) {
      this.database = database;
   }

   public void writeLog(Player player, Card card) {
      this.database.writeLog(player, card);
   }

   public int getPlayerTotalCharged(Player player) {
      return this.database.getPlayerTotalCharged(player);
   }

   public List<Transaction> transactions() {
      return this.database.transactions();
   }

   public List<Transaction> transactions(Date start, Date end) {
      return this.database.transactions(start, end);
   }

   public void disable() {
      this.database.disable();
   }
}
