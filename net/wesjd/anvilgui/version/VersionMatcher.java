package net.wesjd.anvilgui.version;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

public class VersionMatcher {
   private static final Map<String, String> VERSION_TO_REVISION = new HashMap<String, String>() {
      {
         this.put("1.20", "1_20_R1");
         this.put("1.20.1", "1_20_R1");
         this.put("1.20.2", "1_20_R2");
         this.put("1.20.3", "1_20_R3");
         this.put("1.20.4", "1_20_R3");
         this.put("1.20.5", "1_20_R4");
         this.put("1.20.6", "1_20_R4");
         this.put("1.21", "1_21_R1");
         this.put("1.21.1", "1_21_R1");
         this.put("1.21.2", "1_21_R2");
         this.put("1.21.3", "1_21_R2");
      }
   };
   private static final String FALLBACK_REVISION = "1_21_R2";

   public VersionWrapper match() {
      String craftBukkitPackage = Bukkit.getServer().getClass().getPackage().getName();
      String rVersion;
      if (!craftBukkitPackage.contains(".v")) {
         String version = Bukkit.getBukkitVersion().split("-")[0];
         rVersion = (String)VERSION_TO_REVISION.getOrDefault(version, "1_21_R2");
      } else {
         rVersion = craftBukkitPackage.split("\\.")[3].substring(1);
      }

      try {
         return (VersionWrapper)Class.forName(this.getClass().getPackage().getName() + ".Wrapper" + rVersion).getDeclaredConstructor().newInstance();
      } catch (ClassNotFoundException var4) {
         throw new IllegalStateException("AnvilGUI does not support server version \"" + rVersion + "\"", var4);
      } catch (ReflectiveOperationException var5) {
         throw new IllegalStateException("Failed to instantiate version wrapper for version " + rVersion, var5);
      }
   }
}
