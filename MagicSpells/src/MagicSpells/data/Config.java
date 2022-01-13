package MagicSpells.data;

import MagicSpells.enums.ClassesNames;
import MagicSpells.enums.SpellsNames;
import MagicSpells.manager.Manager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class Config {

    private Manager manager;
    private FileConfiguration config;

    public String dbUrl;
    public String dbUser;
    public String dbPassword;
    public String dbTable;
    public String dbMainTable;

    public HashMap<String, Integer> classPowerStrength = new HashMap<>(); // мощность основной способности
    public HashMap<String, Integer> classPowerSubStrength = new HashMap<>(); // вторая мощность основной способности
    public HashMap<String, Integer> classPowerCooldown = new HashMap<>(); // перезарядка основной способности
    public HashMap<String, Integer> classPowerDuration = new HashMap<>(); // длительность основной способности
    public HashMap<String, Integer> classPassivePowerStrength = new HashMap<>(); // мощность пассивной способности
    public HashMap<String, Integer> classPassivePowerSubStrength = new HashMap<>(); // вторая мощность пассивной способности

    public HashMap<String, Integer> spellStrength = new HashMap<>(); // основная сила заклинания
    public HashMap<String, Integer> spellSubStrength = new HashMap<>(); // вторая сила заклинания
    public HashMap<String, Integer> spellCooldown = new HashMap<>(); // перезарядка заклинаний
    public HashMap<String, Integer> spellWaitingTime = new HashMap<>(); // время бездействия, которое заклинания ждут до ухода на КД

    public boolean isClassChoosingEnabled = false;

    public Config(Manager manager) {
        this.manager = manager;
        this.config = manager.getPlugin().getConfig();
    }

    public void loadConfig() {
        dbUrl = config.getString("Database.Url"); // загружаем инфу о БД из конфига
        dbUser = config.getString("Database.User");
        dbPassword = config.getString("Database.Password");
        dbTable = config.getString("Database.Table");
        dbMainTable = config.getString("Database.MainTable");

        isClassChoosingEnabled = config.getBoolean("IsClassChoosingEnabled");

        for(SpellsNames spellsNames : SpellsNames.values()) { // загружаем инфу о заклинаниях
            String spellName = spellsNames.toString();

            spellCooldown.put(spellName, config.getInt("Spells." + spellName + ".Cooldown"));
            spellStrength.put(spellName, config.getInt("Spells." + spellName + ".Strength"));

            if(config.get("Spells." + spellName + ".WaitingTime") != null)
                spellWaitingTime.put(spellName, config.getInt("Spells." + spellName + ".WaitingTime"));

            if(config.get("Spells." + spellName + ".SubStrength") != null)
                spellSubStrength.put(spellName, config.getInt("Spells." + spellName + ".SubStrength"));
        }

        for(ClassesNames classesNames : ClassesNames.values()) { // загружаем инфу о всех классах из конфига
            String className = classesNames.toString();

            classPassivePowerStrength.put(className, config.getInt("Classes." + className + ".PassivePower"));
            classPowerCooldown.put(className, config.getInt("Classes." + className + ".Power.Cooldown"));
            classPowerDuration.put(className, config.getInt("Classes." + className + ".Power.Duration"));
            classPowerStrength.put(className, config.getInt("Classes." + className + ".Power.Strength"));

            if(config.get("Classes." + className + ".Power.SubStrength") != null)
                classPowerSubStrength.put(className, config.getInt("Classes." + className + ".Power.SubStrength"));

            if(config.get("Classes." + className + ".PassivePowerSubStrength") != null)
                classPassivePowerSubStrength.put(className, config.getInt("Classes." + className + ".PassivePowerSubStrength"));
        }

    }
}
