package net.thesieutoc.config;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.thesieutoc.Thesieutoc;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DonateMilestone {
   private static File f = null;
   private static FileConfiguration fc;

   public static boolean isEnable() {
      if (f == null) {
         saveDefault();
      }

      return fc.getBoolean("enable", false);
   }

   public static List<String> getCommands(int milestone) {
      if (f == null) {
         saveDefault();
      }

      return fc.getStringList("command." + milestone);
   }

   public static HashSet<Integer> getMilestones() {
      if (f == null) {
         saveDefault();
      }

      HashSet<Integer> milestones = new HashSet();
      Iterator var1 = fc.getConfigurationSection("command").getKeys(false).iterator();

      while(var1.hasNext()) {
         String str = (String)var1.next();

         try {
            milestones.add(Integer.parseInt(str));
         } catch (Exception var4) {
         }
      }

      return milestones;
   }

   public static void saveDefault() {
      f = new File(Thesieutoc.getInstance().getDataFolder(), "naptheomoc.yml");
      if (!f.exists()) {
         f.getParentFile().mkdirs();
         Thesieutoc.getInstance().saveResource("naptheomoc.yml", false);
      }

      fc = YamlConfiguration.loadConfiguration(f);
   }

   public static void reload() {
      saveDefault();
   }
}
