package MagicSpells.spells;

import MagicSpells.manager.Manager;
import MagicSpells.manager.SpellManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class EarthSpell implements Listener {

    private Manager manager;
    private SpellManager spells;

    public EarthSpell(Manager manager) {
        this.manager = manager;
        this.spells = manager.getSpellManager();
    }

    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta()) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if (item.getType().equals(Material.BOOK) && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Заклинание земли")) { // если игрок активирует заклинание земли

                UUID uuid = p.getUniqueId();
                int cooldownTime = manager.getConfig().spellCooldown.get("EARTH");

                if(spells.isSpellActivated.containsKey(uuid) && spells.isSpellActivated.get(uuid)) {
                    p.sendMessage(ChatColor.RED + "Заклинание уже активировано");
                    return;
                }

                if(spells.spellCooldownLeft.containsKey(uuid)) {
                    long secondsLeft = ((spells.spellCooldownLeft.get(p.getUniqueId()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                    if (secondsLeft > 0) {
                        p.sendMessage(ChatColor.YELLOW + "Вы сможете использовать заклинание через " + secondsLeft + " секунд");
                        return;
                    }
                }

                spells.spellCooldownLeft.put(p.getUniqueId(), System.currentTimeMillis()); // ставим КД
                spells.activateInstantSpell(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

                int spellStrength = manager.getConfig().spellStrength.get("EARTH");
                int spellSubStrength = manager.getConfig().spellSubStrength.get("EARTH");

                List<Entity> near = p.getNearbyEntities(spellStrength, spellStrength/2D, spellStrength);

                for(Entity entity : near) { // проходимся по всем ближайшим энтити
                    if(!(entity instanceof LivingEntity)) continue;

                    if(entity instanceof Player) spells.damagePlayerWithArmor((Player) entity, spellSubStrength); // если энтити игрок, то учитываем броню
                    else ((org.bukkit.entity.LivingEntity) entity).damage(spellSubStrength); // если не игрок, просто дамажим

                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                    entity.setVelocity(new Vector(0,0.4,0)); // чутка подкидываем, +- на 1 блок
                }
            }
        }
    }
}
