package net.thesieutoc.database.a;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.api.Card;
import net.thesieutoc.database.DatabaseType;
import net.thesieutoc.utils.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Flatfile implements DatabaseType {
   public void writeLog(Player p, Card card) {
      Date now = new Date();
      String msg = p.getName() + " | " + card.SERIAL() + " | " + card.PIN() + " | " + card.cardPrice() + " | " + card.cardType() + " | " + card.callbackMessage();
      SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
      File log = new File(Thesieutoc.getInstance().getDataFolder(), "log" + (card.callbackMessage().equals("thanh cong") ? "_success" : "_failed") + ".txt");
      if (!log.exists()) {
         log.getParentFile().mkdirs();
      }

      BufferedWriter writer = null;

      try {
         writer = new BufferedWriter(new FileWriter(log, true));
         writer.append("[").append(df.format(now)).append("] ").append(msg);
         writer.newLine();
         writer.flush();
         writer.close();
      } catch (IOException var9) {
         var9.printStackTrace();
      }

   }

   public int getPlayerTotalCharged(Player p) {
      int total = 0;
      File logFile = new File(Thesieutoc.getInstance().getDataFolder(), "log_success.txt");
      if (logFile.exists()) {
         try {
            Scanner scanner = new Scanner(logFile);

            try {
               while(scanner.hasNextLine()) {
                  String line = scanner.nextLine();
                  if (line.contains("thanh cong")) {
                     String[] logParts = line.split("\\] ", 2);
                     String timestampString = logParts[0].substring(1);
                     String logContent = logParts[1];
                     SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                     df.parse(timestampString);
                     String[] logFields = logContent.split(" \\| ");
                     String playerName = logFields[0];
                     if (playerName.equals(p.getName())) {
                        total += Integer.parseInt(logFields[3]);
                     }
                  }
               }
            } catch (Throwable var14) {
               try {
                  scanner.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }

               throw var14;
            }

            scanner.close();
         } catch (Exception var15) {
            var15.printStackTrace();
            Bukkit.getLogger().warning("[Thesieutoc] §cCo loi xay ra khi dang tinh tong tien da nap cua nguoi choi, vui long lien he staff TheSieuToc.");
         }
      }

      return total;
   }

   public List<Transaction> transactions() {
      return null;
   }

   public List<Transaction> transactions(Date start, Date end) {
      return null;
   }

   public void disable() {
   }
}
