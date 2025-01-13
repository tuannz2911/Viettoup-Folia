package net.thesieutoc;

import com.google.gson.JsonObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import net.thesieutoc.utils.FNum;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Transaction;

public class Thesieutop {
   public List<Transaction> allTimeTrans = new LinkedList();
   public HashMap<String, JsonObject> cache = new HashMap();

   public Thesieutop() {
      Task.asyncTask(this::updatePlaceholder, 0, Thesieutoc.getInstance().getConfig().getInt("placeholder_update", 300));
   }

   public int cacheValue(String key) {
      JsonObject json = (JsonObject)this.cache.getOrDefault(key, new JsonObject());
      return json.has("value") ? json.get("value").getAsInt() : 0;
   }

   public void updatePlaceholder() {
      this.allTimeTrans.clear();
      this.cache.clear();
      this.allTimeTrans = this.loadTransactions();
      Date now = new Date();
      List<Transaction> dailyTrans = TransactionManager.forDaily(this.allTimeTrans, now);
      List<Transaction> weeklyTrans = TransactionManager.forWeekly(this.allTimeTrans, now);
      List<Transaction> monthlyTrans = TransactionManager.forMonthly(this.allTimeTrans, now);
      List<Transaction> yearTrans = TransactionManager.forYear(this.allTimeTrans, now);
      this.total(this.allTimeTrans, "total_alltime");
      this.total(dailyTrans, "total_daily");
      this.total(weeklyTrans, "total_weekly");
      this.total(monthlyTrans, "total_monthly");
      this.total(yearTrans, "total_year");
      this.top(this.allTimeTrans, "top_alltime");
      this.top(dailyTrans, "top_daily");
      this.top(weeklyTrans, "top_weekly");
      this.top(monthlyTrans, "top_monthly");
      this.top(yearTrans, "top_year");
   }

   private List<Transaction> loadTransactions() {
      List<Transaction> transactions = new LinkedList();
      if (Thesieutoc.getInstance().getConfig().getBoolean("mysql.enable")) {
         transactions = Thesieutoc.getDatabase().transactions();
      } else {
         try {
            File log = new File(Thesieutoc.getInstance().getDataFolder(), "log_success.txt");
            if (!log.exists()) {
               log.createNewFile();
            }

            Scanner scanner = new Scanner(log);

            while(scanner.hasNextLine()) {
               String line = scanner.nextLine();
               if (line.contains("thanh cong")) {
                  SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                  String f = line.substring(1).split("\\] ")[0];
                  Date date = df.parse(f);
                  line = line.split("] ")[1];
                  String name = line.split(" \\| ")[0];
                  int cash = FNum.ri(line.split(" \\| ")[3]);
                  Transaction transaction = new Transaction(name, cash, date);
                  ((List)transactions).add(transaction);
               }
            }
         } catch (Exception var11) {
            var11.printStackTrace();
         }
      }

      return (List)transactions;
   }

   public void total(List<Transaction> transactions, String a_) {
      int cash = 0;

      Transaction transaction;
      for(Iterator var4 = transactions.iterator(); var4.hasNext(); cash += transaction.getAmount()) {
         transaction = (Transaction)var4.next();
      }

      JsonObject json = new JsonObject();
      json.addProperty("value", cash);
      this.cache.put(a_, json);
   }

   public void top(List<Transaction> transactions, String a_) {
      HashMap<String, Integer> cash = new HashMap();
      Iterator var4 = transactions.iterator();

      while(var4.hasNext()) {
         Transaction transaction = (Transaction)var4.next();
         cash.put(transaction.getPlayerName(), (Integer)cash.getOrDefault(transaction.getPlayerName(), 0) + transaction.getAmount());
      }

      LinkedHashMap<Integer, String> totalSorted = sortByComparator(cash, false, 10);
      Iterator var10 = totalSorted.keySet().iterator();

      while(var10.hasNext()) {
         int top_index = (Integer)var10.next();
         String player_name = (String)totalSorted.get(top_index);
         JsonObject json = new JsonObject();
         json.addProperty("name", player_name);
         json.addProperty("value", (Number)cash.get(player_name));
         this.cache.put(a_ + "_" + top_index, json);
      }

   }

   public static LinkedHashMap<Integer, String> sortByComparator(Map<String, Integer> unsortMap, boolean order, int maxtop) {
      List<Entry<String, Integer>> list = new LinkedList(unsortMap.entrySet());
      list.sort((o1, o2) -> {
         return order ? ((Integer)o1.getValue()).compareTo((Integer)o2.getValue()) : ((Integer)o2.getValue()).compareTo((Integer)o1.getValue());
      });
      LinkedHashMap<Integer, String> sortedMap = new LinkedHashMap();

      for(int i = 1; i <= Math.min(maxtop, list.size()); ++i) {
         Entry<String, Integer> entry = (Entry)list.get(i - 1);
         sortedMap.put(i, (String)entry.getKey());
      }

      return sortedMap;
   }
}
