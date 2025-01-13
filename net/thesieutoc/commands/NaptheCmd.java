package net.thesieutoc.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.api.Card;
import net.thesieutoc.api.CardPrice;
import net.thesieutoc.api.ThesieutocAPI;
import net.thesieutoc.config.Config;
import net.thesieutoc.config.Language;
import net.thesieutoc.menu.anvil.AnvilMenu_seripin;
import net.thesieutoc.menu.chat.ChatMenu;
import net.thesieutoc.utils.Task;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class NaptheCmd implements CommandExecutor, TabCompleter {
   HashMap<String, Long> fastcmd_cooldown = new HashMap();

   public NaptheCmd() {
      Thesieutoc.getInstance().getCommand("napthe").setTabCompleter(this);
   }

   public boolean onCommand(CommandSender sender, Command cmd, String a, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("§cCommand chi xai duoc trong game!");
         return false;
      } else {
         Player p = (Player)sender;
         int cardPrice;
         String serial;
         if (args.length == 4) {
            if (!Config.fastCmd()) {
               return false;
            }

            if ((Long)this.fastcmd_cooldown.getOrDefault(p.getUniqueId().toString(), 0L) > System.currentTimeMillis()) {
               p.sendMessage(Language.get("nap_the_that_bai"));
               return false;
            }

            this.fastcmd_cooldown.put(p.getUniqueId().toString(), System.currentTimeMillis() + 3000L);

            try {
               String cardType = args[0];
               cardPrice = Integer.parseInt(args[1]);
               serial = args[2];
               String pin = args[3];
               if (!Config.cardList().contains(args[0])) {
                  return false;
               }

               if (CardPrice.getPrice(cardPrice).getId() == -1) {
                  return false;
               }

               Card card = new Card(p, cardType, cardPrice, serial, pin);
               Task.asyncTask(() -> {
                  ThesieutocAPI.processCard(p, card);
               });
            } catch (Exception var14) {
               p.sendMessage(Language.get("nap_the_that_bai"));
            }
         }

         if (args.length == 0) {
            TextComponent txtcomponent = new TextComponent("");
            Iterator var17 = Config.cardList().iterator();

            while(var17.hasNext()) {
               serial = (String)var17.next();
               TextComponent message = new TextComponent(serial);
               message.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/napthe textchoosecard " + serial));
               txtcomponent.addExtra(message);
               txtcomponent.addExtra("§r   ");
            }

            p.sendMessage("");
            p.sendMessage(Language.get("chat_chon_loai_the"));
            p.spigot().sendMessage(txtcomponent);
         }

         Card card;
         String cardType;
         if (args.length == 2 && args[0].equalsIgnoreCase("choosecard")) {
            card = ThesieutocAPI.getPromptCard(p);
            cardType = args[1];
            if (!Config.cardList().contains(cardType)) {
               ThesieutocAPI.removePromptCard(p);
               return false;
            }

            card.cardType(cardType);
            ThesieutocAPI.updatePromptCard(p, card);
         }

         if (args.length == 2 && args[0].equalsIgnoreCase("choosecardprice")) {
            try {
               card = ThesieutocAPI.getPromptCard(p);
               cardPrice = Integer.parseInt(args[1]);
               if (CardPrice.getPrice(cardPrice).getId() == -1) {
                  ThesieutocAPI.removePromptCard(p);
                  return false;
               }

               card.cardPrice(cardPrice);
               ThesieutocAPI.updatePromptCard(p, card);
               AnvilMenu_seripin.seri(p);
            } catch (Exception var13) {
               ThesieutocAPI.removePromptCard(p);
               p.sendMessage(Language.get("nap_the_that_bai"));
            }
         }

         if (args.length == 2 && args[0].equalsIgnoreCase("textchoosecard")) {
            card = ThesieutocAPI.getPromptCard(p);
            cardType = args[1];
            if (!Config.cardList().contains(cardType)) {
               ThesieutocAPI.removePromptCard(p);
               p.sendMessage(Language.get("nap_the_that_bai"));
               return false;
            }

            card.cardType(cardType);
            ThesieutocAPI.updatePromptCard(p, card);
            TextComponent txtcomponent = new TextComponent("");
            Iterator var21 = Arrays.asList(10000, 20000, 30000, 50000, 100000, 200000, 500000, 1000000).iterator();

            while(var21.hasNext()) {
               int cardPrice = (Integer)var21.next();
               TextComponent message = new TextComponent("§b" + (new DecimalFormat("#,###")).format((long)cardPrice));
               message.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/napthe textchoosecardprice " + cardPrice));
               txtcomponent.addExtra(message);
               txtcomponent.addExtra("§r   ");
            }

            p.sendMessage(MessageFormat.format(Language.get("chat_da_chon_loai_the"), args[1]));
            p.sendMessage("");
            p.sendMessage(Language.get("chat_chon_menh_gia"));
            p.spigot().sendMessage(txtcomponent);
         }

         if (args.length == 2 && args[0].equalsIgnoreCase("textchoosecardprice")) {
            try {
               card = ThesieutocAPI.getPromptCard(p);
               cardPrice = Integer.parseInt(args[1]);
               if (CardPrice.getPrice(cardPrice).getId() == -1) {
                  ThesieutocAPI.removePromptCard(p);
                  p.sendMessage(Language.get("nap_the_that_bai"));
                  return false;
               }

               card.cardPrice(cardPrice);
               ThesieutocAPI.updatePromptCard(p, card);
               ChatMenu.prompt(p);
            } catch (Exception var12) {
               ThesieutocAPI.removePromptCard(p);
               p.sendMessage(Language.get("nap_the_that_bai"));
            }
         }

         return true;
      }
   }

   public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
      if (args.length == 1) {
         return Config.cardList();
      } else if (args.length == 2) {
         return new LinkedList(Thesieutoc.getInstance().getConfig().getConfigurationSection("card.command").getKeys(false));
      } else if (args.length == 3) {
         return Arrays.asList("serial");
      } else {
         return (List)(args.length == 4 ? Arrays.asList("pin") : new LinkedList());
      }
   }
}
