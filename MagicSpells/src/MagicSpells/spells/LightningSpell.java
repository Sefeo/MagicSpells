package MagicSpells.spells;

import MagicSpells.manager.Manager;
import MagicSpells.manager.SpellManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class LightningSpell implements Listener {

    private Manager manager;
    private SpellManager spells;

    private HashMap<UUID, Player> lightningOwner = new HashMap<>();

    public LightningSpell(Manager manager) {
        this.manager = manager;
        this.spells = manager.getSpellManager();
    }

    @EventHandler
    private void onLightningStrike(LightningStrikeEvent e) { // чистим переменную lightningOwner если не ударило по энтити
        LightningStrike lightning = e.getLightning();


        Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), () -> {
            if(lightningOwner.containsKey(lightning.getUniqueId())) // тупой интелидж, не unnecessary, чистится в onDamage если попало по мобу
                lightningOwner.remove(lightning.getUniqueId());
        }, 10);
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof LightningStrike) {
            Entity victim = e.getEntity();

            if(!lightningOwner.containsKey(e.getDamager().getUniqueId())) return;

            double damage = manager.getConfig().spellStrength.get("LIGHTNING");

            if(victim instanceof Player) spells.damagePlayerWithArmor((Player) victim, damage);
            else e.setDamage(damage);

            lightningOwner.remove(e.getDamager().getUniqueId());
        }
    }

    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta()) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if (item.getType().equals(Material.BOOK) && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Заклинание молнии")) { // если игрок активирует заклинание молнии

                UUID uuid = p.getUniqueId();
                int cooldownTime = manager.getConfig().spellCooldown.get("LIGHTNING");

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

                strikeLightning(p);
            }
        }
    }

    private void strikeLightning(Player p) {
        Location target = p.getLocation().add(p.getLocation().getDirection().multiply(5));

        for (Block b : p.getLineOfSight(null, 50)) {

            if (!b.getType().equals(Material.AIR)) { target = b.getLocation(); break; } // получаем блок, на который игрок смотрит

            for (Entity g : b.getChunk().getEntities()) // энтити на пути считаем за точку  телепорта
                if (g.getLocation().toVector().distance(b.getLocation().toVector()) < 1) { target = g.getLocation(); break; }
        }

        Entity ent = p.getWorld().strikeLightning(target); // создаем молнию
        lightningOwner.put(ent.getUniqueId(), p); // и записываем её владельца

        p.getWorld().playSound(target, Sound.ENTITY_LIGHTNING_THUNDER, 1, 1);
    }
}
