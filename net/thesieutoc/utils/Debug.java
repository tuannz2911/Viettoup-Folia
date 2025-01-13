package net.thesieutoc.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.api.Card;

public class Debug {
   public static void write(String msg) {
      msg = msg.trim();
      Date now = new Date();
      SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
      File log = new File(Thesieutoc.getInstance().getDataFolder(), "debug.txt");
      if (!log.exists()) {
         log.getParentFile().mkdirs();
      }

      BufferedWriter writer = null;

      try {
         writer = new BufferedWriter(new FileWriter(log, true));
         writer.append("[").append(df.format(now)).append("] ").append(msg);
         writer.newLine();
         writer.flush();
         writer.close();
      } catch (IOException var6) {
      }

   }

   public static void write(String msg, Card card) {
      msg = msg.trim();
      msg = card.player() + "|" + card.cardType() + "|" + card.cardPrice() + "|" + card.SERIAL() + "|" + card.PIN() + "|" + card.transID() + "|" + card.internalID() + "| " + msg;
      Date now = new Date();
      SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
      File log = new File(Thesieutoc.getInstance().getDataFolder(), "debug.txt");
      if (!log.exists()) {
         log.getParentFile().mkdirs();
      }

      BufferedWriter writer = null;

      try {
         writer = new BufferedWriter(new FileWriter(log, true));
         writer.append("[").append(df.format(now)).append("] ").append(msg);
         writer.newLine();
         writer.flush();
         writer.close();
      } catch (IOException var7) {
      }

   }
}
