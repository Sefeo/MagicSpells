package MagicSpells.classes;

import MagicSpells.manager.Manager;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Archer implements Listener {

    private Manager manager;

    private HashMap<UUID, Boolean> arrowUnpickable = new HashMap<>(); // чтобы фри стрелы нельзя было дюпать
    private HashMap<UUID, Player> playerNewArrow = new HashMap<>(); // владелец бесплатной стрелы из способкки
    private HashMap<UUID, Player> playerNormalArrow = new HashMap<>(); // владелец ориг стрелы

    private HashMap<UUID, Long> powerCooldownLeft = new HashMap<>();
    private HashMap<UUID, Boolean> isPowerActivated = new HashMap<>();

    public Archer(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void onBowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            String playerClass = manager.getCheckData().playerClass.get(p.getUniqueId().toString());

            if (playerClass.equals("ARCHER") && isPowerActivated.containsKey(p.getUniqueId()) && isPowerActivated.get(p.getUniqueId())) { // если выстрелил лучник с активной способкой

                ItemStack arrowItem = new ItemStack(Material.ARROW);
                p.getInventory().addItem(arrowItem);
                p.getInventory().removeItem(arrowItem);

                Arrow newArrowProjectile = p.launchProjectile(Arrow.class); // создаем вторую бесплатную стрелу

                arrowUnpickable.put(e.getProjectile().getUniqueId(), true);
                arrowUnpickable.put(newArrowProjectile.getUniqueId(), true); // делаем две бесплатные стрелы неподбираемыми

                playerNewArrow.put(newArrowProjectile.getUniqueId(), ((Player) e.getEntity()).getPlayer()); // записываем чья новая стрела

            }
            playerNormalArrow.put(e.getProjectile().getUniqueId(), ((Player) e.getEntity()).getPlayer()); // записываем чья обычная стрела
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Arrow) { // если кого-то ударила стрела
            Arrow arrow = (Arrow) e.getDamager();

            if(!playerNewArrow.containsKey(arrow.getUniqueId()) && !playerNormalArrow.containsKey(arrow.getUniqueId())) return; // если стрела не привязана к лучнику, то ретурнимся

            Player p;
            if (playerNewArrow.containsKey(arrow.getUniqueId())) p = playerNewArrow.get(arrow.getUniqueId());
            else p = playerNormalArrow.get(arrow.getUniqueId()); // записываем игрока к которому она привязана

            String playerClass = manager.getCheckData().playerClass.get(p.getUniqueId().toString());
            if(!playerClass.equals("ARCHER")) return;

            double additionalHealth = e.getDamage() * (manager.getConfig().classPassivePowerStrength.get("ARCHER") / 100F);
            double health = p.getHealth() + additionalHealth;

            if (p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() > health) p.setHealth(health);
            else p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); // регеним игроку ХП от урона стрелы

            if(isPowerActivated.containsKey(p.getUniqueId()) && isPowerActivated.get(p.getUniqueId())) { // если у лучника включена способка

                double additionalDamage = e.getDamage() * (manager.getConfig().classPowerStrength.get("ARCHER") / 100F);
                double damage = e.getDamage() + additionalDamage;

                e.setDamage(damage); // то увеличиваем урон на значение из конфига
            }
        }
    }

    @EventHandler
    private void onArrowPickUp(PlayerPickupArrowEvent e) {
        Arrow arrow = e.getArrow();
        if(arrowUnpickable.containsKey(arrow.getUniqueId())) {
            e.setCancelled(true); // не даем поднять бесплатные стрелы
            arrow.remove(); // и удаляем их физически
            arrowUnpickable.remove(arrow.getUniqueId()); // + чистим список
        }
    }

    @EventHandler
    private void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta() ) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if(item.getType().equals(Material.BLAZE_ROD) && item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Глаз-Алмаз")) { // когда игрок активирует способку лучника

                int cooldownTime = manager.getConfig().classPowerCooldown.get("ARCHER");
                int duration = manager.getConfig().classPowerDuration.get("ARCHER")*20;
                int effectsLevel =  manager.getConfig().classPowerSubStrength.get("ARCHER")-1;

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

                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, effectsLevel));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, effectsLevel));

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
