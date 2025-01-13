package net.thesieutoc.menu.chat;

import java.util.HashMap;
import net.thesieutoc.api.Card;
import net.thesieutoc.api.ThesieutocAPI;
import net.thesieutoc.config.Language;
import net.thesieutoc.utils.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMenu implements Listener {
   private static HashMap<String, String> prompt = new HashMap();

   public static void prompt(Player p) {
      prompt.put(p.getUniqueId().toString(), "seri");
      p.sendMessage(Language.get("chat_nhap_seri"));
   }

   @EventHandler
   public void chat(AsyncPlayerChatEvent e) {
      Player p = e.getPlayer();
      if (prompt.containsKey(p.getUniqueId().toString())) {
         String msg = e.getMessage();
         e.setCancelled(true);
         e.setMessage("");
         String promptState = (String)prompt.get(p.getUniqueId().toString());
         if (msg.equalsIgnoreCase("huy") || msg.contains(" ")) {
            prompt.remove(p.getUniqueId().toString());
            p.sendMessage(Language.get("nap_the_huy"));
            return;
         }

         Card card;
         if (promptState.equalsIgnoreCase("seri")) {
            card = ThesieutocAPI.getPromptCard(p);
            card.SERIAL(msg);
            ThesieutocAPI.updatePromptCard(p, card);
            prompt.put(p.getUniqueId().toString(), "pin");
            p.sendMessage("");
            p.sendMessage(Language.get("chat_nhap_pin"));
            return;
         }

         if (promptState.equalsIgnoreCase("pin")) {
            prompt.remove(p.getUniqueId().toString());
            card = ThesieutocAPI.getPromptCard(p);
            card.PIN(msg);
            ThesieutocAPI.updatePromptCard(p, card);
            Task.asyncTask(() -> {
               ThesieutocAPI.processCard(p, card);
            });
         }
      }

   }
}
