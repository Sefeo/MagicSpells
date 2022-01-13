package MagicSpells.listener;

import MagicSpells.items.SpellItems;
import MagicSpells.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Handler implements Listener {

    private Manager manager;
    private Inventory classChooseInv;

    private HashMap<String, Boolean> isChoosingClass = new HashMap<>();

    public Handler(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.getInventory().clear();
        e.setJoinMessage(null);

        p.getInventory().addItem(manager.getSpellItems().createAirSpellItem());
        p.getInventory().addItem(manager.getSpellItems().createEarthSpellItem());
        p.getInventory().addItem(manager.getSpellItems().createFireSpellItem());
        p.getInventory().addItem(manager.getSpellItems().createHealSpellItem());
        p.getInventory().addItem(manager.getSpellItems().createPoisonSpellItem());
        p.getInventory().addItem(manager.getSpellItems().createLightningSpellItem());
        p.getInventory().addItem(manager.getSpellItems().createTpSpellItem());

        manager.getCheckData().checkPlayerData(p);
        String playerClass = manager.getCheckData().playerClass.get(p.getUniqueId().toString());

        if (playerClass.equals("Null") && manager.getConfig().isClassChoosingEnabled) { // если у игрока нет класса, то создаем инвентарь его выбора
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            classChooseInv = Bukkit.createInventory(p, 9, "Выбор класса");
            isChoosingClass.put(p.getUniqueId().toString(), true);
            manager.getClassInventory().createClassInvItems(p, classChooseInv);
        }

        else if(playerClass.equals("HEALER")) manager.getHealClass().regenTeamHealth(p); // если игрок хилер, то включаем таймер обновления хила союзников
    }

    @EventHandler
    private void onDisc(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(null);

        String playerClass = manager.getCheckData().playerClass.get(p.getUniqueId().toString());

        if(playerClass.equals("HEALER")) manager.getHealClass().cancelHealTask(p.getUniqueId());
    }

    @EventHandler
    private void onCloseInv(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(e.getInventory().getName().equals("Выбор класса") && isChoosingClass.get(p.getUniqueId().toString())) // если игрок выбирает класс, то при закрытии инвентаря переоткрываем его
            Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), () -> p.openInventory(classChooseInv), 1L);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if(item == null) return;

        if(e.getInventory().getName().equals("Выбор класса")) {

            isChoosingClass.put(p.getUniqueId().toString(), false);
            p.closeInventory();
            e.setCancelled(true);

            switch (String.valueOf(item.getType())) {
                case "IRON_AXE":
                    manager.getClassItems().createBerserkPowerItem(p); // выдаем игроку способку в зависимости от выбранного класса
                    break;
                case "DIAMOND_CHESTPLATE":
                    manager.getClassItems().createTankPowerItem(p);
                    break;
                case "BOW":
                    manager.getClassItems().createArcherPowerItem(p);
                    break;
                case "GOLDEN_APPLE":
                    manager.getClassItems().createHealerPowerItem(p);
                    break;
                case "BLAZE_POWDER":
                    manager.getClassItems().createMagePowerItem(p);
                    break;
                case "NETHER_STAR":
                    manager.getClassItems().createDarkMagePowerItem(p);
                    break;
            }
        }
    }

}
