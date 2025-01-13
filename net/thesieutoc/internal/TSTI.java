package net.thesieutoc.internal;

import com.google.gson.JsonObject;
import java.text.MessageFormat;
import java.util.HashMap;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.api.Card;
import net.thesieutoc.api.CardPrice;
import net.thesieutoc.api.ThesieutocAPI;
import net.thesieutoc.config.Config;
import net.thesieutoc.config.Language;
import net.thesieutoc.event.PlayerCardProcessEvent;
import net.thesieutoc.utils.Debug;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Utils;
import org.bukkit.entity.Player;

public class TSTI {
   private static HashMap<String, Long> trongHeThong = new HashMap();

   public static void processCard(Player p, Card card) {
      if (card.cardPrice() > 0 && !card.cardType().isEmpty() && !card.SERIAL().isEmpty() && !card.PIN().isEmpty()) {
         if (Config.debug()) {
            Debug.write("yeu cau nap the", card);
         }

         p.sendMessage(Language.get("nap_the_dang_xu_ly"));
         JsonObject web_response = requestCardTransaction(Config.APIKey(), Config.APISecret(), card.cardType(), card.cardPrice(), card.SERIAL(), card.PIN());
         if (Config.debug()) {
            Debug.write("tra ket qua the = " + web_response, card);
         }

         if (web_response != null) {
            if (web_response.get("status").getAsString().equals("2")) {
               p.sendMessage(Language.get("nap_the_that_bai") + "§c | " + web_response.get("msg").getAsString());
               ThesieutocAPI.removePromptCard(p);
               return;
            }

            if (!web_response.get("status").getAsString().equals("00")) {
               p.sendMessage(Language.get("nap_the_that_bai"));
               p.sendMessage("§c" + web_response.get("msg").getAsString());
            }
         }

         if (web_response != null && web_response.has("transaction_id")) {
            String transactionID = web_response.get("transaction_id").getAsString();
            card.transID(transactionID);
            if (trongHeThong.containsKey(transactionID)) {
               p.sendMessage(Language.get("nap_the_that_bai") + "§c | Thẻ đã tồn tại trong hệ thống!");
            } else {
               trongHeThong.put(transactionID, System.currentTimeMillis());
               Task.syncTask(() -> {
                  new PlayerCardProcessEvent(p, card.SERIAL(), card.PIN(), card.cardType(), card.cardPrice(), web_response.get("randomMD5").getAsString(), System.currentTimeMillis());
               });
               ThesieutocAPI.removePromptCard(p);
               if (Config.debug()) {
                  Debug.write("queue the", card);
               }

               Thesieutoc.getInstance().doNotTouch().internalQueue(card);
            }
         } else {
            p.sendMessage(Language.get("nap_the_that_bai") + "§c | Không thể kết nối đến cổng nạp thẻ!");
            ThesieutocAPI.removePromptCard(p);
         }
      } else {
         String reason = "";
         if (card.cardPrice() <= 0) {
            reason = reason + " | Thiếu thông tin loại thẻ";
         }

         if (card.cardType().isEmpty()) {
            reason = reason + " | Thiếu thông tin mệnh giá";
         }

         if (card.SERIAL().isEmpty()) {
            reason = reason + " | Chưa nhập Seri";
         }

         if (card.PIN().isEmpty()) {
            reason = reason + " | Chưa nhập mã thẻ";
         }

         p.sendMessage(Language.get("nap_the_that_bai") + "§c" + reason);
         ThesieutocAPI.removePromptCard(p);
      }
   }

   private static JsonObject requestCardTransaction(String apiKey, String apiSecret, String cardType, int cardprice, String seri, String pin) {
      String url = "http://vnpt.thesieutoc.net/API/transaction?APIkey={0}&APIsecret={1}&mathe={2}&seri={3}&type={4}&menhgia={5}";
      url = MessageFormat.format(url, apiKey, apiSecret, pin, seri, cardType, CardPrice.getPrice(cardprice).getId());
      String randomMD5 = Utils.randomMD5();
      if (Config.customURL()) {
         url = Config.customURLValue();
         url = MessageFormat.format(url, apiKey, apiSecret, pin, seri, cardType, cardprice + "", randomMD5);
      }

      url = url.replace("\"", "");
      JsonObject json = Utils.fetchJsonResponse(url);
      if (json != null) {
         json.addProperty("randomMD5", randomMD5);
      }

      return json;
   }
}
