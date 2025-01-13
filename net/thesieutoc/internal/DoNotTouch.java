package net.thesieutoc.internal;

import com.google.gson.JsonObject;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.api.Card;
import net.thesieutoc.config.Config;
import net.thesieutoc.config.Language;
import net.thesieutoc.event.PlayerCardChargedEvent;
import net.thesieutoc.utils.Debug;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DoNotTouch {
   private final LinkedHashMap<String, Card> queue = new LinkedHashMap();
   private final HashMap<String, Long> taisaolaiDupeNhi = new HashMap();

   public void init() {
      if (Config.callBack()) {
         if (!Config.customURL()) {
            Task.asyncTask(() -> {
               Iterator var1 = this.queue.keySet().iterator();
               if (var1.hasNext()) {
                  String internalID = (String)var1.next();
                  Card card = (Card)this.queue.get(internalID);
                  Player p = Bukkit.getPlayer(card.player());
                  if (p != null && p.isOnline()) {
                     if (this.taisaolaiDupeNhi.containsKey(card.transID())) {
                        if (Config.debug()) {
                           Debug.write("cancel do co 1 card khac trung trans id", card);
                        }

                        this.queue.remove(internalID);
                     } else {
                        JsonObject web_response = this.fetchCardStatus(Config.APIKey(), Config.APISecret(), card.transID());
                        if (web_response != null) {
                           String status = web_response.get("status").getAsString();
                           if (Config.debug()) {
                              Debug.write("dang duyet the, status = " + status, card);
                           }

                           if (status.equals("-9")) {
                              if (card.retry() > 30) {
                                 Debug.write("the bi treo 5p", card);
                                 this.queue.remove(internalID);
                              } else {
                                 card.retry(card.retry() + 1);
                                 this.queue.put(internalID, card);
                              }
                           } else {
                              if (status.equals("-10")) {
                                 p.sendMessage(Language.get("nap_the_that_bai"));
                                 card.callbackMessage("that bai");
                                 if (Config.debug()) {
                                    Debug.write("nap the that bai", card);
                                 }
                              }

                              if (status.equals("10")) {
                                 p.sendMessage(Language.get("sai_menh_gia"));
                                 card.callbackMessage("sai menh gia");
                              }

                              if (status.equals("00")) {
                                 this.taisaolaiDupeNhi.put(card.transID(), System.currentTimeMillis());
                                 if (p == null || !p.isOnline()) {
                                    Debug.write("the thanh cong nhung " + card.player() + " offline | " + card.cardType() + " | " + card.cardPrice() + " | " + card.SERIAL() + " | " + card.PIN() + " | " + card.internalID());
                                    return;
                                 }

                                 if (card.cardPrice() == 0) {
                                    Bukkit.getLogger().warning("[Thesieutoc] §cThe duoc duyet thanh cong nhung thieu gia tri the!");
                                    Bukkit.getLogger().warning("[Thesieutoc] " + card.player() + "  TransID: " + card.transID());
                                    return;
                                 }

                                 if (Config.debug()) {
                                    Debug.write("nap the thanh cong", card);
                                 }

                                 p.sendMessage(MessageFormat.format(Language.get("nap_the_thanh_cong"), card.cardPrice()));
                                 this.chargeSuccess(p, card);
                              }

                              Thesieutoc.getDatabase().writeLog(p, card);
                              this.queue.remove(internalID);
                           }
                        }
                     }
                  }
               }
            }, 100, 200);
         }
      }
   }

   public void internalQueue(Card card) {
      if (Config.callBack()) {
         if (!Config.customURL()) {
            this.queue.put(card.internalID(), card);
         }
      }
   }

   public void chargeSuccess(Player p, Card card) {
      if (!Thesieutoc.getInstance().getConfig().contains("card.command." + card.cardPrice())) {
         Bukkit.getLogger().warning("[Thesieutoc] Khong co lenh thuc thi cho card menh gia " + card.cardPrice() + " trong config, vui long kiem tra!");
      } else {
         List<String> commands = Thesieutoc.getInstance().getConfig().getStringList("card.command." + card.cardPrice());
         if (commands.isEmpty()) {
            Bukkit.getLogger().warning("[Thesieutoc] Khong co lenh thuc thi cho card menh gia " + card.cardPrice() + " VND trong config, vui long kiem tra!");
         } else {
            Task.syncTask(() -> {
               Iterator var2 = commands.iterator();

               while(var2.hasNext()) {
                  String command = (String)var2.next();
                  Utils.dispatchCommand(p, command);
               }

            });
         }
      }

      card.callbackMessage("thanh cong");
      int total_charged = Thesieutoc.getDatabase().getPlayerTotalCharged(p) + card.cardPrice();
      Task.syncTask(() -> {
         new PlayerCardChargedEvent(p, card.cardType(), card.cardPrice(), total_charged);
      });
   }

   private JsonObject fetchCardStatus(String apiKey, String apiSecret, String transactionID) {
      String url = MessageFormat.format("http://vnpt.thesieutoc.net/API/get_status_card.php?APIkey={0}&APIsecret={1}&transaction_id={2}", apiKey, apiSecret, transactionID);
      return Utils.fetchJsonResponse(url);
   }
}
