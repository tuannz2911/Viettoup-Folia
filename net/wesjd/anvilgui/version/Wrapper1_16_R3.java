package net.wesjd.anvilgui.version;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.Container;
import net.minecraft.server.v1_16_R3.ContainerAccess;
import net.minecraft.server.v1_16_R3.ContainerAnvil;
import net.minecraft.server.v1_16_R3.Containers;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IInventory;
import net.minecraft.server.v1_16_R3.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_16_R3.PacketPlayOutExperience;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_16_R3.Slot;
import net.minecraft.server.v1_16_R3.World;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Wrapper1_16_R3 implements VersionWrapper {
   private int getRealNextContainerId(Player player) {
      return this.toNMS(player).nextContainerCounter();
   }

   public int getNextContainerId(Player player, VersionWrapper.AnvilContainerWrapper container) {
      return ((Wrapper1_16_R3.AnvilContainer)container).getContainerId();
   }

   public void handleInventoryCloseEvent(Player player) {
      CraftEventFactory.handleInventoryCloseEvent(this.toNMS(player));
      this.toNMS(player).o();
   }

   public void sendPacketOpenWindow(Player player, int containerId, Object guiTitle) {
      this.toNMS(player).playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, Containers.ANVIL, (IChatBaseComponent)guiTitle));
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
   }

   public void addActiveContainerSlotListener(VersionWrapper.AnvilContainerWrapper container, Player player) {
      ((Container)container).addSlotListener(this.toNMS(player));
   }

   public VersionWrapper.AnvilContainerWrapper newContainerAnvil(Player player, Object guiTitle) {
      return new Wrapper1_16_R3.AnvilContainer(player, (IChatBaseComponent)guiTitle);
   }

   public Object literalChatComponent(String content) {
      return new ChatComponentText(content);
   }

   public Object jsonChatComponent(String json) {
      return ChatSerializer.a(json);
   }

   private EntityPlayer toNMS(Player player) {
      return ((CraftPlayer)player).getHandle();
   }

   private class AnvilContainer extends ContainerAnvil implements VersionWrapper.AnvilContainerWrapper {
      public AnvilContainer(Player param2, IChatBaseComponent param3) {
         super(Wrapper1_16_R3.this.getRealNextContainerId(player), ((CraftPlayer)player).getHandle().inventory, ContainerAccess.at(((CraftWorld)player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
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
}
