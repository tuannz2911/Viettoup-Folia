package net.wesjd.anvilgui.version;

import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.Blocks;
import net.minecraft.server.v1_9_R2.ChatMessage;
import net.minecraft.server.v1_9_R2.Container;
import net.minecraft.server.v1_9_R2.ContainerAnvil;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_9_R2.PacketPlayOutExperience;
import net.minecraft.server.v1_9_R2.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_9_R2.Slot;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Wrapper1_9_R2 implements VersionWrapper {
   public int getNextContainerId(Player player, VersionWrapper.AnvilContainerWrapper container) {
      return this.toNMS(player).nextContainerCounter();
   }

   public void handleInventoryCloseEvent(Player player) {
      CraftEventFactory.handleInventoryCloseEvent(this.toNMS(player));
      this.toNMS(player).s();
   }

   public void sendPacketOpenWindow(Player player, int containerId, Object guiTitle) {
      this.toNMS(player).playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage(Blocks.ANVIL.a() + ".name", new Object[0])));
   }

   public void sendPacketCloseWindow(Player player, int containerId) {
      this.toNMS(player).playerConnection.sendPacket(new PacketPlayOutCloseWindow(containerId));
   }

   public void sendPacketExperienceChange(Player player, int experienceLevel) {
      this.toNMS(player).playerConnection.sendPacket(new PacketPlayOutExperience(0.0F, 0, experienceLevel));
   }

   public void setActiveContainerDefault(Player player) {
      this.toNMS(player).activeContainer = this.toNMS(player).defaultContainer;
   }

   public void setActiveContainer(Player player, VersionWrapper.AnvilContainerWrapper container) {
      this.toNMS(player).activeContainer = (Container)container;
   }

   public void setActiveContainerId(VersionWrapper.AnvilContainerWrapper container, int containerId) {
      ((Container)container).windowId = containerId;
   }

   public void addActiveContainerSlotListener(VersionWrapper.AnvilContainerWrapper container, Player player) {
      ((Container)container).addSlotListener(this.toNMS(player));
   }

   public VersionWrapper.AnvilContainerWrapper newContainerAnvil(Player player, Object guiTitle) {
      return new Wrapper1_9_R2.AnvilContainer(this.toNMS(player));
   }

   public boolean isCustomTitleSupported() {
      return false;
   }

   public Object literalChatComponent(String content) {
      return null;
   }

   public Object jsonChatComponent(String json) {
      return null;
   }

   private EntityPlayer toNMS(Player player) {
      return ((CraftPlayer)player).getHandle();
   }

   private class AnvilContainer extends ContainerAnvil implements VersionWrapper.AnvilContainerWrapper {
      public AnvilContainer(EntityHuman param2) {
         super(entityhuman.inventory, entityhuman.world, new BlockPosition(0, 0, 0), entityhuman);
      }

      public void e() {
         Slot output = this.getSlot(2);
         if (!output.hasItem()) {
            Slot input = this.getSlot(0);
            if (input.hasItem()) {
               output.set(input.getItem().cloneItemStack());
            }
         }

         this.a = 0;
         this.b();
      }

      public boolean a(EntityHuman entityhuman) {
         return true;
      }

      public void b(EntityHuman entityhuman) {
      }

      public Inventory getBukkitInventory() {
         return this.getBukkitView().getTopInventory();
      }
   }
}
