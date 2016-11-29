package network.lcc.factions.emblems.command;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import network.lcc.factions.emblems.Main;
import network.lcc.factions.emblems.Team;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmblemsCommandListener implements CommandExecutor {
  private Main plugin;

  public EmblemsCommandListener(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("e") || command.getName().equalsIgnoreCase("em") || command.getName().equalsIgnoreCase("emblems")) {
      if (args.length == 0) {
        // TODO: Error
        return false;
      }
      if (args[0].equalsIgnoreCase("teams") && args.length > 1) {
        return teamsCommand(commandSender, command, label, args);
      } else {
        commandSender.sendMessage("you did wrong");
        return false;
      }
    }
    return false;
  }

  private boolean teamsCommand(CommandSender commandSender, Command command, String label, String[] args) {
    WorldGuardPlugin wg = plugin.getWorldGuard();
    if (args[1].equalsIgnoreCase("add") && args.length == 7) {
      Player p = plugin.getServer().getPlayer(args[3]);
      if (p == null) {
        commandSender.sendMessage(args[0] + " is not online!");
        return false;
      }
      Team t = new Team(args[2], p, plugin, DyeColor.valueOf(args[4]), Material.valueOf(args[5]), wg.getRegionManager(p.getWorld()).getRegion(args[4]));
      plugin.createTeam(t);
    } else if (args[1].equalsIgnoreCase("add") && args.length == 6) {
      if (commandSender instanceof  Player) {
        Team t = new Team(args[2], (Player) commandSender, plugin, DyeColor.valueOf(args[4]), Material.valueOf(args[5]), wg.getRegionManager(((Player) commandSender).getWorld()).getRegion(args[3]));
        plugin.createTeam(t);
      } else {
        commandSender.sendMessage("This command must be run as a player.");
      }
    } else {
      commandSender.sendMessage("you did wrong");
      return false;
    }
    return false;
  }
}
