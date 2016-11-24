package network.lcc.factions.emblems;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import network.lcc.factions.emblems.command.EmblemsCommandListener;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
  private File emblemsFile;
  private FileConfiguration emblems;
  private FileConfiguration cfg;
  private GameState gameState;
  private List<Team> teams = new ArrayList<>();
  private Logger logger;

  @Override
  public void onEnable(){
    logger = getLogger();
    saveResource("emblems.yml", false);
    emblemsFile = new File(getDataFolder(), "emblems.yml");
    emblems = YamlConfiguration.loadConfiguration(emblemsFile);
    getConfig().options().copyDefaults(true);
    saveDefaultConfig();
    cfg = getConfig();
    load();

    try {
      updateGameState(GameState.valueOf((String) emblems.get("game.status.gamestate")));
    } catch (Exception e) {
      gameState = GameState.valueOf((String) emblems.get("game.status.gamestate"));
    }

    this.getCommand("e").setExecutor(new EmblemsCommandListener(this));
    this.getCommand("em").setExecutor(new EmblemsCommandListener(this));
    this.getCommand("emblems").setExecutor(new EmblemsCommandListener(this));
  }

  @Override
  public void onDisable(){
    saveEmblemData();
  }

  private void save() {
    for (Team team : teams) {
      team.save();
    }
  }

  private void load() {
    if (emblems.getConfigurationSection("teams") != null) {
      Set<String> keys = emblems.getConfigurationSection("teams").getKeys(false);
      for (String key : keys) {
        teams.add(new Team(key, this));
      }
    }
  }

  public FileConfiguration getEmblemData() {
    return emblems;
  }

  public void saveEmblemData() {
    save();
    try {
      emblems.save(emblemsFile);
    } catch (IOException e) {
      this.getLogger().log(Level.SEVERE, "Could not save config to " + emblemsFile, e);
    }
  }

  public GameState getGameState() {
    return gameState;
  }

  public List<Team> getTeams() {
    return teams;
  }

  public void updateGameState(GameState newState) throws Exception {
    switch (newState) {
      case OFF:
        throw new Exception("Cannot reset game after start.");
      case PICKING_TEAMS:
        broadcast("Now picking teams!");
        broadcast("All players have been teleported to spawn!");
    }
    saveEmblemData();
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

  public void createFaction(Player leader, String name) {
    try {
      leader.setOp(true);
      getServer().dispatchCommand(leader, "f create " + name);
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      leader.setOp(false);
    }
  }

  public void createTeam(Team team) {
    teams.add(team);
    saveEmblemData();
  }

  @Nullable
  private WorldGuardPlugin getWorldGuard() {
    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
      return null;
    }

    return (WorldGuardPlugin) plugin;
  }

  public Location upOne(Location location) {
    location.setY(location.getY() + 1);
    return location;
  }
}
