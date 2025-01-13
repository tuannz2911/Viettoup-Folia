package net.thesieutoc.config;

import java.util.List;
import net.thesieutoc.Thesieutoc;

public class Config {
   public static boolean debug() {
      if (!Thesieutoc.getInstance().getConfig().contains("debug")) {
         Thesieutoc.getInstance().getConfig().set("debug", false);
         Thesieutoc.getInstance().saveConfig();
      }

      return Thesieutoc.getInstance().getConfig().getBoolean("debug", false);
   }

   public static String APIKey() {
      return Thesieutoc.getInstance().getConfig().getString("TheSieuToc-API.key", "");
   }

   public static String APISecret() {
      return Thesieutoc.getInstance().getConfig().getString("TheSieuToc-API.secret", "");
   }

   public static boolean callBack() {
      return Thesieutoc.getInstance().getConfig().getBoolean("TheSieuToc-API.callback", true);
   }

   public static boolean customURL() {
      return Thesieutoc.getInstance().getConfig().getBoolean("TheSieuToc-API.custom.enable", false);
   }

   public static String customURLValue() {
      return Thesieutoc.getInstance().getConfig().getString("TheSieuToc-API.custom.url", "");
   }

   public static boolean SQLEnable() {
      return Thesieutoc.getInstance().getConfig().getBoolean("mysql.enable", false);
   }

   public static String SQLHost() {
      return Thesieutoc.getInstance().getConfig().getString("mysql.host", "");
   }

   public static int SQLPort() {
      return Thesieutoc.getInstance().getConfig().getInt("mysql.port", 3306);
   }

   public static String SQLUser() {
      return Thesieutoc.getInstance().getConfig().getString("mysql.user", "");
   }

   public static String SQLPass() {
      return Thesieutoc.getInstance().getConfig().getString("mysql.password", "");
   }

   public static String SQLDatabase() {
      return Thesieutoc.getInstance().getConfig().getString("mysql.database", "");
   }

   public static int placeholderUpdate() {
      return Thesieutoc.getInstance().getConfig().getInt("placeholder_update", 300);
   }

   public static boolean fastCmd() {
      return Thesieutoc.getInstance().getConfig().getBoolean("fastcmd", true);
   }

   public static List<String> cardList() {
      return Thesieutoc.getInstance().getConfig().getStringList("card.enable");
   }
}
