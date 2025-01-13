package net.thesieutoc.api;

public enum CardPrice {
   _10K(10000, 1),
   _20K(20000, 2),
   _30K(30000, 3),
   _50K(50000, 4),
   _100K(100000, 5),
   _200K(200000, 6),
   _300K(300000, 7),
   _500K(500000, 8),
   _1M(1000000, 9),
   UNKNOWN(0, -1);

   private final int price;
   private final int id;

   private CardPrice(int param3, int param4) {
      this.price = price;
      this.id = id;
   }

   public static CardPrice getPrice(int price) {
      CardPrice[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CardPrice a = var1[var3];
         if (a.price == price) {
            return a;
         }
      }

      return UNKNOWN;
   }

   public int getPrice() {
      return this.price;
   }

   public int getId() {
      return this.id;
   }

   // $FF: synthetic method
   private static CardPrice[] $values() {
      return new CardPrice[]{_10K, _20K, _30K, _50K, _100K, _200K, _300K, _500K, _1M, UNKNOWN};
   }
}
