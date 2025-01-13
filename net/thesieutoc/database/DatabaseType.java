package net.thesieutoc.database;

import java.util.Date;
import java.util.List;
import net.thesieutoc.api.Card;
import net.thesieutoc.utils.Transaction;
import org.bukkit.entity.Player;

public interface DatabaseType {
   void writeLog(Player var1, Card var2);

   int getPlayerTotalCharged(Player var1);

   List<Transaction> transactions();

   List<Transaction> transactions(Date var1, Date var2);

   void disable();
}
