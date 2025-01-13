package net.thesieutoc.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {
   private static final String MATCH = "(?ium)^(player:|op:|console:|)(.*)$";

   public static HashMap<Integer, String> sortByComparator(Map<String, Integer> unsortMap, boolean order, int maxtop) {
      List<Entry<String, Integer>> list = new LinkedList(unsortMap.entrySet());
      list.sort((o1, o2) -> {
         return order ? ((Integer)o1.getValue()).compareTo((Integer)o2.getValue()) : ((Integer)o2.getValue()).compareTo((Integer)o1.getValue());
      });
      HashMap<Integer, String> sortedMap = new LinkedHashMap();
      Iterator var5 = list.iterator();

      while(true) {
         while(var5.hasNext()) {
            Entry<String, Integer> entry = (Entry)var5.next();

            for(int i = 1; i <= maxtop; ++i) {
               if (!sortedMap.containsKey(i)) {
                  sortedMap.put(i, (String)entry.getKey());
                  break;
               }
            }
         }

         return sortedMap;
      }
   }

   public static String randomMD5() {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         byte[] messageDigest = md.digest((System.currentTimeMillis() + (long)FNum.randomInt(0, 999999) + "").getBytes("UTF-8"));
         BigInteger no = new BigInteger(1, messageDigest);
         StringBuilder hashtext = new StringBuilder(no.toString(16));

         while(hashtext.length() < 32) {
            hashtext.insert(0, "0");
         }

         return hashtext.toString();
      } catch (Exception var4) {
         return "";
      }
   }

   public static JsonObject fetchJsonResponse(String url) {
      try {
         CookieHandler.setDefault(new CookieManager((CookieStore)null, CookiePolicy.ACCEPT_ALL));
         HttpURLConnection connection = (HttpURLConnection)(new URL(url)).openConnection();
         connection.setRequestMethod("GET");
         connection.setRequestProperty("User-Agent", "Mozilla/5.0");
         connection.setDoInput(true);
         connection.setConnectTimeout(7000);
         connection.setReadTimeout(7000);
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String response = (String)reader.lines().collect(Collectors.joining());
         reader.close();
         connection.disconnect();
         return (JsonObject)(new JsonParser()).parse(response);
      } catch (Exception var4) {
         return null;
      }
   }

   public static void dispatchCommand(Player player, String command) {
      Task.syncTask(() -> {
         String type = command.replaceAll("(?ium)^(player:|op:|console:|)(.*)$", "$1").replace(":", "").toLowerCase();
         String cmd = command.replaceAll("(?ium)^(player:|op:|console:|)(.*)$", "$2").replaceAll("(?ium)([{]Player[}])", player.getName());
         byte var5 = -1;
         switch(type.hashCode()) {
         case -985752863:
            if (type.equals("player")) {
               var5 = 2;
            }
            break;
         case 0:
            if (type.equals("")) {
               var5 = 1;
            }
            break;
         case 3553:
            if (type.equals("op")) {
               var5 = 0;
            }
            break;
         case 951510359:
            if (type.equals("console")) {
               var5 = 3;
            }
         }

         switch(var5) {
         case 0:
            if (player.isOp()) {
               player.performCommand(cmd);
            } else {
               player.setOp(true);
               player.performCommand(cmd);
               player.setOp(false);
            }
            break;
         case 1:
         case 2:
            player.performCommand(cmd);
            break;
         case 3:
         default:
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
         }

      });
   }
}
