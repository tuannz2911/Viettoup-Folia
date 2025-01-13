package net.thesieutoc;

import net.thesieutoc.card.MilestonesListener;
import net.thesieutoc.commands.NaptheCmd;
import net.thesieutoc.commands.ThesieutocCmd;
import net.thesieutoc.config.Config;
import net.thesieutoc.config.DonateMilestone;
import net.thesieutoc.config.Language;
import net.thesieutoc.database.Database;
import net.thesieutoc.database.a.Flatfile;
import net.thesieutoc.database.a.MySQL;
import net.thesieutoc.internal.DoNotTouch;
import net.thesieutoc.menu.anvil.AnvilMenu_seripin;
import net.thesieutoc.menu.chat.ChatMenu;
import net.thesieutoc.placeholder.TSTPlaceholder;
import net.thesieutoc.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Thesieutoc extends JavaPlugin {
   private static Thesieutoc m;
   private DoNotTouch doNotTouch;
   private static Database database;
   public Thesieutop THESIEUTOP;
   TSTPlaceholder placeholder;

   public void onEnable() {
      this.enable();
   }

   public void onDisable() {
      this.disable();
   }

   public static Thesieutoc getInstance() {
      return m;
   }

   public DoNotTouch doNotTouch() {
      return this.doNotTouch;
   }

   public static Database getDatabase() {
      return database;
   }

   public void enable() {
      m = this;
      this.THESIEUTOP = new Thesieutop();
      this.saveDefaultConfig();
      Language.saveDefault();
      DonateMilestone.saveDefault();
      this.getCommand("napthe").setExecutor(new NaptheCmd());
      this.getCommand("thesieutoc").setExecutor(new ThesieutocCmd());
      Bukkit.getPluginManager().registerEvents(new ChatMenu(), m);
      Bukkit.getPluginManager().registerEvents(new AnvilMenu_seripin(), m);
      Bukkit.getPluginManager().registerEvents(new MilestonesListener(), m);
      Bukkit.getPluginManager().registerEvents(new UpdateChecker(), m);
      if (Config.SQLEnable()) {
         MySQL sql = new MySQL();
         sql.init();
         database = new Database(sql);
      } else {
         database = new Database(new Flatfile());
      }

      this.doNotTouch = new DoNotTouch();
      this.doNotTouch.init();
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         try {
            (new TSTPlaceholder(m)).register();
         } catch (Exception var2) {
         }
      }

   }

   public void disable() {
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "papi unregister tst");
      }

      getDatabase().disable();
   }
}
