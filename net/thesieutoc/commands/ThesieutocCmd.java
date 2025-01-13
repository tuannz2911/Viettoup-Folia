package net.thesieutoc.commands;

import com.google.gson.JsonObject;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.TransactionManager;
import net.thesieutoc.api.Card;
import net.thesieutoc.api.CardPrice;
import net.thesieutoc.config.DonateMilestone;
import net.thesieutoc.config.Language;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThesieutocCmd implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String a, String[] args) {
      if (args.length == 0) {
         sender.sendMessage("§e/thesieutoc give [tên người chơi] [mệnh giá]§f: Nạp cho người chơi số tiền tương ứng");
         sender.sendMessage("§e/thesieutoc top§f: Xem top nạp thẻ");
         sender.sendMessage("§e/thesieutoc reload§f: Tải lại các file config.");
      }

      if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
         Thesieutoc.getInstance().reloadConfig();
         Language.reload();
         DonateMilestone.reload();
         sender.sendMessage("§eReload config TheSieuToc thanh cong!");
      }

      if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
         if (args.length == 2) {
            sender.sendMessage("§c/thesieutoc give [tên người chơi] [mệnh giá]§f: Nạp cho người chơi số tiền tương ứng");
         }

         if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
               int menhgia = Integer.parseInt(args[2]);
               if (CardPrice.getPrice(menhgia).getId() == -1) {
                  sender.sendMessage("§cMệnh giá không hợp lệ!");
                  return false;
               }

               Card card = new Card();
               card.player(target.getName());
               card.cardType("GIVE");
               card.cardPrice(menhgia);
               card.SERIAL("0");
               card.PIN("0");
               card.callbackMessage("thanh cong");
               Thesieutoc.getInstance().doNotTouch().chargeSuccess(target, card);
               Thesieutoc.getDatabase().writeLog(target, card);
               sender.sendMessage("§aNạp thành công §f" + menhgia + "§a VNĐ cho " + target.getName() + "!");
            } else {
               sender.sendMessage("§cNgười chơi §e" + args[1] + "§c không online!");
            }
         }
      }

      return true;
   }

   public void top(CommandSender sender, String[] args) {
      switch(args.length) {
      case 1:
         this.sendHelp(sender);
         break;
      case 2:
         if (args[1].equalsIgnoreCase("help")) {
            this.sendHelp(sender);
         }

         if (Arrays.asList("total", "alltime").contains(args[1].toLowerCase())) {
            this.printTop(sender, "top_alltime", (Date)null);
         }

         if (Arrays.asList("today", "daily").contains(args[1].toLowerCase())) {
            this.printTop(sender, "top_daily", (Date)null);
         }

         if (Arrays.asList("week", "weekly").contains(args[1].toLowerCase())) {
            this.printTop(sender, "top_weekly", (Date)null);
         }

         if (Arrays.asList("month", "monthly").contains(args[1].toLowerCase())) {
            this.printTop(sender, "top_monthly", (Date)null);
         }

         if (Arrays.asList("year", "yearly").contains(args[1].toLowerCase())) {
            this.printTop(sender, "top_year", (Date)null);
         }
      }

   }

   public void sendHelp(CommandSender sender) {
      sender.sendMessage("§e/thesieutoc top total:§f Tính tổng top từ trước đến nay");
      sender.sendMessage("§e/thesieutoc top daily:§f Tính top của hôm nay");
      sender.sendMessage("§e/thesieutoc top weekly:§f Tính top của tuần này");
      sender.sendMessage("§e/thesieutoc top month:§f Tính top của tháng này");
      sender.sendMessage("§e/thesieutoc top year:§f Tính top của năm nay");
   }

   public void printTop(CommandSender sender, String type, Date date) {
      Task.asyncTask(() -> {
         Thesieutoc.getInstance().THESIEUTOP.updatePlaceholder();
         String placeholder_format = Language.get("placeholder.top");
         SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

         for(int top_index = 1; top_index <= 10; ++top_index) {
            if (!type.isEmpty()) {
               JsonObject json = (JsonObject)Thesieutoc.getInstance().THESIEUTOP.cache.getOrDefault(type + "_" + top_index, new JsonObject());
               if (!json.has("name")) {
                  sender.sendMessage(MessageFormat.format(Language.get("placeholder.top_empty"), top_index));
               } else {
                  sender.sendMessage(MessageFormat.format(placeholder_format, top_index, json.get("name").getAsString(), json.get("value").getAsInt()));
               }
            }

            if (date != null) {
               String key = "top_" + dateformat.format(date) + "_" + top_index;
               if (!Thesieutoc.getInstance().THESIEUTOP.cache.containsKey(key)) {
                  List<Transaction> dateTrans = TransactionManager.forDate(Thesieutoc.getInstance().THESIEUTOP.allTimeTrans, date);
                  Thesieutoc.getInstance().THESIEUTOP.top(dateTrans, key);
               }

               if (!Thesieutoc.getInstance().THESIEUTOP.cache.containsKey(key)) {
                  sender.sendMessage(MessageFormat.format(Language.get("placeholder.top_empty"), top_index));
               } else {
                  JsonObject jsonx = (JsonObject)Thesieutoc.getInstance().THESIEUTOP.cache.get(key);
                  sender.sendMessage(MessageFormat.format(placeholder_format, top_index, jsonx.get("name").getAsString(), jsonx.get("value").getAsInt()));
               }
            }
         }

      });
   }

   public List<Long> day(Date date) {
      List<Long> dd = new LinkedList();
      date.setHours(0);
      date.setMinutes(0);
      date.setSeconds(0);
      dd.add(date.getTime());
      date.setHours(23);
      date.setMinutes(59);
      date.setSeconds(59);
      dd.add(date.getTime());
      return dd;
   }
}
