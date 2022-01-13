package MagicSpells.manager;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class SpellManager {
    private Manager manager;

    public HashMap<UUID, Boolean> isSpellActivated = new HashMap<>();
    public HashMap<UUID, Long> spellCooldownLeft = new HashMap<>();
    public HashMap<UUID, String> whichSpellActivated = new HashMap<>();

    SpellManager(Manager manager) {
        this.manager = manager;
    }

    public void deactivateSpell(Player p) {
        UUID uuid = p.getUniqueId();

        isSpellActivated.put(uuid, false);
        whichSpellActivated.put(uuid, null);
        p.sendMessage(ChatColor.YELLOW + "Заклинание деактивировано");
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITCH_DEATH, 1, 1);

        spellCooldownLeft.put(p.getUniqueId(), System.currentTimeMillis());  // ставим КД
    }

    public void activateSpell(Player p, String spellName, Sound sound) {
        UUID uuid = p.getUniqueId();

        isSpellActivated.put(uuid, true);
        if(spellName != null) whichSpellActivated.put(uuid, spellName);
        p.sendMessage(ChatColor.GREEN + "Заклинание активировано");
        p.getWorld().playSound(p.getLocation(), sound, 1, 1);
    }

    public void activateInstantSpell(Player p, Sound sound) {
        UUID uuid = p.getUniqueId();

        isSpellActivated.put(uuid, false);
        whichSpellActivated.put(uuid, null);
        p.getWorld().playSound(p.getLocation(), sound, 1, 1);
    }

    // все методы ниже - уменьшение урона в зависимости от брони
    public void damagePlayerWithArmor(Player p, double damage) {
        double points = p.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
        PotionEffect effect = p.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        int resistance = effect == null ? 0 : effect.getAmplifier();
        int epf = getEPF(p.getInventory());

        p.damage(calculateDamageApplied(damage, points, toughness, resistance, epf));
    }

    private double calculateDamageApplied(double damage, double points, double toughness, int resistance, int epf) {
        double withArmorAndToughness = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
        double withResistance = withArmorAndToughness * (1 - (resistance * 0.2));
        return withResistance * (1 - (Math.min(20.0, epf) / 25));
    }

    private static int getEPF(PlayerInventory inv) {
        ItemStack helm = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack legs = inv.getLeggings();
        ItemStack boot = inv.getBoots();

        return (helm != null ? helm.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (chest != null ? chest.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (legs != null ? legs.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (boot != null ? boot.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0);
    }
}
