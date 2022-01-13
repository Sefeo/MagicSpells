package MagicSpells.data;

import MagicSpells.Main;
import MagicSpells.manager.Manager;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class CheckData {
    private Manager manager;
    public HashMap<String, String> playerClass = new HashMap<>();

    public CheckData(Manager manager) {
        this.manager = manager;
    }

    public void checkPlayerData(Player player) { // проверка на то, есть ли игрок в БД, тут же сохраняется его класс при заходе
        try {
            PreparedStatement statement = Main.getInstance().getConnection().prepareStatement(
                    "SELECT * FROM `users` WHERE `UUID` = ?"
            );
            statement.setString(1, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();

            boolean playerHasData = result.next();

            if (!playerHasData) {
                playerClass.put(player.getUniqueId().toString(), "Null");

                PreparedStatement preparedStatementOne = Main.getInstance().getConnection().prepareStatement(
                        "INSERT INTO `users`(`UUID`, `Nick`, `Class`) VALUES (?,?,?)"
                );
                preparedStatementOne.setString(1, player.getUniqueId().toString());
                preparedStatementOne.setString(2, player.getDisplayName());
                preparedStatementOne.setString(3, "Null");

                preparedStatementOne.executeUpdate();
            } else {
                statement = Main.getInstance().getConnection().prepareStatement(
                        "SELECT Class FROM `users` WHERE `UUID` = ?"
                );
                statement.setString(1, player.getUniqueId().toString());
                result = statement.executeQuery();
                result.next();

                playerClass.put(player.getUniqueId().toString(), result.getString("Class"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
