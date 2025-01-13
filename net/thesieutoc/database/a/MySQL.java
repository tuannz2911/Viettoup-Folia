package net.thesieutoc.database.a;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import net.thesieutoc.api.Card;
import net.thesieutoc.config.Config;
import net.thesieutoc.database.DatabaseType;
import net.thesieutoc.utils.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MySQL implements DatabaseType {
   private static HikariConfig config;
   private static HikariDataSource dataSource;
   private static Connection connection = null;

   public void init() {
      if (config == null) {
         try {
            config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + Config.SQLHost() + ":" + Config.SQLPort() + "/" + Config.SQLDatabase());
            config.setUsername(Config.SQLUser());
            config.setPassword(Config.SQLPass());
            config.setMaximumPoolSize(1);
            config.setConnectionTimeout(60000L);
            dataSource = new HikariDataSource(config);
            connection = dataSource.getConnection();
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         try {
            Statement stmt = this.getConnection().createStatement();

            try {
               stmt.executeUpdate("CREATE TABLE if not exists `napthe_log` ( `id` MEDIUMINT(11) NOT NULL AUTO_INCREMENT , `name` VARCHAR(255) NOT NULL , `uuid` VARCHAR(100) NOT NULL , `seri` VARCHAR(255) NOT NULL , `pin` VARCHAR(255) NOT NULL , `loai` VARCHAR(255) NOT NULL , `time` INT(11) NOT NULL , `menhgia` VARCHAR(10) NOT NULL , `note` VARCHAR(255) NOT NULL , `server_port` VARCHAR(15) NOT NULL , PRIMARY KEY (`id`))");

               try {
                  stmt.executeUpdate("ALTER Table `napthe_log` DROP COLUMN IF EXISTS `referral`");
               } catch (Exception var5) {
               }
            } catch (Throwable var7) {
               if (stmt != null) {
                  try {
                     stmt.close();
                  } catch (Throwable var4) {
                     var7.addSuppressed(var4);
                  }
               }

               throw var7;
            }

            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var8) {
            var8.printStackTrace();
         }
      }

   }

   public void disable() {
      try {
         dataSource.close();
         connection.close();
      } catch (Exception var2) {
      }

   }

   public Connection getConnection() {
      if (connection == null) {
         this.init();

         try {
            connection = dataSource.getConnection();
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

      return connection;
   }

   public ResultSet query(String query) {
      this.init();

      try {
         Statement stmt = connection.createStatement();

         ResultSet var3;
         try {
            var3 = stmt.executeQuery(query);
         } catch (Throwable var6) {
            if (stmt != null) {
               try {
                  stmt.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (stmt != null) {
            stmt.close();
         }

         return var3;
      } catch (SQLException var7) {
         var7.printStackTrace();
         return null;
      }
   }

   public int getPlayerTotalCharged(Player p) {
      this.init();
      int total = 0;

      try {
         PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM napthe_log WHERE note = ? AND name = ?");

         try {
            statement.setString(1, "thanh cong");
            statement.setString(2, p.getName());
            ResultSet rs = statement.executeQuery();

            while(true) {
               if (!rs.next()) {
                  rs.close();
                  break;
               }

               total += rs.getInt("menhgia");
            }
         } catch (Throwable var7) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (Exception var8) {
         var8.printStackTrace();
         Bukkit.getLogger().warning("[Thesieutoc] §cXuat hien loi ve MySQL, vui long lien he staff TheSieuToc.");
      }

      return total;
   }

   public void writeLog(Player player, Card card) {
      this.init();

      try {
         PreparedStatement statement = this.getConnection().prepareStatement("INSERT INTO napthe_log(name,uuid,loai,time,menhgia,pin,seri,server_port,note) VALUE (?,?,?,?,?,?,?,?,?)");

         try {
            statement.setString(1, player.getName());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, card.cardType());
            statement.setLong(4, System.currentTimeMillis() / 1000L);
            statement.setInt(5, card.cardPrice());
            statement.setString(6, card.PIN());
            statement.setString(7, card.SERIAL());
            statement.setInt(8, Bukkit.getPort());
            statement.setString(9, card.callbackMessage());
            statement.executeUpdate();
         } catch (Throwable var7) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (Exception var8) {
         var8.printStackTrace();
         Bukkit.getLogger().warning("[Thesieutoc] §cCo loi xay ra khi ghi log nap the, vui long lien he staff TheSieuToc.");
      }

   }

   public List<Transaction> transactions() {
      this.init();
      LinkedList transactions = new LinkedList();

      try {
         PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM napthe_log WHERE note = ?");

         try {
            statement.setString(1, "thanh cong");
            ResultSet rs = statement.executeQuery();

            while(true) {
               if (!rs.next()) {
                  rs.close();
                  break;
               }

               Date date = new Date(rs.getLong("time") * 1000L);
               Transaction transaction = new Transaction(rs.getString("name"), rs.getInt("menhgia"), date);
               transactions.add(transaction);
            }
         } catch (Throwable var7) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      return transactions;
   }

   public List<Transaction> transactions(Date start, Date end) {
      this.init();
      LinkedList transactions = new LinkedList();

      try {
         PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM napthe_log WHERE note = ?");

         try {
            statement.setString(1, "thanh cong");
            ResultSet rs = statement.executeQuery();

            while(true) {
               if (!rs.next()) {
                  rs.close();
                  break;
               }

               Date date = new Date(rs.getLong("time") * 1000L);
               Transaction transaction = new Transaction(rs.getString("name"), rs.getInt("menhgia"), date);
               transactions.add(transaction);
            }
         } catch (Throwable var9) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      return transactions;
   }
}
