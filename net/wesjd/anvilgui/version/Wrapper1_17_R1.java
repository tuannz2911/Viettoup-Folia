package net.wesjd.anvilgui.version;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayOutExperience;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.inventory.Slot;
import net.wesjd.anvilgui.version.special.AnvilContainer1_17_1_R1;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Wrapper1_17_R1 implements VersionWrapper {
   private final boolean IS_ONE_SEVENTEEN_ONE = Bukkit.getBukkitVersion().contains("1.17.1");

   private int getRealNextContainerId(Player player) {
      return this.toNMS(player).nextContainerCounter();
   }

   public int getNextContainerId(Player player, VersionWrapper.AnvilContainerWrapper container) {
      return this.IS_ONE_SEVENTEEN_ONE ? ((AnvilContainer1_17_1_R1)container).getContainerId() : ((Wrapper1_17_R1.AnvilContainer)container).getContainerId();
   }

   public void handleInventoryCloseEvent(Player player) {
      CraftEventFactory.handleInventoryCloseEvent(this.toNMS(player));
      this.toNMS(player).o();
   }

   public void sendPacketOpenWindow(Player player, int containerId, Object guiTitle) {
      this.toNMS(player).b.sendPacket(new PacketPlayOutOpenWindow(containerId, Containers.h, (IChatBaseComponent)guiTitle));
   }

   public void sendPacketCloseWindow(Player player, int containerId) {
      this.toNMS(player).b.sendPacket(new PacketPlayOutCloseWindow(containerId));
   }

   public void sendPacketExperienceChange(Player player, int experienceLevel) {
      this.toNMS(player).b.sendPacket(new PacketPlayOutExperience(0.0F, 0, experienceLevel));
   }

   public void setActiveContainerDefault(Player player) {
      this.toNMS(player).bV = this.toNMS(player).bU;
   }

   public void setActiveContainer(Player player, VersionWrapper.AnvilContainerWrapper container) {
      this.toNMS(player).bV = (Container)container;
   }

   public void setActiveContainerId(VersionWrapper.AnvilContainerWrapper container, int containerId) {
   }

   public void addActiveContainerSlotListener(VersionWrapper.AnvilContainerWrapper container, Player player) {
      this.toNMS(player).initMenu((Container)container);
   }

   public VersionWrapper.AnvilContainerWrapper newContainerAnvil(Player player, Object guiTitle) {
      return (VersionWrapper.AnvilContainerWrapper)(this.IS_ONE_SEVENTEEN_ONE ? new AnvilContainer1_17_1_R1(player, this.getRealNextContainerId(player), (IChatBaseComponent)guiTitle) : new Wrapper1_17_R1.AnvilContainer(player, (IChatBaseComponent)guiTitle));
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
         super(Wrapper1_17_R1.this.getRealNextContainerId(player), ((CraftPlayer)player).getHandle().getInventory(), ContainerAccess.at(((CraftWorld)player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
         this.checkReachable = false;
         this.setTitle(guiTitle);
      }

      public void i() {
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
}
