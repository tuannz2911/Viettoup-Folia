package net.thesieutoc.api;

import java.util.HashMap;
import net.thesieutoc.internal.TSTI;
import org.bukkit.entity.Player;

public class ThesieutocAPI {
   private static HashMap<String, Card> promptCard = new HashMap();

   public static void processCard(Player p, Card card) {
      TSTI.processCard(p, card);
   }

   public static Card getPromptCard(Player p) {
      if (!promptCard.containsKey(p.getUniqueId().toString())) {
         promptCard.put(p.getUniqueId().toString(), (new Card()).player(p.getName()));
      }

      return (Card)promptCard.get(p.getUniqueId().toString());
   }

   public static void updatePromptCard(Player p, Card card) {
      promptCard.put(p.getUniqueId().toString(), card);
   }

   public static void removePromptCard(Player p) {
      promptCard.remove(p.getUniqueId().toString());
   }
}
