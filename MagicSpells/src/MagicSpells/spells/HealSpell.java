package MagicSpells.spells;

import MagicSpells.manager.Manager;
import MagicSpells.manager.SpellManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HealSpell implements Listener {

    private Manager manager;
    private SpellManager spells;

    public HealSpell(Manager manager) {
        this.manager = manager;
        this.spells = manager.getSpellManager();
    }

    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && !e.getItem().getType().equals(Material.AIR) && e.getItem().hasItemMeta()) { // проверка на воздух, чтобы 100% не было ерроров в консоли
            ItemStack item = e.getItem();

            if (item.getType().equals(Material.BOOK) && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Заклинание исцеления")) { // если игрок активирует заклинание исцеления

                UUID uuid = p.getUniqueId();
                int cooldownTime = manager.getConfig().spellCooldown.get("HEAL");

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

                double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double healAmout = maxHealth * (manager.getConfig().spellStrength.get("HEAL")/100F);
                double newHealth = p.getHealth()+healAmout;

                if(newHealth < maxHealth) p.setHealth(newHealth);
                else p.setHealth(maxHealth);
            }
        }
    }
}
