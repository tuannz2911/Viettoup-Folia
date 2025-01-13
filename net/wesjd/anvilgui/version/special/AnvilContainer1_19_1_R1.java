package net.wesjd.anvilgui.version.special;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Slot;
import net.wesjd.anvilgui.version.VersionWrapper;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AnvilContainer1_19_1_R1 extends ContainerAnvil implements VersionWrapper.AnvilContainerWrapper {
   public AnvilContainer1_19_1_R1(Player player, int containerId, IChatBaseComponent guiTitle) {
      super(containerId, ((CraftPlayer)player).getHandle().fA(), ContainerAccess.a(((CraftWorld)player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
      this.checkReachable = false;
      this.setTitle(guiTitle);
   }

   public void l() {
      Slot output = this.b(2);
      if (!output.f()) {
         output.e(this.b(0).e().o());
      }

      this.w.a(0);
      this.b();
      this.d();
   }

   public void b(EntityHuman player) {
   }

   protected void a(EntityHuman player, IInventory container) {
   }

   public String getRenameText() {
      return this.v;
   }

   public void setRenameText(String text) {
      Slot inputLeft = this.b(0);
      if (inputLeft.f()) {
         inputLeft.e().a(IChatBaseComponent.b(text));
      }

   }

   public int getContainerId() {
      return this.j;
   }

   public Inventory getBukkitInventory() {
      return this.getBukkitView().getTopInventory();
   }
}
