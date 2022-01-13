package MagicSpells.inventories;

import MagicSpells.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;

public class ChooseClassInventory {

    private Manager manager;
    private ItemStack classItem;
    private ItemMeta classMeta;
    private ArrayList<String> classLore = new ArrayList<>();

    public ChooseClassInventory(Manager manager) {
        this.manager = manager;
    }

    public void createClassInvItems(Player p, Inventory inv) {
        ItemStack itemBers = createBerserkItem(); // тут все методы создают предметы в инвентарь выбора классов
        ItemStack itemTank = createTankItem();
        ItemStack itemArch = createArcherItem();
        ItemStack itemHeal = createHealerItem();
        ItemStack itemMage = createMageItem();
        ItemStack itemDarkMage = createDarkMageItem();

        inv.setItem(0, itemBers);
        inv.setItem(1, itemTank);
        inv.setItem(2, itemArch);
        inv.setItem(3, itemHeal);
        inv.setItem(4, itemMage);
        inv.setItem(5, itemDarkMage);
        Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), () -> p.openInventory(inv), 5L); // задержка в открытии инвентаря чтобы избежать ероров в консоли
    }

    private ItemStack createBerserkItem() {
        classItem = new ItemStack(Material.IRON_AXE);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.AQUA + "Berserk");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "Даёт доступ к специальной способности - " + ChatColor.RED + "Рык Дракона.");
        classLore.add(ChatColor.WHITE + "При использовании увеличивает ваш урон, скорость и защиту на " + manager.getConfig().classPowerDuration.get("BERSERK") + " секунд.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("BERSERK") + " секунд.");
        classLore.add(null);
        classLore.add(ChatColor.WHITE + "Пассивное умение:");
        classLore.add(ChatColor.WHITE + "Урон от оружий ближнего боя увеличен на " + manager.getConfig().classPassivePowerStrength.get("BERSERK") + "%");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);
        return classItem;
    }

    private ItemStack createTankItem() {
        classItem = new ItemStack(Material.DIAMOND_CHESTPLATE);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.AQUA + "Tank");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "Даёт доступ к специальной способности - " + ChatColor.AQUA + "Несокрушимость.");
        classLore.add(ChatColor.WHITE + "При использовании блокирует " + manager.getConfig().classPowerStrength.get("TANK") + "% получаемого урона на " + manager.getConfig().classPowerDuration.get("TANK") + " секунд.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("TANK") + " секунд.");
        classLore.add(null);
        classLore.add(ChatColor.WHITE + "Пассивное умение:");
        classLore.add(ChatColor.WHITE + "Ваше HP всегда увеличено на " + manager.getConfig().classPassivePowerStrength.get("TANK") + "% от максимального");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);
        return classItem;
    }

    private ItemStack createArcherItem() {
        classItem = new ItemStack(Material.BOW);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.AQUA + "Archer");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "Даёт доступ к специальной способности - " + ChatColor.AQUA + "Глаз-Алмаз.");
        classLore.add(ChatColor.WHITE + "При использовании увеличивает урон из лука на " + manager.getConfig().classPowerStrength.get("ARCHER") + "%.");
        classLore.add(ChatColor.WHITE + "Также временно увеличивает вашу скорость и высоту прыжка.");
        classLore.add(ChatColor.WHITE + "К тому же на время действия выстрел из лука не будет тратить стрелы, выпуская сразу по 2 стрелы.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("ARCHER") + " секунд.");
        classLore.add(null);
        classLore.add(ChatColor.WHITE + "Пассивное умение:");
        classLore.add(ChatColor.WHITE + "При попадании из лука по игроку вы восстанавливаете себе " + manager.getConfig().classPassivePowerStrength.get("ARCHER") + "% HP от нанесённого урона");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);
        return classItem;
    }

    private ItemStack createHealerItem() {
        classItem = new ItemStack(Material.GOLDEN_APPLE);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.AQUA + "Healer");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "Даёт доступ к специальной способности - " + ChatColor.GREEN + "Благословение природы.");
        classLore.add(ChatColor.WHITE + "При использовании мгновенно восстанавливает " + manager.getConfig().classPowerStrength.get("HEALER") + "% от макисмального количества HP.");
        classLore.add(ChatColor.WHITE + "Также накладывает эффект регенерации на " + manager.getConfig().classPowerDuration.get("HEALER") + " секунд.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("HEALER") + " секунд.");
        classLore.add(null);
        classLore.add(ChatColor.WHITE + "Пассивное умение:");
        classLore.add(ChatColor.WHITE + "Раз в %d секунд вы восстанавливаете ближайшим союзникам %d HP" + manager.getConfig().classPassivePowerStrength.get("HEALER")); // ????????????????
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);
        return classItem;
    }

    private ItemStack createMageItem() {
        classItem = new ItemStack(Material.BLAZE_POWDER);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.AQUA + "Mage");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "Даёт доступ к специальной способности - " + ChatColor.YELLOW + "Огнерождённый.");
        classLore.add(ChatColor.WHITE + "При использовании каждый ваш удар по существу накладывает на него эффект горения на " + manager.getConfig().classPowerStrength.get("MAGE") + " секунд.");
        classLore.add(ChatColor.WHITE + "Также на время действия полностью убирает урон от огня и дает возможность свободного полёта.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("MAGE") + " секунд.");
        classLore.add(null);
        classLore.add(ChatColor.WHITE + "Пассивное умение:");
        classLore.add(ChatColor.WHITE + "Вы получаете на " + manager.getConfig().classPassivePowerStrength.get("MAGE") + "% меньше урона от горения");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);
        return classItem;
    }

    private ItemStack createDarkMageItem() {
        classItem = new ItemStack(Material.NETHER_STAR);
        classMeta = classItem.getItemMeta();
        classMeta.setDisplayName(ChatColor.AQUA + "Dark Mage");

        classLore.clear();
        classLore.add(ChatColor.WHITE + "Даёт доступ к специальной способности - " + ChatColor.DARK_GRAY + "Тёмное Проклятие.");
        classLore.add(ChatColor.WHITE + "При использовании каждый ваш удар по игроку накладывает на него негативные эффекты на " + manager.getConfig().classPowerStrength.get("DARKMAGE") + " секунд.");
        classLore.add(ChatColor.WHITE + "Также на время действия увеличивает урон по всем существам кроме игроков на " + manager.getConfig().classPowerSubStrength.get("DARKMAGE") + "%.");
        classLore.add(ChatColor.WHITE + "Перезарядка способности - " + manager.getConfig().classPowerCooldown.get("DARKMAGE") + " секунд.");
        classLore.add(null);
        classLore.add(ChatColor.WHITE + "Пассивное умение:");
        classLore.add(ChatColor.WHITE + "При получении удара от игрока вы мгновенно возвращаете противнику " + manager.getConfig().classPassivePowerStrength.get("DARKMAGE") + "% от полученного урона");
        classMeta.setLore(classLore);
        classMeta.addItemFlags(HIDE_ATTRIBUTES);
        classItem.setItemMeta(classMeta);
        return classItem;
    }

}
