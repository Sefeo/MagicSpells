package MagicSpells.items;

import MagicSpells.manager.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

public class ClassPowerItems {

    private Manager manager;

    private ItemStack classItem;
    private ItemMeta classMeta;
    private ArrayList<String> classLore = new ArrayList<>();

    public ClassPowerItems(Manager manager) {
        this.manager = manager;
    }

    public void createBerserkPowerItem(Player p) { // тут все методы создают и выдают способку класса + записывают в БД класс игрока
        classItem = new ItemStack(Material.BLAZE_ROD);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.RED + "Рык Дракона");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "При использовании увеличивает ваш урон, скорость и защиту на " + manager.getConfig().classPowerDuration.get("BERSERK") + " секунд.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("BERSERK") + " секунд.");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);

        p.getInventory().addItem(classItem);

        manager.getUpdateData().setPlayerClass(p, "BERSERK");
    }

    public void createTankPowerItem(Player p) {
        classItem = new ItemStack(Material.BLAZE_ROD);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.AQUA + "Несокрушимость");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "При использовании блокирует " + manager.getConfig().classPowerStrength.get("TANK") + "% получаемого урона на " + manager.getConfig().classPowerDuration.get("TANK") + " секунд.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("TANK") + " секунд.");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);

        p.getInventory().addItem(classItem);

        double newHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * (manager.getConfig().classPassivePowerStrength.get("TANK")/100F);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + newHealth);

        manager.getUpdateData().setPlayerClass(p, "TANK");
    }

    public void createArcherPowerItem(Player p) {
        classItem = new ItemStack(Material.BLAZE_ROD);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.AQUA + "Глаз-Алмаз");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "При использовании увеличивает урон из лука на " + manager.getConfig().classPowerStrength.get("ARCHER") + "%.");
        classLore.add(ChatColor.WHITE + "Также временно увеличивает вашу скорость и высоту прыжка.");
        classLore.add(ChatColor.WHITE + "К тому же на время действия выстрел из лука не будет тратить стрелы, выпуская сразу по 2 стрелы.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("ARCHER") + " секунд.");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);

        p.getInventory().addItem(classItem);

        manager.getUpdateData().setPlayerClass(p, "ARCHER");
    }

    public void createHealerPowerItem(Player p) {
        classItem = new ItemStack(Material.BLAZE_ROD);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.GREEN + "Благословение Природы");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "При использовании мгновенно восстанавливает " + manager.getConfig().classPowerStrength.get("HEALER") + "% от макисмального количества HP.");
        classLore.add(ChatColor.WHITE + "Также накладывает эффект регенерации на " + manager.getConfig().classPowerDuration.get("HEALER") + " секунд.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("HEALER") + " секунд.");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);

        p.getInventory().addItem(classItem);

        manager.getUpdateData().setPlayerClass(p, "HEALER");
    }

    public void createMagePowerItem(Player p) {
        classItem = new ItemStack(Material.BLAZE_ROD);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.YELLOW + "Огнерождённый");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "При использовании каждый ваш удар по существу накладывает на него эффект горения на " + manager.getConfig().classPowerStrength.get("MAGE") + " секунд.");
        classLore.add(ChatColor.WHITE + "Также на время действия полностью убирает урон от огня и дает возможность свободного полёта.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("MAGE") + " секунд.");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);

        p.getInventory().addItem(classItem);

        manager.getUpdateData().setPlayerClass(p, "MAGE");
    }

    public void createDarkMagePowerItem(Player p) {
        classItem = new ItemStack(Material.BLAZE_ROD);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.DARK_GRAY + "Тёмное Проклятие");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "При использовании каждый ваш удар по игроку накладывает на него негативные эффекты на " + manager.getConfig().classPowerStrength.get("DARKMAGE") + " секунд.");
        classLore.add(ChatColor.WHITE + "Также на время действия увеличивает урон по всем существам кроме игроков на " + manager.getConfig().classPowerSubStrength.get("DARKMAGE") + "%.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("DARKMAGE") + " секунд.");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);

        p.getInventory().addItem(classItem);

        manager.getUpdateData().setPlayerClass(p, "DARKMAGE");
    }
}
