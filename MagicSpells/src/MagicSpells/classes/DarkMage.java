package MagicSpells.classes;

import MagicSpells.manager.Manager;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class DarkMage implements Listener {

    private Manager manager;

    private HashMap<UUID, Long> powerCooldownLeft = new HashMap<>();
    private HashMap<UUID, Boolean> isPowerActivated = new HashMap<>();

    public DarkMage(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void onHit(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player) { // если любое энтити ударило игрока

            Player victim = (Player) e.getEntity();
            Entity damager = e.getDamager();

            String victimClass = manager.getCheckData().playerClass.get(victim.getUniqueId().toString());

            if(victimClass.equals("DARKMAGE") && e.getDamager() instanceof LivingEntity) { // если ударили тёмного мага
                double damage = e.getDamage() * (manager.getConfig().classPassivePowerStrength.get("DARKMAGE")/100F);
                ((org.bukkit.entity.LivingEntity)damager).damage(damage); // отражаем процент урона из конфига в атакующего
            }

            if(damager instanceof Player) { // если атакующим был игрок
                String damagerClass = manager.getCheckData().playerClass.get(damager.getUniqueId().toString());

                if (damagerClass.equals("DARKMAGE") && isPowerActivated.containsKey(damager.getUniqueId()) && isPowerActivated.get(damager.getUniqueId())) {

                    int duration = manager.getConfig().classPowerStrength.get("DARKMAGE") * 20; // если атакующим был тёмный маг с активированной способкой

                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 1)); // накладываем негативные эффекты
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 1));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, 1));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, 0));
                }
            }
        }
        else if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)) { // если игрок ударил любое энтити кроме игрока

            Player damager = (Player) e.getDamager();
            String damagerClass = manager.getCheckData().playerClass.get(damager.getUniqueId().toString());

            if(damagerClass.equals("DARKMAGE") && isPowerActivated.containsKey(damager.getUniqueId()) && isPowerActivated.get(damager.getUniqueId())) { // если игрок был тёмным магом с активированной способкой
                double newDamage = e.getDamage() * (manager.getConfig().classPowerSubStrength.get("DARKMAGE") / 100F);
                e.setDamage(newDamage); // увеличиваем урон на значение из конфига
            }
        }
    }

    @EventHandler
    private void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta() ) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if(item.getType().equals(Material.BLAZE_ROD) && item.getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "Тёмное Проклятие")) { // когда игрок активирует способку тёмного мага

                int cooldownTime = manager.getConfig().classPowerCooldown.get("DARKMAGE");
                int duration = manager.getConfig().classPowerDuration.get("DARKMAGE")*20;

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
}
