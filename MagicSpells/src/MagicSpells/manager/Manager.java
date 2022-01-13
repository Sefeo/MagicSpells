package MagicSpells.manager;

import MagicSpells.Main;
import MagicSpells.classes.*;
import MagicSpells.data.CheckData;
import MagicSpells.data.Config;
import MagicSpells.data.UpdateData;
import MagicSpells.inventories.ChooseClassInventory;
import MagicSpells.items.SpellItems;
import MagicSpells.listener.Handler;
import MagicSpells.items.ClassPowerItems;
import org.bukkit.plugin.Plugin;

public class Manager {

    private Main plugin;
    private Handler handler;
    private UpdateData updateData;
    private CheckData checkData;
    private ChooseClassInventory chooseClass;
    private Config config;
    private ClassPowerItems classItems;
    private SpellItems spellItems;
    private SpellManager spellManager;

    private Berserk bersClass;
    private Tank tankClass;
    private Archer archClass;
    private Healer healClass;
    private Mage mageClass;
    private DarkMage darkMageClass;

    public Manager(Main main) {
        this.plugin = main;
        this.handler = new Handler(this);
        this.updateData = new UpdateData(this);
        this.checkData = new CheckData(this);
        this.chooseClass = new ChooseClassInventory(this);
        this.config = new Config(this);
        this.classItems = new ClassPowerItems(this);
        this.spellItems = new SpellItems(this);
        this.spellManager = new SpellManager(this);

        this.bersClass = new Berserk(this);
        this.tankClass = new Tank(this);
        this.archClass = new Archer(this);
        this.healClass = new Healer(this);
        this.mageClass = new Mage(this);
        this.darkMageClass = new DarkMage(this);
    }

    public Handler getHandler() {
        return handler;
    }

    public UpdateData getUpdateData() {
        return updateData;
    }

    public CheckData getCheckData(){
        return checkData;
    }

    public Config getConfig() {
        return config;
    }

    public ChooseClassInventory getClassInventory() {
        return chooseClass;
    }

    public ClassPowerItems getClassItems() {
        return classItems;
    }

    public SpellItems getSpellItems() {
        return spellItems;
    }

    public SpellManager getSpellManager() { return spellManager; }


    public Berserk getBersClass() {
        return bersClass;
    }

    public Tank getTankClass() {
        return tankClass;
    }

    public Archer getArchClass() {
        return archClass;
    }

    public Healer getHealClass() {
        return healClass;
    }

    public Mage getMageClass() {
        return mageClass;
    }

    public DarkMage getDarkMageClass() {
        return darkMageClass;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
