package net.wesjd.anvilgui.version;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.component.DataComponents;
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
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R4.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class Wrapper1_20_R4 implements VersionWrapper {
   private int getRealNextContainerId(Player player) {
      return this.toNMS(player).nextContainerCounter();
   }

   private EntityPlayer toNMS(Player player) {
      return ((CraftPlayer)player).getHandle();
   }

   public int getNextContainerId(Player player, VersionWrapper.AnvilContainerWrapper container) {
      return ((Wrapper1_20_R4.AnvilContainer)container).getContainerId();
   }

   public void handleInventoryCloseEvent(Player player) {
      CraftEventFactory.handleInventoryCloseEvent(this.toNMS(player));
      this.toNMS(player).s();
   }

   public void sendPacketOpenWindow(Player player, int containerId, Object inventoryTitle) {
      this.toNMS(player).c.b(new PacketPlayOutOpenWindow(containerId, Containers.i, (IChatBaseComponent)inventoryTitle));
   }

   public void sendPacketCloseWindow(Player player, int containerId) {
      this.toNMS(player).c.b(new PacketPlayOutCloseWindow(containerId));
   }

   public void sendPacketExperienceChange(Player player, int experienceLevel) {
      this.toNMS(player).c.b(new PacketPlayOutExperience(0.0F, 0, experienceLevel));
   }

   public void setActiveContainerDefault(Player player) {
      this.toNMS(player).cb = this.toNMS(player).ca;
   }

   public void setActiveContainer(Player player, VersionWrapper.AnvilContainerWrapper container) {
      this.toNMS(player).cb = (Container)container;
   }

   public void setActiveContainerId(VersionWrapper.AnvilContainerWrapper container, int containerId) {
   }

   public void addActiveContainerSlotListener(VersionWrapper.AnvilContainerWrapper container, Player player) {
      this.toNMS(player).a((Container)container);
   }

   public VersionWrapper.AnvilContainerWrapper newContainerAnvil(Player player, Object title) {
      return new Wrapper1_20_R4.AnvilContainer(player, this.getRealNextContainerId(player), (IChatBaseComponent)title);
   }

   public Object literalChatComponent(String content) {
      return IChatBaseComponent.b(content);
   }

   public Object jsonChatComponent(String json) {
      return ChatSerializer.a(json, IRegistryCustom.b);
   }

   private static class AnvilContainer extends ContainerAnvil implements VersionWrapper.AnvilContainerWrapper {
      public AnvilContainer(Player player, int containerId, IChatBaseComponent guiTitle) {
         super(containerId, ((CraftPlayer)player).getHandle().gc(), ContainerAccess.a(((CraftWorld)player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
         this.checkReachable = false;
         this.setTitle(guiTitle);
      }

      public void m() {
         Slot output = this.b(2);
         if (!output.h()) {
            output.f(this.b(0).g().s());
         }

         this.w.a(0);
         this.b();
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
         Slot inputLeft = this.b(0);
         if (inputLeft.h()) {
            inputLeft.g().b(DataComponents.g, IChatBaseComponent.b(text));
         }

      }

      public Inventory getBukkitInventory() {
         return this.getBukkitView().getTopInventory();
      }
   }
}
