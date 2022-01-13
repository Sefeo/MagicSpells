package MagicSpells;

import MagicSpells.classes.*;
import MagicSpells.commands.Commands;
import MagicSpells.listener.Handler;
import MagicSpells.listener.SubListeners;
import MagicSpells.manager.Manager;
import MagicSpells.spells.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main extends JavaPlugin {

    private static Main instance;
    private Connection connection;
    private Manager manager;

    public void onEnable() {
        saveDefaultConfig();
        System.out.println("[MagicSpells] MagicSpells loaded! Dev: Sefeo");

        setInstance(this);
        manager = new Manager(this);

        Bukkit.getPluginManager().registerEvents(new Handler(manager), this);
        Bukkit.getPluginManager().registerEvents(new SubListeners(manager), this);
        Bukkit.getPluginManager().registerEvents(new Berserk(manager), this);
        Bukkit.getPluginManager().registerEvents(new Tank(manager), this);
        Bukkit.getPluginManager().registerEvents(new Archer(manager), this);
        Bukkit.getPluginManager().registerEvents(new Healer(manager), this);
        Bukkit.getPluginManager().registerEvents(new Mage(manager), this);
        Bukkit.getPluginManager().registerEvents(new DarkMage(manager), this);

        Bukkit.getPluginManager().registerEvents(new AirSpell(manager), this);
        Bukkit.getPluginManager().registerEvents(new EarthSpell(manager), this);
        Bukkit.getPluginManager().registerEvents(new FireSpell(manager), this);
        Bukkit.getPluginManager().registerEvents(new HealSpell(manager), this);
        Bukkit.getPluginManager().registerEvents(new LightningSpell(manager), this);
        Bukkit.getPluginManager().registerEvents(new PoisonSpell(manager), this);
        Bukkit.getPluginManager().registerEvents(new TpSpell(manager), this);

        manager.getConfig().loadConfig();
        listConnection();

        if(Bukkit.getOnlinePlayers().size() > 0) {
            for(Player p: Bukkit.getOnlinePlayers()) {
                manager.getCheckData().checkPlayerData(p); // обновляем класс всех игроков при /reload

                String playerClass = manager.getCheckData().playerClass.get(p.getUniqueId().toString());

                if(playerClass.equals("HEALER")) manager.getHealClass().regenTeamHealth(p); // врубаем пассивный отхил хилера
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private static void setInstance(Main instance) {
        Main.instance = instance;
    }

    public static Main getInstance() {
        return instance;
    }

    private void listConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectUrl = "jdbc:mysql://" + manager.getConfig().dbUrl + "/" + manager.getConfig().dbMainTable;
            this.connection = DriverManager.getConnection(connectUrl, manager.getConfig().dbUser, manager.getConfig().dbPassword);

            String createTable = "CREATE TABLE IF NOT EXISTS " + manager.getConfig().dbMainTable + "." + manager.getConfig().dbTable + " (" // подключаемся к бд и создаем таблицу
                    + "ID INT(11) NOT NULL primary key AUTO_INCREMENT,"
                    + "UUID VARCHAR(36) NOT NULL,"
                    + "Nick VARCHAR(26) NOT NULL,"
                    + "Class VARCHAR(10) NOT NULL)";
            Statement statement = this.connection.createStatement();
            statement.execute(createTable);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[MagicSpells] Couldn`t connect to the database, disabling plugin...");
            onDisable();
        }
    }
}
