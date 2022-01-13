package MagicSpells.classes;

import MagicSpells.manager.Manager;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Healer implements Listener {

    private Manager manager;

    private HashMap<UUID, Long> powerCooldownLeft = new HashMap<>();
    private Map<UUID, Integer> healTask = new HashMap<>();

    public Healer(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta() ) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if(item.getType().equals(Material.BLAZE_ROD) && item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Благословение Природы")) { // когда игрок активирует способку хила

                int cooldownTime = manager.getConfig().classPowerCooldown.get("HEALER");
                int effectsDuration = manager.getConfig().classPowerDuration.get("HEALER")*20;
                int effectsLevel =  manager.getConfig().classPowerSubStrength.get("HEALER")-1;

                if (powerCooldownLeft.containsKey(p.getUniqueId())) {
                    long secondsLeft = ((powerCooldownLeft.get(p.getUniqueId()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                    if(secondsLeft > 0) { // если способность еще не откатилась
                        p.sendMessage(ChatColor.YELLOW + "Вы сможете использовать способность через " + secondsLeft + " секунд");
                        return;
                    }
                }

                powerCooldownLeft.put(p.getUniqueId(), System.currentTimeMillis());

                double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double healAmount = maxHealth * (manager.getConfig().classPowerStrength.get("HEALER")/100F);
                double newHealth = p.getHealth() + healAmount;

                if(maxHealth > newHealth) p.setHealth(newHealth);
                else p.setHealth(maxHealth); // регеним игроку ХП от процентов максимального

                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, effectsDuration, effectsLevel));
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                p.sendMessage(ChatColor.GREEN + "Способность активирована");

                runClassEffects(p);
            }
        }
    }

    private void runClassEffects(Player p) {
        Location loc = p.getLocation();
        double x = 0.4 * Math.sin(0.4);
        double z = 0.4 * Math.cos(0.4);

        PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float) (loc.getX()+x), (float)(loc.getY() + 1), (float) (loc.getZ() + z), (float) 0.8, (float) 0.8, (float) 0.8, 0, 3, 3);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(particles);
    }


    public void regenTeamHealth(Player player) { // тут надо будет проверку на то, являются ли игроки дружелюбными, ведь хилить врагов кринж, но пока такой системы нет
        final int healTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(manager.getPlugin(), () -> {
            List<Entity> entities = player.getNearbyEntities(10, 5, 10);

            for (Entity ent : entities) {
                if (!(ent instanceof Player)) continue;
                Player p = (Player) ent;

                double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                int healCount = manager.getConfig().classPassivePowerStrength.get("HEALER");

                if (maxHealth > p.getHealth() + healCount) p.setHealth(p.getHealth() + healCount);
                else p.setHealth(maxHealth);
            }
        }, 20L, manager.getConfig().classPassivePowerSubStrength.get("HEALER")*20);

        healTask.put(player.getUniqueId(), healTimer);
    }

    public void cancelHealTask(UUID uuid) {
        if(healTask.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(healTask.get(uuid));
            healTask.remove(uuid);
        }
    }
}
