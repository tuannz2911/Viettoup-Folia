package net.wesjd.anvilgui.version.special;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.ContainerAccess;
import net.minecraft.server.v1_14_R1.ContainerAnvil;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.IInventory;
import net.minecraft.server.v1_14_R1.Slot;
import net.minecraft.server.v1_14_R1.World;
import net.wesjd.anvilgui.version.VersionWrapper;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AnvilContainer1_14_4_R1 extends ContainerAnvil implements VersionWrapper.AnvilContainerWrapper {
   public AnvilContainer1_14_4_R1(Player player, int containerId, IChatBaseComponent guiTitle) {
      super(containerId, ((CraftPlayer)player).getHandle().inventory, ContainerAccess.at(((CraftWorld)player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
      this.checkReachable = false;
      this.setTitle(guiTitle);
   }

   public void e() {
      Slot output = this.getSlot(2);
      if (!output.hasItem()) {
         Slot input = this.getSlot(0);
         if (input.hasItem()) {
            output.set(input.getItem().cloneItemStack());
         }
      }

      this.levelCost.set(0);
      this.c();
   }

   public void b(EntityHuman entityhuman) {
   }

   protected void a(EntityHuman entityhuman, World world, IInventory iinventory) {
   }

   public int getContainerId() {
      return this.windowId;
   }

   public String getRenameText() {
      return this.renameText;
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
