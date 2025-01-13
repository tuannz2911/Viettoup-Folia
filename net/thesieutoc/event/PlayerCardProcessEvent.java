package net.thesieutoc.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCardProcessEvent extends Event {
   static final HandlerList handlers = new HandlerList();
   Player p;
   String seri;
   String pin;
   String cardType;
   int cardprice;
   String randomMD5;
   long timestamp;

   public PlayerCardProcessEvent(Player p, String seri, String pin, String cardType, int cardprice, String randomMD5, long timestamp) {
      this.p = p;
      this.seri = seri;
      this.pin = pin;
      this.cardType = cardType;
      this.cardprice = cardprice;
      this.randomMD5 = randomMD5;
      this.timestamp = timestamp;
      Bukkit.getServer().getPluginManager().callEvent(this);
   }

   public static final HandlerList getHandlerList() {
      return handlers;
   }

   public Player getPlayer() {
      return this.p;
   }

   public String getSerial() {
      return this.seri;
   }

   public String getPin() {
      return this.pin;
   }

   public String getCardType() {
      return this.cardType;
   }

   public int getCardPrice() {
      return this.cardprice;
   }

   public String getRandomMD5() {
      return this.randomMD5;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public final HandlerList getHandlers() {
      return handlers;
   }
}
