package net.thesieutoc.config;

import java.io.File;
import net.thesieutoc.Thesieutoc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Language {
   private static File f = null;
   private static FileConfiguration fc;

   public static String get(String id) {
      if (f == null) {
         saveDefault();
      }

      String msg = "";

      try {
         msg = fc.getString(id);
         msg = ChatColor.translateAlternateColorCodes('&', msg);
      } catch (Exception var3) {
         var3.printStackTrace();
         Bukkit.getLogger().warning("[Thesieutoc] §cThieu §f" + id + "§c trong file lang.yml.");
      }

      return msg;
   }

   public static void saveDefault() {
      f = new File(Thesieutoc.getInstance().getDataFolder(), "lang.yml");
      if (!f.exists()) {
         f.getParentFile().mkdirs();
         Thesieutoc.getInstance().saveResource("lang.yml", false);
      }

      fc = YamlConfiguration.loadConfiguration(f);
   }

   public static void reload() {
      saveDefault();
   }
}
