package net.thesieutoc.menu.anvil;

import net.thesieutoc.Thesieutoc;
import net.thesieutoc.api.Card;
import net.thesieutoc.api.ThesieutocAPI;
import net.thesieutoc.config.Language;
import net.thesieutoc.utils.Task;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class AnvilMenu_seripin implements Listener {
   public static void seri(Player p) {
      (new AnvilGUI.Builder()).onClick((slot, stateSnapshot) -> {
         String reply = stateSnapshot.getText();
         reply = reply.replaceAll(Language.get("anvilmenu_nhap_seri"), "").replaceAll(" ", "");
         Card card = ThesieutocAPI.getPromptCard(p);
         card.SERIAL(reply);
         ThesieutocAPI.updatePromptCard(p, card);
         return AnvilGUI.Response.close();
      }).onClose((player) -> {
         Task.syncTask(() -> {
            pin(p);
         });
      }).title(Language.get("anvilmenu_nhap_seri")).itemLeft(new ItemStack(Material.DIAMOND)).text(Language.get("anvilmenu_nhap_seri")).plugin(Thesieutoc.getInstance()).open(p);
   }

   public static void pin(Player p) {
      (new AnvilGUI.Builder()).onClick((slot, stateSnapshot) -> {
         String reply = stateSnapshot.getText();
         reply = reply.replaceAll(Language.get("anvilmenu_nhap_pin"), "").replaceAll(" ", "");
         Card card = ThesieutocAPI.getPromptCard(p);
         card.PIN(reply);
         ThesieutocAPI.removePromptCard(p);
         Task.asyncTask(() -> {
            ThesieutocAPI.processCard(p, card);
         });
         return AnvilGUI.Response.close();
      }).title(Language.get("anvilmenu_nhap_pin")).itemLeft(new ItemStack(Material.DIAMOND)).text(Language.get("anvilmenu_nhap_pin")).plugin(Thesieutoc.getInstance()).open(p);
   }
}
