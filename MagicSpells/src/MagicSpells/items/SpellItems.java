package MagicSpells.items;

import MagicSpells.manager.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SpellItems {

    private Manager manager;

    private ItemStack spellItem;
    private ItemMeta spellMeta;
    private ArrayList<String> spellLore = new ArrayList<>();

    public SpellItems(Manager manager) {
        this.manager = manager;
    }

    public ItemStack createFireSpellItem() {
        spellItem = new ItemStack(Material.BOOK);
        spellItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        spellMeta = spellItem.getItemMeta();
        spellMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spellMeta.setDisplayName(ChatColor.GOLD + "Заклинание огня");

        spellLore.clear();
        spellLore = new ArrayList<>();
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "При использовании ваш следующий удар подожжёт противника на " + manager.getConfig().spellStrength.get("FIRE") + " секунд");
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "Стоимость: " + ChatColor.GOLD + "800$");
        spellMeta.setLore(spellLore);
        spellItem.setItemMeta(spellMeta);

        return spellItem;
    }

    public ItemStack createAirSpellItem() {
        spellItem = new ItemStack(Material.BOOK);
        spellItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        spellMeta = spellItem.getItemMeta();
        spellMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spellMeta.setDisplayName(ChatColor.GOLD + "Заклинание ветра");

        spellLore.clear();
        spellLore = new ArrayList<>();
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "При использовании ваш следующий удар отбросит противника на небольшое расстояние");
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "Стоимость: " + ChatColor.GOLD + "1500$");
        spellMeta.setLore(spellLore);
        spellItem.setItemMeta(spellMeta);

        return spellItem;
    }

    public ItemStack createTpSpellItem() {
        spellItem = new ItemStack(Material.BOOK);
        spellItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        spellMeta = spellItem.getItemMeta();
        spellMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spellMeta.setDisplayName(ChatColor.GOLD + "Заклинание телепортации");

        spellLore.clear();
        spellLore = new ArrayList<>();
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "При использовании телепортарует вас в место, на которое вы смотрите");
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "Стоимость: " + ChatColor.GOLD + "3000$");
        spellMeta.setLore(spellLore);
        spellItem.setItemMeta(spellMeta);

        return spellItem;
    }

    public ItemStack createEarthSpellItem() {
        spellItem = new ItemStack(Material.BOOK);
        spellItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        spellMeta = spellItem.getItemMeta();
        spellMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spellMeta.setDisplayName(ChatColor.GOLD + "Заклинание земли");

        spellLore.clear();
        spellLore = new ArrayList<>();
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "При использовании все существа в радиусе 5 блоков получат замедление и небольшой урон");
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "Стоимость: " + ChatColor.GOLD + "1500$");
        spellMeta.setLore(spellLore);
        spellItem.setItemMeta(spellMeta);

        return spellItem;
    }

    public ItemStack createLightningSpellItem() {
        spellItem = new ItemStack(Material.BOOK);
        spellItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        spellMeta = spellItem.getItemMeta();
        spellMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spellMeta.setDisplayName(ChatColor.GOLD + "Заклинание молнии");

        spellLore.clear();
        spellLore = new ArrayList<>();
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "При использовании создаёт молнию в месте, на которое вы смотрите");
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "Стоимость: " + ChatColor.GOLD + "2500$");
        spellMeta.setLore(spellLore);
        spellItem.setItemMeta(spellMeta);

        return spellItem;
    }

    public ItemStack createPoisonSpellItem() {
        spellItem = new ItemStack(Material.BOOK);
        spellItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        spellMeta = spellItem.getItemMeta();
        spellMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spellMeta.setDisplayName(ChatColor.GOLD + "Заклинание отравления");

        spellLore.clear();
        spellLore = new ArrayList<>();
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "При использовании следующий ваш удар по противнику будет наносить ему урон, равный " + manager.getConfig().spellStrength.get("POISON")+ "% от ваших HP на протяжении " + manager.getConfig().spellSubStrength.get("POISON") + " секунд");
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "Стоимость: " + ChatColor.GOLD + "2500$");
        spellMeta.setLore(spellLore);
        spellItem.setItemMeta(spellMeta);

        return spellItem;
    }

    public ItemStack createHealSpellItem() {
        spellItem = new ItemStack(Material.BOOK);
        spellItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        spellMeta = spellItem.getItemMeta();
        spellMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spellMeta.setDisplayName(ChatColor.GOLD + "Заклинание исцеления");

        spellLore.clear();
        spellLore = new ArrayList<>();
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "При использовании востснавливаем вам" + manager.getConfig().spellStrength.get("HEAL") +"от максимального количества HP");
        spellLore.add(null);
        spellLore.add(ChatColor.WHITE + "Стоимость: " + ChatColor.GOLD + "5000$");
        spellMeta.setLore(spellLore);
        spellItem.setItemMeta(spellMeta);

        return spellItem;
    }


}
