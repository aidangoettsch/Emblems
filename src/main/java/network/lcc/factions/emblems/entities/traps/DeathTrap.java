package network.lcc.factions.emblems.entities.traps;

import network.lcc.factions.emblems.Main;
import network.lcc.factions.emblems.entities.Monument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class DeathTrap implements MonumentTrap, Listener {
  private Location location;
  private Monument monument;
  private Main plugin;

  public DeathTrap(Location location, Monument monument, Main plugin) {
    this.location = location;
    this.monument = monument;
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    Player p = e.getPlayer();

    if (p.getLocation().getBlockX() == plugin.upOne(location).getBlockX() &&
        p.getLocation().getBlockY() == plugin.upOne(location).getBlockY() &&
        p.getLocation().getBlockZ() == plugin.upOne(location).getBlockZ()) {
      if (!monument.getTeam().getPlayers().contains(plugin.getServer().getOfflinePlayer(p.getUniqueId()))) {
        onTrigger(p);
      }
    }
  }

  @Override
  public void onTrigger(Player p) {
    p.setHealth(0.0D);
  }
}
