package MagicSpells.classes;

import MagicSpells.items.ClassPowerItems;
import MagicSpells.manager.Manager;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Tank implements Listener {

    private Manager manager;

    private HashMap<UUID, Long> powerCooldownLeft = new HashMap<>();
    private HashMap<UUID, Boolean> isPowerActivated = new HashMap<>();

    public Tank(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta() ) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if(item.getType().equals(Material.BLAZE_ROD) && item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Несокрушимость")) { // когда игрок активирует способку танка

                int cooldownTime = manager.getConfig().classPowerCooldown.get("TANK");
                int duration = manager.getConfig().classPowerDuration.get("TANK")*20;

                if(isPowerActivated.containsKey(p.getUniqueId()) && isPowerActivated.get(p.getUniqueId())) {
                    p.sendMessage(ChatColor.RED + "Способность уже активирована");
                    return;
                }
                else if (powerCooldownLeft.containsKey(p.getUniqueId())) {
                    long secondsLeft = ((powerCooldownLeft.get(p.getUniqueId()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                    if(secondsLeft > 0) { // если способность неактивна но еще не откатилась
                        p.sendMessage(ChatColor.YELLOW + "Вы сможете использовать способность через " + secondsLeft + " секунд");
                        return;
                    }
                }

                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 0));

                p.sendMessage(ChatColor.GREEN + "Способность активирована");
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 1);
                isPowerActivated.put(p.getUniqueId(), true);

                Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), () -> { // деактивируем способку по прошествию действия
                    p.sendMessage(ChatColor.YELLOW + "Способность деактивирована");
                    powerCooldownLeft.put(p.getUniqueId(), System.currentTimeMillis());
                    isPowerActivated.put(p.getUniqueId(), false);
                }, duration);

                runClassEffects(p);
            }
        }
    }

    private void runClassEffects(Player p) {
        Location loc = p.getLocation();
        double x = 0.4 * Math.sin(0.4);
        double z = 0.4 * Math.cos(0.4);

        new BukkitRunnable() { // даем эффекты ежесекундно
            public void run() {
                if (!isPowerActivated.get(p.getUniqueId()) || !p.isOnline()) cancel();

                PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float) (loc.getX()+x), (float)(loc.getY() + 1), (float) (loc.getZ() + z), (float) 0.8, (float) 0.8, (float) 0.8, 0, 3, 3);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(particles);

            }
        }.runTaskTimer(manager.getPlugin(), 0, 1);
    }

    @EventHandler
    private void onHit(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            String playerClass = manager.getCheckData().playerClass.get(p.getUniqueId().toString());

            if(playerClass.equals("TANK") && isPowerActivated.containsKey(p.getUniqueId()) && isPowerActivated.get(p.getUniqueId())) { // если ударили танка с активной способкой
                double loweringDamage = e.getDamage() * (manager.getConfig().classPowerStrength.get("TANK") / 100F);
                e.setDamage(e.getDamage() - loweringDamage); // то уменьшаем урон на значение из конфига
            }
        }
    }
}