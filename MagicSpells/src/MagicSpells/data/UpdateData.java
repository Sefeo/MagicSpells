package MagicSpells.data;

import MagicSpells.Main;
import MagicSpells.manager.Manager;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateData {

    private Manager manager;

    public UpdateData(Manager manager) {
        this.manager = manager;
    }

    public void setPlayerClass(Player p, String playerClass) {
        try {
            PreparedStatement preparedStatement = Main.getInstance().getConnection().prepareStatement(
                    "UPDATE " + manager.getConfig().dbTable + " SET Class = '" + playerClass + "' WHERE UUID = ?"
            );
            preparedStatement.setString(1, p.getUniqueId().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
