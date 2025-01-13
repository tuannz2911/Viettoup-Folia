package net.wesjd.anvilgui;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import net.wesjd.anvilgui.version.VersionMatcher;
import net.wesjd.anvilgui.version.VersionWrapper;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.geysermc.geyser.api.GeyserApi;

public class AnvilGUI {
   private static final VersionWrapper WRAPPER = (new VersionMatcher()).match();
   private static final ItemStack AIR;
   private final Plugin plugin;
   private final Player player;
   private final Executor mainThreadExecutor;
   private final Object titleComponent;
   private final ItemStack[] initialContents;
   private final boolean preventClose;
   private final boolean geyserCompatibility;
   private final Set<Integer> interactableSlots;
   private final Consumer<AnvilGUI.StateSnapshot> closeListener;
   private final boolean concurrentClickHandlerExecution;
   private final AnvilGUI.ClickHandler clickHandler;
   private int containerId;
   private Inventory inventory;
   private final AnvilGUI.ListenUp listener;
   private boolean open;
   private VersionWrapper.AnvilContainerWrapper container;

   private static ItemStack itemNotNull(ItemStack stack) {
      return stack == null ? AIR : stack;
   }

   private AnvilGUI(Plugin plugin, Player player, Executor mainThreadExecutor, Object titleComponent, ItemStack[] initialContents, boolean preventClose, boolean geyserCompatibility, Set<Integer> interactableSlots, Consumer<AnvilGUI.StateSnapshot> closeListener, boolean concurrentClickHandlerExecution, AnvilGUI.ClickHandler clickHandler) {
      this.listener = new AnvilGUI.ListenUp();
      this.plugin = plugin;
      this.player = player;
      this.mainThreadExecutor = mainThreadExecutor;
      this.titleComponent = titleComponent;
      this.initialContents = initialContents;
      this.preventClose = preventClose;
      this.geyserCompatibility = geyserCompatibility;
      this.interactableSlots = Collections.unmodifiableSet(interactableSlots);
      this.closeListener = closeListener;
      this.concurrentClickHandlerExecution = concurrentClickHandlerExecution;
      this.clickHandler = clickHandler;
   }

   private void openInventory() {
      Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);
      this.container = WRAPPER.newContainerAnvil(this.player, this.titleComponent);
      this.inventory = this.container.getBukkitInventory();

      for(int i = 0; i < this.initialContents.length; ++i) {
         this.inventory.setItem(i, this.initialContents[i]);
      }

      this.containerId = WRAPPER.getNextContainerId(this.player, this.container);
      WRAPPER.handleInventoryCloseEvent(this.player);
      WRAPPER.sendPacketOpenWindow(this.player, this.containerId, this.titleComponent);
      WRAPPER.setActiveContainer(this.player, this.container);
      WRAPPER.setActiveContainerId(this.container, this.containerId);
      WRAPPER.addActiveContainerSlotListener(this.container, this.player);
      if (this.geyserCompatibility && this.plugin.getServer().getPluginManager().getPlugin("Geyser-Spigot") != null && this.plugin.getServer().getPluginManager().getPlugin("Geyser-Spigot").isEnabled() && GeyserApi.api().isBedrockPlayer(this.player.getUniqueId())) {
         WRAPPER.sendPacketExperienceChange(this.player, 20);
      }

      this.open = true;
   }

   public void closeInventory() {
      this.closeInventory(true);
   }

   private void closeInventory(boolean sendClosePacket) {
      if (this.open) {
         this.open = false;
         HandlerList.unregisterAll(this.listener);
         if (sendClosePacket) {
            WRAPPER.handleInventoryCloseEvent(this.player);
            WRAPPER.setActiveContainerDefault(this.player);
            WRAPPER.sendPacketCloseWindow(this.player, this.containerId);
         }

         if (this.geyserCompatibility && this.plugin.getServer().getPluginManager().getPlugin("Geyser-Spigot") != null && this.plugin.getServer().getPluginManager().getPlugin("Geyser-Spigot").isEnabled() && GeyserApi.api().isBedrockPlayer(this.player.getUniqueId())) {
            WRAPPER.sendPacketExperienceChange(this.player, this.player.getLevel());
         }

         if (this.closeListener != null) {
            this.closeListener.accept(AnvilGUI.StateSnapshot.fromAnvilGUI(this));
         }

      }
   }

   public void setTitle(String literalTitle, boolean preserveRenameText) {
      Validate.notNull(literalTitle, "literalTitle cannot be null");
      this.setTitle(WRAPPER.literalChatComponent(literalTitle), preserveRenameText);
   }

   public void setJsonTitle(String json, boolean preserveRenameText) {
      Validate.notNull(json, "json cannot be null");
      this.setTitle(WRAPPER.jsonChatComponent(json), preserveRenameText);
   }

   private void setTitle(Object title, boolean preserveRenameText) {
      if (WRAPPER.isCustomTitleSupported()) {
         String renameText = this.container.getRenameText();
         WRAPPER.sendPacketOpenWindow(this.player, this.containerId, title);
         if (preserveRenameText) {
            this.container.setRenameText(renameText == null ? "" : renameText);
         }

      }
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   // $FF: synthetic method
   AnvilGUI(Plugin x0, Player x1, Executor x2, Object x3, ItemStack[] x4, boolean x5, boolean x6, Set x7, Consumer x8, boolean x9, AnvilGUI.ClickHandler x10, Object x11) {
      this(x0, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10);
   }

   static {
      AIR = new ItemStack(Material.AIR);
   }

   @FunctionalInterface
   public interface ClickHandler extends BiFunction<Integer, AnvilGUI.StateSnapshot, CompletableFuture<List<AnvilGUI.ResponseAction>>> {
   }

   private class ListenUp implements Listener {
      private boolean clickHandlerRunning;

      private ListenUp() {
         this.clickHandlerRunning = false;
      }

      @EventHandler
      public void onInventoryClick(InventoryClickEvent event) {
         if (event.getInventory().equals(AnvilGUI.this.inventory)) {
            int rawSlot = event.getRawSlot();
            if (rawSlot != -999) {
               Player clicker = (Player)event.getWhoClicked();
               Inventory clickedInventory = event.getClickedInventory();
               if (clickedInventory != null) {
                  if (clickedInventory.equals(clicker.getInventory())) {
                     if (event.getClick().equals(ClickType.DOUBLE_CLICK)) {
                        event.setCancelled(true);
                        return;
                     }

                     if (event.isShiftClick()) {
                        event.setCancelled(true);
                        return;
                     }
                  }

                  if (event.getCursor() != null && event.getCursor().getType() != Material.AIR && !AnvilGUI.this.interactableSlots.contains(rawSlot) && event.getClickedInventory().equals(AnvilGUI.this.inventory)) {
                     event.setCancelled(true);
                     return;
                  }
               }

               if (rawSlot < 3 && rawSlot >= 0 || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                  event.setCancelled(!AnvilGUI.this.interactableSlots.contains(rawSlot));
                  if (this.clickHandlerRunning && !AnvilGUI.this.concurrentClickHandlerExecution) {
                     return;
                  }

                  CompletableFuture<List<AnvilGUI.ResponseAction>> actionsFuture = (CompletableFuture)AnvilGUI.this.clickHandler.apply(rawSlot, AnvilGUI.StateSnapshot.fromAnvilGUI(AnvilGUI.this));
                  Consumer<List<AnvilGUI.ResponseAction>> actionsConsumer = (actions) -> {
                     Iterator var3 = actions.iterator();

                     while(var3.hasNext()) {
                        AnvilGUI.ResponseAction action = (AnvilGUI.ResponseAction)var3.next();
                        action.accept(AnvilGUI.this, clicker);
                     }

                  };
                  if (actionsFuture.isDone()) {
                     actionsFuture.thenAccept(actionsConsumer).join();
                  } else {
                     this.clickHandlerRunning = true;
                     actionsFuture.thenAcceptAsync(actionsConsumer, AnvilGUI.this.mainThreadExecutor).handle((results, exception) -> {
                        if (exception != null) {
                           AnvilGUI.this.plugin.getLogger().log(Level.SEVERE, "An exception occurred in the AnvilGUI clickHandler", exception);
                        }

                        this.clickHandlerRunning = false;
                        return null;
                     });
                  }
               }

            }
         }
      }

      @EventHandler
      public void onInventoryDrag(InventoryDragEvent event) {
         if (event.getInventory().equals(AnvilGUI.this.inventory)) {
            int[] var2 = AnvilGUI.Slot.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               int slot = var2[var4];
               if (event.getRawSlots().contains(slot)) {
                  event.setCancelled(!AnvilGUI.this.interactableSlots.contains(slot));
                  break;
               }
            }
         }

      }

      @EventHandler
      public void onInventoryClose(InventoryCloseEvent event) {
         if (AnvilGUI.this.open && event.getInventory().equals(AnvilGUI.this.inventory)) {
            AnvilGUI.this.closeInventory(false);
            if (AnvilGUI.this.preventClose) {
               AnvilGUI.this.mainThreadExecutor.execute(() -> {
                  AnvilGUI.this.openInventory();
               });
            }
         }

      }

      // $FF: synthetic method
      ListenUp(Object x1) {
         this();
      }
   }

   public static final class StateSnapshot {
      private final ItemStack leftItem;
      private final ItemStack rightItem;
      private final ItemStack outputItem;
      private final Player player;

      private static AnvilGUI.StateSnapshot fromAnvilGUI(AnvilGUI anvilGUI) {
         Inventory inventory = anvilGUI.getInventory();
         return new AnvilGUI.StateSnapshot(AnvilGUI.itemNotNull(inventory.getItem(0)).clone(), AnvilGUI.itemNotNull(inventory.getItem(1)).clone(), AnvilGUI.itemNotNull(inventory.getItem(2)).clone(), anvilGUI.player);
      }

      public StateSnapshot(ItemStack leftItem, ItemStack rightItem, ItemStack outputItem, Player player) {
         this.leftItem = leftItem;
         this.rightItem = rightItem;
         this.outputItem = outputItem;
         this.player = player;
      }

      public ItemStack getLeftItem() {
         return this.leftItem;
      }

      public ItemStack getRightItem() {
         return this.rightItem;
      }

      public ItemStack getOutputItem() {
         return this.outputItem;
      }

      public Player getPlayer() {
         return this.player;
      }

      public String getText() {
         return this.outputItem.hasItemMeta() ? this.outputItem.getItemMeta().getDisplayName() : "";
      }
   }

   public static class Slot {
      private static final int[] values = new int[]{0, 1, 2};
      public static final int INPUT_LEFT = 0;
      public static final int INPUT_RIGHT = 1;
      public static final int OUTPUT = 2;

      public static int[] values() {
         return values;
      }
   }

   /** @deprecated */
   @Deprecated
   public static class Response {
      /** @deprecated */
      public static List<AnvilGUI.ResponseAction> close() {
         return Arrays.asList(AnvilGUI.ResponseAction.close());
      }

      /** @deprecated */
      public static List<AnvilGUI.ResponseAction> text(String text) {
         return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(text));
      }

      /** @deprecated */
      public static List<AnvilGUI.ResponseAction> openInventory(Inventory inventory) {
         return Arrays.asList(AnvilGUI.ResponseAction.openInventory(inventory));
      }
   }

   @FunctionalInterface
   public interface ResponseAction extends BiConsumer<AnvilGUI, Player> {
      static AnvilGUI.ResponseAction replaceInputText(String text) {
         Validate.notNull(text, "text cannot be null");
         return (anvilgui, player) -> {
            ItemStack item = anvilgui.getInventory().getItem(2);
            if (item == null) {
               item = anvilgui.getInventory().getItem(0);
            }

            if (item == null) {
               throw new IllegalStateException("replaceInputText can only be used if slots OUTPUT or INPUT_LEFT are not empty");
            } else {
               ItemStack cloned = item.clone();
               ItemMeta meta = cloned.getItemMeta();
               meta.setDisplayName(text);
               cloned.setItemMeta(meta);
               anvilgui.getInventory().setItem(0, cloned);
            }
         };
      }

      static AnvilGUI.ResponseAction updateTitle(String literalTitle, boolean preserveRenameText) {
         Validate.notNull(literalTitle, "literalTitle cannot be null");
         return (anvilGUI, player) -> {
            anvilGUI.setTitle(literalTitle, preserveRenameText);
         };
      }

      static AnvilGUI.ResponseAction updateJsonTitle(String json, boolean preserveRenameText) {
         Validate.notNull(json, "json cannot be null");
         return (anvilGUI, player) -> {
            anvilGUI.setJsonTitle(json, preserveRenameText);
         };
      }

      static AnvilGUI.ResponseAction openInventory(Inventory otherInventory) {
         Validate.notNull(otherInventory, "otherInventory cannot be null");
         return (anvilgui, player) -> {
            player.openInventory(otherInventory);
         };
      }

      static AnvilGUI.ResponseAction close() {
         return (anvilgui, player) -> {
            anvilgui.closeInventory();
         };
      }

      static AnvilGUI.ResponseAction run(Runnable runnable) {
         Validate.notNull(runnable, "runnable cannot be null");
         return (anvilgui, player) -> {
            runnable.run();
         };
      }
   }

   public static class Builder {
      private Executor mainThreadExecutor;
      private Consumer<AnvilGUI.StateSnapshot> closeListener;
      private boolean concurrentClickHandlerExecution = false;
      private AnvilGUI.ClickHandler clickHandler;
      private boolean preventClose = false;
      private boolean geyserCompatibility = true;
      private Set<Integer> interactableSlots = Collections.emptySet();
      private Plugin plugin;
      private Object titleComponent;
      private String itemText;
      private ItemStack itemLeft;
      private ItemStack itemRight;
      private ItemStack itemOutput;

      public Builder() {
         this.titleComponent = AnvilGUI.WRAPPER.literalChatComponent("Repair & Name");
      }

      public AnvilGUI.Builder mainThreadExecutor(Executor executor) {
         Validate.notNull(executor, "Executor cannot be null");
         this.mainThreadExecutor = executor;
         return this;
      }

      public AnvilGUI.Builder preventClose() {
         this.preventClose = true;
         return this;
      }

      public AnvilGUI.Builder disableGeyserCompat() {
         this.geyserCompatibility = false;
         return this;
      }

      public AnvilGUI.Builder interactableSlots(int... slots) {
         Set<Integer> newValue = new HashSet();
         int[] var3 = slots;
         int var4 = slots.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int slot = var3[var5];
            newValue.add(slot);
         }

         this.interactableSlots = newValue;
         return this;
      }

      public AnvilGUI.Builder onClose(Consumer<AnvilGUI.StateSnapshot> closeListener) {
         Validate.notNull(closeListener, "closeListener cannot be null");
         this.closeListener = closeListener;
         return this;
      }

      public AnvilGUI.Builder onClickAsync(AnvilGUI.ClickHandler clickHandler) {
         Validate.notNull(clickHandler, "click function cannot be null");
         this.clickHandler = clickHandler;
         return this;
      }

      public AnvilGUI.Builder allowConcurrentClickHandlerExecution() {
         this.concurrentClickHandlerExecution = true;
         return this;
      }

      public AnvilGUI.Builder onClick(BiFunction<Integer, AnvilGUI.StateSnapshot, List<AnvilGUI.ResponseAction>> clickHandler) {
         Validate.notNull(clickHandler, "click function cannot be null");
         this.clickHandler = (slot, stateSnapshot) -> {
            return CompletableFuture.completedFuture((List)clickHandler.apply(slot, stateSnapshot));
         };
         return this;
      }

      public AnvilGUI.Builder plugin(Plugin plugin) {
         Validate.notNull(plugin, "Plugin cannot be null");
         this.plugin = plugin;
         return this;
      }

      public AnvilGUI.Builder text(String text) {
         Validate.notNull(text, "Text cannot be null");
         this.itemText = text;
         return this;
      }

      public AnvilGUI.Builder title(String title) {
         Validate.notNull(title, "title cannot be null");
         this.titleComponent = AnvilGUI.WRAPPER.literalChatComponent(title);
         return this;
      }

      public AnvilGUI.Builder jsonTitle(String json) {
         Validate.notNull(json, "json cannot be null");
         this.titleComponent = AnvilGUI.WRAPPER.jsonChatComponent(json);
         return this;
      }

      public AnvilGUI.Builder itemLeft(ItemStack item) {
         Validate.notNull(item, "item cannot be null");
         this.itemLeft = item;
         return this;
      }

      public AnvilGUI.Builder itemRight(ItemStack item) {
         this.itemRight = item;
         return this;
      }

      public AnvilGUI.Builder itemOutput(ItemStack item) {
         this.itemOutput = item;
         return this;
      }

      public AnvilGUI open(Player player) {
         Validate.notNull(this.plugin, "Plugin cannot be null");
         Validate.notNull(this.clickHandler, "click handler cannot be null");
         Validate.notNull(player, "Player cannot be null");
         if (this.itemText != null) {
            if (this.itemLeft == null) {
               this.itemLeft = new ItemStack(Material.PAPER);
            }

            ItemMeta paperMeta = this.itemLeft.getItemMeta();
            paperMeta.setDisplayName(this.itemText);
            this.itemLeft.setItemMeta(paperMeta);
         }

         if (this.mainThreadExecutor == null) {
            this.mainThreadExecutor = (task) -> {
               Bukkit.getScheduler().runTask(this.plugin, task);
            };
         }

         AnvilGUI anvilGUI = new AnvilGUI(this.plugin, player, this.mainThreadExecutor, this.titleComponent, new ItemStack[]{this.itemLeft, this.itemRight, this.itemOutput}, this.preventClose, this.geyserCompatibility, this.interactableSlots, this.closeListener, this.concurrentClickHandlerExecution, this.clickHandler);
         anvilGUI.openInventory();
         return anvilGUI;
      }
   }
}
