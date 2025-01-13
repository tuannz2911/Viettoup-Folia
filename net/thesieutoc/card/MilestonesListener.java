package net.thesieutoc.card;

import java.util.Iterator;
import java.util.List;
import net.thesieutoc.config.DonateMilestone;
import net.thesieutoc.event.PlayerCardChargedEvent;
import net.thesieutoc.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MilestonesListener implements Listener {
   @EventHandler
   public void a(PlayerCardChargedEvent e) {
      Player p = e.getPlayer();
      if (DonateMilestone.isEnable()) {
         Iterator var3 = DonateMilestone.getMilestones().iterator();

         while(true) {
            int milestone;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  milestone = (Integer)var3.next();
               } while(e.getTotalCharged() - e.getCardPrice() >= milestone);
            } while(e.getTotalCharged() < milestone);

            List<String> commands = DonateMilestone.getCommands(milestone);
            Iterator var6 = commands.iterator();

            while(var6.hasNext()) {
               String command = (String)var6.next();
               Utils.dispatchCommand(p, command);
            }
         }
      }
   }
}
