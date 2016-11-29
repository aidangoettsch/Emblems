package network.lcc.factions.emblems.entities;

import network.lcc.factions.emblems.Main;
import network.lcc.factions.emblems.Team;
import network.lcc.factions.emblems.entities.traps.DeathTrap;
import network.lcc.factions.emblems.entities.traps.MonumentTrap;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Colorable;
import org.bukkit.material.Dye;
import org.bukkit.material.Wool;

import java.util.*;

public class Monument implements Listener {
  private Team team;
  private Main plugin;
  private ArrayList<Emblem> emblems = new ArrayList<>();
  private ArrayList<MonumentTrap> traps = new ArrayList<>();
  private World world;
  private Location entrance;
  private Location teleporter;
  private int x1;
  private int y1;
  private int z1;
  private int x2;
  private int y2;
  private int z2;
  private Block ownEmblem;
  private boolean hasOwn;
  private Map<Team, Block> capturePoints = new HashMap<>();

  public Monument(Team team, Main plugin, World world, int x1, int y1, int z1, int x2, int y2, int z2) {
    this.team = team;
    this.plugin = plugin;
    this.world = world;
    this.x1 = x1;
    this.y1 = y1;
    this.z1 = z1;
    this.x2 = x2;
    this.y2 = y2;
    this.z2 = z2;

    scanArea();
  }

  private void scanArea() {
    for (int x = x1; x <= x2; x++) {
      for (int y = y1; y <= y2; y++) {
        for (int z = z1; z <= z2; z++) {
          Block block = world.getBlockAt(x, y, z);
          System.out.println(block.getType().toString());
          if (block.getType() == Material.WOOL) {
            if (block.getData() == DyeColor.GREEN.getData()) {
              this.entrance = plugin.upOne(block.getLocation());
            } else if (block.getData() == DyeColor.BLACK.getData()) {
              traps.add(new DeathTrap(block.getLocation(), this, plugin));
            }
          } else if (block.getType() == Material.IRON_PLATE){
            teleporter = block.getLocation();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
          } else if (block.getType() == Material.STAINED_CLAY) {
            System.out.println(block.getData());
            if (block.getData() == team.getColor().getData()) {
              ownEmblem = block;
            } else {
              for (Team team : plugin.getTeams()) {
                System.out.println(team.getColor());
                if (block.getData() == team.getColor().getData()) {
                  capturePoints.put(team, block);
                }
              }
            }
          }
        }
      }
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    if (teleporter != null && e.getPlayer().getLocation().getBlockX() == teleporter.getBlockX() &&
        e.getPlayer().getLocation().getBlockY() == teleporter.getBlockY()&&
        e.getPlayer().getLocation().getBlockZ() == teleporter.getBlockZ()) {
      Location target = teleporter;
      target.setY(target.getBlockY() + 8);
      e.getPlayer().teleport(target);
    }
  }

  public Team getTeam() {
    return team;
  }

  public Location getEntrance() {
    return entrance;
  }

  public Block getOwnEmblem() {
    return ownEmblem;
  }

  public Map<Team, Block> getCapturePoints() {
    return capturePoints;
  }

  public void removeEmblem(Emblem emblem) {
    emblems.remove(emblem);
  }

  public void setHasOwn(boolean hasOwn) {
    this.hasOwn = hasOwn;
    if (hasOwn) {
      plugin.broadcast(team.getName() + " has recovered their emblem!");
    } else {
      plugin.broadcast(team.getName() + " has lost their emblem!");
    }
    if (emblems.size() == 2 && hasOwn) {
      team.win();
    }
  }

  public void capture(Emblem emblem) {
    emblems.add(emblem);
    plugin.broadcast(team.getName() + " has captured the " + emblem.getTeam().getName() + " emblem!");
    if (emblems.size() == 2 && hasOwn) {
      team.win();
    }
  }
}
