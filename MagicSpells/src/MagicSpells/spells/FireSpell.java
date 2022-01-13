package MagicSpells.spells;

import MagicSpells.manager.Manager;
import MagicSpells.manager.SpellManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FireSpell implements Listener {

    private Manager manager;
    private SpellManager spells;

    private Map<UUID, Integer> spellCancelTask = new HashMap<>();

    public FireSpell(Manager manager) {
        this.manager = manager;
        this.spells = manager.getSpellManager();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) { // если игрок любое энтити
            Player p = (Player) e.getDamager();
            UUID uuid = p.getUniqueId();

            if (!spells.isSpellActivated.containsKey(uuid) || !spells.whichSpellActivated.containsKey(uuid)) return;

            if(spells.isSpellActivated.get(uuid) && spells.whichSpellActivated.get(uuid).equals("FIRE")) {
                int burnTime = manager.getConfig().spellStrength.get("FIRE")*20;
                e.getEntity().setFireTicks(burnTime); // поджигаем жертву удара на значение из конфига

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

            if (item.getType().equals(Material.BOOK) && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Заклинание огня")) { // если игрок активирует заклинание огня

                UUID uuid = p.getUniqueId();
                int waitingTime = manager.getConfig().spellWaitingTime.get("FIRE");
                int cooldownTime = manager.getConfig().spellCooldown.get("FIRE");

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

                spells.activateSpell(p, "FIRE", Sound.ENTITY_WITCH_AMBIENT);
                int task = Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), () -> { // по истечению WaitingTime отправляем заклинание на кулдаун
                    spells.deactivateSpell(p);
                    spellCancelTask.remove(uuid);
                }, waitingTime*20L);

                spellCancelTask.put(uuid, task);
            }
        }
    }
}
