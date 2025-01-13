package net.wesjd.anvilgui.version;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.Container;
import net.minecraft.server.v1_14_R1.ContainerAccess;
import net.minecraft.server.v1_14_R1.ContainerAnvil;
import net.minecraft.server.v1_14_R1.Containers;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.IInventory;
import net.minecraft.server.v1_14_R1.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_14_R1.PacketPlayOutExperience;
import net.minecraft.server.v1_14_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_14_R1.Slot;
import net.minecraft.server.v1_14_R1.World;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import net.wesjd.anvilgui.version.special.AnvilContainer1_14_4_R1;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Wrapper1_14_R1 implements VersionWrapper {
   private final boolean IS_ONE_FOURTEEN = Bukkit.getBukkitVersion().contains("1.14.4");

   private int getRealNextContainerId(Player player) {
      return this.toNMS(player).nextContainerCounter();
   }

   public int getNextContainerId(Player player, VersionWrapper.AnvilContainerWrapper container) {
      return this.IS_ONE_FOURTEEN ? ((AnvilContainer1_14_4_R1)container).getContainerId() : ((Wrapper1_14_R1.AnvilContainer)container).getContainerId();
   }

   public void handleInventoryCloseEvent(Player player) {
      CraftEventFactory.handleInventoryCloseEvent(this.toNMS(player));
      this.toNMS(player).m();
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
      return (VersionWrapper.AnvilContainerWrapper)(this.IS_ONE_FOURTEEN ? new AnvilContainer1_14_4_R1(player, this.getRealNextContainerId(player), (IChatBaseComponent)guiTitle) : new Wrapper1_14_R1.AnvilContainer(player, (IChatBaseComponent)guiTitle));
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
         super(Wrapper1_14_R1.this.getRealNextContainerId(player), ((CraftPlayer)player).getHandle().inventory, ContainerAccess.at(((CraftWorld)player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
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

         this.levelCost.a(0);
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
