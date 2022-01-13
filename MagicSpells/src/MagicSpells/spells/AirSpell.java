package MagicSpells.spells;

import MagicSpells.manager.Manager;
import MagicSpells.manager.SpellManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AirSpell implements Listener {
    private Manager manager;
    private SpellManager spells;

    private Map<UUID, Integer> spellCancelTask = new HashMap<>();

    public AirSpell(Manager manager) {
        this.manager = manager;
        this.spells = manager.getSpellManager();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {

            Player p = (Player) e.getDamager();
            UUID uuid = p.getUniqueId();

            if(!spells.isSpellActivated.containsKey(uuid) || !spells.whichSpellActivated.containsKey(uuid)) return;

            if(spells.isSpellActivated.get(uuid) && spells.whichSpellActivated.get(uuid).equals("AIR")) { // если у ударяющего активно заклинание ветра
                Entity victim = e.getEntity();
                int spellStrength = manager.getConfig().spellStrength.get("AIR");

                victim.setVelocity(p.getLocation().getDirection().multiply(spellStrength)); // отбрасываем жертву

                spells.deactivateSpell(p);

                int task = spellCancelTask.get(uuid);
                Bukkit.getScheduler().cancelTask(task);
                spellCancelTask.remove(uuid);
            }
        }
    }

    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta()) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if (item.getType().equals(Material.BOOK) && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Заклинание ветра")) { // если игрок активирует заклинание ветра

                UUID uuid = p.getUniqueId();
                int waitingTime = manager.getConfig().spellWaitingTime.get("AIR");
                int cooldownTime = manager.getConfig().spellCooldown.get("AIR");

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

                spells.activateSpell(p, "AIR", Sound.ENTITY_WITCH_AMBIENT);
                int task = Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), () -> { // по истечению WaitingTime отправляем заклинание на кулдаун
                    spells.deactivateSpell(p);
                    spellCancelTask.remove(uuid);
                }, waitingTime*20L);

                spellCancelTask.put(uuid, task);
            }
        }
    }
}
