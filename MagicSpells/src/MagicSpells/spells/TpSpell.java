package MagicSpells.spells;

import MagicSpells.manager.Manager;
import MagicSpells.manager.SpellManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TpSpell implements Listener {

    private Manager manager;
    private SpellManager spells;

    public TpSpell(Manager manager) {
        this.manager = manager;
        this.spells = manager.getSpellManager();
    }

    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta()) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if (item.getType().equals(Material.BOOK) && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Заклинание телепортации")) { // если игрок активирует заклинание телепорта

                UUID uuid = p.getUniqueId();
                int cooldownTime = manager.getConfig().spellCooldown.get("TP");

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

                int spellStrength = manager.getConfig().spellStrength.get("TP");

                Location target = p.getLocation();
                for (Block b : p.getLineOfSight(null, 200)) { // получаем блок, на который игрок смотрит


                    if(b.getLocation().toVector().distance(p.getLocation().toVector()) > spellStrength) { p.sendMessage(ChatColor.YELLOW + "Блок слишком далеко"); break; }
                    if (!b.getType().equals(Material.AIR)) {
                        if(p.getWorld().getBlockAt(b.getX(), b.getY()+1, b.getZ()).getType().equals(Material.AIR)) target = b.getLocation().add(0, 1, 0);
                        else p.sendMessage(ChatColor.YELLOW + "Нельзя телепортироваться в данную точку");
                        break;
                    }
                    for (Entity g : b.getChunk().getEntities())
                        if (g.getLocation().toVector().distance(b.getLocation().toVector()) < 1) { target = g.getLocation(); break; }
                }

                if(target.equals(p.getLocation())) return; // если сказали игроку, что блок далеко, или в целом что-то пошло не так, то не тпаем его
                p.teleport(target); // если всё норм, то тпаем

                spells.spellCooldownLeft.put(p.getUniqueId(), System.currentTimeMillis()); // ставим КД

                spells.activateInstantSpell(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            }
        }
    }
}
