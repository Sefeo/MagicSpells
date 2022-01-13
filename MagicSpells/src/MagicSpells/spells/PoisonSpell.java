package MagicSpells.spells;

import MagicSpells.manager.Manager;
import MagicSpells.manager.SpellManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PoisonSpell implements Listener {

    private Manager manager;
    private SpellManager spells;

    private Map<UUID, Integer> spellCancelTask = new HashMap<>();
    private Map<UUID, Integer> counter = new HashMap<>();

    public PoisonSpell(Manager manager) {
        this.manager = manager;
        this.spells = manager.getSpellManager();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) { // если ударяющий - игрок
            Player p = (Player) e.getDamager();
            UUID uuid = p.getUniqueId();

            if(!spells.isSpellActivated.containsKey(uuid) || !spells.whichSpellActivated.containsKey(uuid)) return;

            if(spells.isSpellActivated.get(uuid) && spells.whichSpellActivated.get(uuid).equals("POISON")) { // если у ударяющего активно заклинание яда
                Entity victim = e.getEntity();
                UUID victimUUID = victim.getUniqueId();
                int spellStrength = manager.getConfig().spellStrength.get("POISON");
                int spellSubStrength = manager.getConfig().spellSubStrength.get("POISON");

                counter.put(victimUUID, 0); // счётчик, нужен для отключения таймера

                new BukkitRunnable() { // наносим урон ежесекундно
                    public void run() {
                        counter.put(victimUUID, counter.get(victimUUID)+1); // увеличиваем счётчик на единицу

                        if(counter.get(victimUUID) == spellSubStrength) {
                            counter.remove(victimUUID);
                            cancel(); // если таймер равен макс. длительности яда, то отрубаем его
                        }

                        double damage = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * (spellStrength/100F);

                        if(victim instanceof Player) spells.damagePlayerWithArmor((Player) victim, damage); // если энтити игрок - уменьшаем урон в зависимости от брони
                        else ((org.bukkit.entity.LivingEntity)victim).damage(damage); // если нет - просто дамажим
                    }
                }.runTaskTimer(manager.getPlugin(), 0, 20L);

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

            if (item.getType().equals(Material.BOOK) && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Заклинание отравления")) { // если игрок активирует заклинание яда

                UUID uuid = p.getUniqueId();
                int waitingTime = manager.getConfig().spellWaitingTime.get("POISON");
                int cooldownTime = manager.getConfig().spellCooldown.get("POISON");

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

                spells.activateSpell(p, "POISON", Sound.ENTITY_WITCH_AMBIENT);
                int task = Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), () -> { // по истечению WaitingTime отправляем заклинание на кулдаун

                    spells.deactivateSpell(p);
                    spellCancelTask.remove(uuid);
                }, waitingTime*20L);

                spellCancelTask.put(uuid, task);
            }
        }
    }
}
