package MagicSpells.listener;

import MagicSpells.items.ClassPowerItems;
import MagicSpells.manager.Manager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SubListeners implements Listener {

    private Manager manager;
    private ClassPowerItems classItems;

    private final Map<UUID, List<ItemStack>> keepItems = new HashMap<>();

    public SubListeners(Manager manager) {
        this.manager = manager;
        this.classItems = new ClassPowerItems(manager);
    }

    @EventHandler
    public void onDrop (PlayerDropItemEvent e) {
        Item item = e.getItemDrop();

        if(item.getItemStack().hasItemMeta() && isItemClassPower(item.getItemStack())) e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        final List<ItemStack> toKeep = new ArrayList<>();

        for (ItemStack item : e.getDrops()) // тут сохраняем способки классов при смерти
            if (item != null && item.hasItemMeta() && isItemClassPower(item)) toKeep.add(item);

        if (!toKeep.isEmpty())
        {
            e.getDrops().removeAll(toKeep);
            keepItems.put(e.getEntity().getUniqueId(), toKeep);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        final List<ItemStack> toRestore = keepItems.get(e.getPlayer().getUniqueId());

        if (toRestore != null) // перевыдаем способки классов при возрождении
        {
            e.getPlayer().getInventory().addItem(toRestore.toArray(new ItemStack[0]));
            keepItems.remove(e.getPlayer().getUniqueId());
        }
    }

    private boolean isItemClassPower(ItemStack item) {
        String itemName = item.getItemMeta().getDisplayName();
        return itemName.equals(ChatColor.RED + "Рык Дракона") || itemName.equals(ChatColor.AQUA + "Несокрушимость") || itemName.equals(ChatColor.AQUA + "Глаз-Алмаз")
                || itemName.equals(ChatColor.GREEN + "Благословение Природы") || itemName.equals(ChatColor.YELLOW + "Огнерождённый") || itemName.equals(ChatColor.DARK_GRAY + "Тёмное Проклятие");
    }

}
