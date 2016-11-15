package network.lcc.factions.emblems;

import com.sun.javaws.exceptions.InvalidArgumentException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Main extends JavaPlugin {
  private File emblemsFile;
  private FileConfiguration emblems;
  private FileConfiguration cfg;
  private GameState gameState;

  @Override
  public void onEnable(){
    emblemsFile = new File(this.getDataFolder(), "emblems.yml");
    if (!emblemsFile.exists()) {
      saveResource("emblems.yml", false);
      emblemsFile = new File(this.getDataFolder(), "emblems.yml");
    }
    emblems = YamlConfiguration.loadConfiguration(emblemsFile);
    cfg = getConfig();

    try {
      updateGameState(GameState.valueOf((String) emblems.get("game.status.gamestate")));
    } catch (InvalidArgumentException e) {
      gameState = GameState.valueOf((String) emblems.get("game.status.gamestate"));
    }
  }

  @Override
  public void onDisable(){
    saveEmblemData();
  }

  public FileConfiguration getEmblemData() {
    return emblems;
  }

  public void saveEmblemData() {
    try {
      emblems.save(emblemsFile);
    } catch (IOException e) {
      this.getLogger().log(Level.SEVERE, "Could not save config to " + emblemsFile, e);
    }
  }

  public GameState getGameState() {
    return gameState;
  }

  public void updateGameState(GameState newState) throws InvalidArgumentException {
    switch (newState) {
      case OFF:
        throw new IllegalArgumentException("Cannot reset game after start.");
      case PICKING_TEAMS:
        broadcast("Now picking teams!");
        broadcast("All players have been teleported to spawn!");
    }
  }

  private void broadcast(String msg) {
    TextComponent broadcast = new TextComponent("[");
    broadcast.setColor(ChatColor.YELLOW);
    TextComponent prefix = new TextComponent((String) cfg.get("chat.prefix"));
    prefix.setColor(ChatColor.AQUA);
    broadcast.addExtra(prefix);
    broadcast.addExtra("] ");
    TextComponent chatBody = new TextComponent(msg);
    chatBody.setColor(ChatColor.WHITE);
    broadcast.addExtra(chatBody);
    getServer().spigot().broadcast(broadcast);
  }
}
