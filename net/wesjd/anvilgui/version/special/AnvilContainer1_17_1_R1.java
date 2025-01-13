package net.wesjd.anvilgui.version.special;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Slot;
import net.wesjd.anvilgui.version.VersionWrapper;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AnvilContainer1_17_1_R1 extends ContainerAnvil implements VersionWrapper.AnvilContainerWrapper {
   public AnvilContainer1_17_1_R1(Player player, int containerId, IChatBaseComponent guiTitle) {
      super(containerId, ((CraftPlayer)player).getHandle().getInventory(), ContainerAccess.at(((CraftWorld)player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
      this.checkReachable = false;
      this.setTitle(guiTitle);
   }

   public void l() {
      Slot output = this.getSlot(2);
      if (!output.hasItem()) {
         Slot input = this.getSlot(0);
         if (input.hasItem()) {
            output.set(input.getItem().cloneItemStack());
         }
      }

      this.w.set(0);
      this.updateInventory();
      this.d();
   }

   public void b(EntityHuman player) {
   }

   protected void a(EntityHuman player, IInventory container) {
   }

   public int getContainerId() {
      return this.j;
   }

   public String getRenameText() {
      return this.v;
   }

   public void setRenameText(String text) {
      Slot inputLeft = this.getSlot(0);
      if (inputLeft.hasItem()) {
         inputLeft.getItem().a(new ChatComponentText(text));
      }

   }

   public Inventory getBukkitInventory() {
      return this.getBukkitView().getTopInventory();
   }
}
