package net.thesieutoc.api;

import net.thesieutoc.utils.FNum;
import org.bukkit.entity.Player;

public class Card {
   private String player;
   private String internalID = "";
   private String transID = "0";
   private String cardType = "";
   private int cardPrice;
   private String SERIAL = "";
   private String PIN = "";
   private int retry = 0;
   private String callbackMessage = "";

   public Card() {
      this.internalID = "" + FNum.randomInt(100000000, 999999999);
   }

   public Card(Player p, String cardType, int cardPrice, String SERIAL, String PIN) {
      this.internalID = "" + FNum.randomInt(100000000, 999999999);
      this.player = p.getName();
      this.cardType = cardType;
      this.cardPrice = cardPrice;
      this.SERIAL = SERIAL;
      this.PIN = PIN;
   }

   public String internalID() {
      return this.internalID;
   }

   public String player() {
      return this.player;
   }

   public String transID() {
      return this.transID;
   }

   public String cardType() {
      return this.cardType;
   }

   public int cardPrice() {
      return this.cardPrice;
   }

   public String SERIAL() {
      return this.SERIAL;
   }

   public String PIN() {
      return this.PIN;
   }

   public int retry() {
      return this.retry;
   }

   public String callbackMessage() {
      return this.callbackMessage;
   }

   public Card player(String player) {
      this.player = player;
      return this;
   }

   public Card transID(String transID) {
      this.transID = transID;
      return this;
   }

   public Card cardType(String cardType) {
      this.cardType = cardType;
      return this;
   }

   public Card cardPrice(int cardPrice) {
      this.cardPrice = cardPrice;
      return this;
   }

   public Card SERIAL(String SERIAL) {
      this.SERIAL = SERIAL;
      return this;
   }

   public Card PIN(String PIN) {
      this.PIN = PIN;
      return this;
   }

   public Card retry(int retry) {
      this.retry = retry;
      return this;
   }

   public Card callbackMessage(String callbackMessage) {
      this.callbackMessage = callbackMessage;
      return this;
   }
}
