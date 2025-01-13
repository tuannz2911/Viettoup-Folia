package net.thesieutoc.placeholder;

import com.google.gson.JsonObject;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.TransactionManager;
import net.thesieutoc.config.Language;
import net.thesieutoc.utils.Transaction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TSTPlaceholder extends PlaceholderExpansion {
   Thesieutoc m;

   public TSTPlaceholder(Thesieutoc m) {
      this.m = m;
   }

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   public String getAuthor() {
      return this.m.getDescription().getAuthors().toString();
   }

   public String getIdentifier() {
      return "tst";
   }

   public String getVersion() {
      return this.m.getDescription().getVersion();
   }

   public String onRequest(OfflinePlayer player, String placeholder) {
      return this.solv(player.getName(), placeholder);
   }

   public String onPlaceholderRequest(Player p, String placeholder) {
      return p == null ? "" : this.solv(p.getName(), placeholder);
   }

   private String solv(String player_name, String placeholder) {
      placeholder = placeholder.toLowerCase();
      DecimalFormat value_format = new DecimalFormat(Language.get("placeholder.value_format"));
      String[] args = placeholder.split("_");
      String key;
      if (args[0].equals("total")) {
         int value = 0;
         key = Language.get("placeholder.total");
         if (args.length == 1 || args[1].equals("total") || args[1].equals("alltime")) {
            value = this.m.THESIEUTOP.cacheValue("total_alltime");
         }

         if (args.length > 1) {
            if (args[1].equals("daily") || args[1].equals("today")) {
               value = this.m.THESIEUTOP.cacheValue("total_daily");
            }

            if (args[1].equals("week") || args[1].equals("weekly")) {
               value = this.m.THESIEUTOP.cacheValue("total_weekly");
            }

            if (args[1].equals("month") || args[1].equals("monthly")) {
               value = this.m.THESIEUTOP.cacheValue("total_monthly");
            }

            if (args[1].equals("year") || args[1].equals("yearly")) {
               value = this.m.THESIEUTOP.cacheValue("total_year");
            }
         }

         return MessageFormat.format(key, value_format.format((long)value));
      } else {
         String placeholder_format;
         if (args[0].equals("top")) {
            placeholder_format = args[args.length - 1];
            key = Language.get("placeholder.top");
            String type = "top_alltime";
            if (args.length == 3) {
               if (args[1].equals("daily") || args[1].equals("today")) {
                  type = "top_daily";
               }

               if (args[1].equals("week") || args[1].equals("weekly")) {
                  type = "top_weekly";
               }

               if (args[1].equals("month") || args[1].equals("monthly")) {
                  type = "top_monthly";
               }

               if (args[1].equals("year") || args[1].equals("yearly")) {
                  type = "top_year";
               }
            }

            JsonObject json = (JsonObject)this.m.THESIEUTOP.cache.getOrDefault(type + "_" + placeholder_format, new JsonObject());
            return !json.has("name") ? MessageFormat.format(Language.get("placeholder.top_empty"), placeholder_format) : MessageFormat.format(key, placeholder_format, json.get("name").getAsString(), value_format.format((long)json.get("value").getAsInt()));
         } else if (!args[0].equals("player")) {
            return "";
         } else {
            placeholder_format = Language.get("placeholder.player");
            key = "player_" + player_name + "_" + args[1];
            if (!this.m.THESIEUTOP.cache.containsKey(key)) {
               List<Transaction> playerTrans = TransactionManager.forPlayer(this.m.THESIEUTOP.allTimeTrans, player_name);
               if (args.length == 2) {
                  if (args[1].equals("daily") || args[1].equals("today")) {
                     playerTrans = TransactionManager.forDaily(playerTrans, new Date());
                  }

                  if (args[1].equals("week") || args[1].equals("weekly")) {
                     playerTrans = TransactionManager.forWeekly(playerTrans, new Date());
                  }

                  if (args[1].equals("month") || args[1].equals("monthly")) {
                     playerTrans = TransactionManager.forMonthly(playerTrans, new Date());
                  }

                  if (args[1].equals("year") || args[1].equals("yearly")) {
                     playerTrans = TransactionManager.forYear(playerTrans, new Date());
                  }
               }

               this.m.THESIEUTOP.total(playerTrans, key);
            }

            int value = ((JsonObject)this.m.THESIEUTOP.cache.get(key)).get("value").getAsInt();
            return MessageFormat.format(placeholder_format, value_format.format((long)value));
         }
      }
   }
}
