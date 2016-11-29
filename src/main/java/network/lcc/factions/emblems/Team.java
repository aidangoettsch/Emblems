package network.lcc.factions.emblems;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import network.lcc.factions.emblems.entities.Emblem;
import network.lcc.factions.emblems.entities.EmblemState;
import network.lcc.factions.emblems.entities.Monument;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {
  private String name;
  private OfflinePlayer captain;
  private Faction faction;
  private List<OfflinePlayer> players = new ArrayList<>();
  private Main plugin;
  private Monument monument;
  private Emblem emblem;
  private FileConfiguration emblems;
  private DyeColor color;

  public Team(String name, Main plugin) {
    emblems = plugin.getEmblemData();
    captain = plugin.getServer().getOfflinePlayer(UUID.fromString((String) emblems.get("teams." + name + ".captain")));
    faction = FactionColl.get().getByName((String) emblems.get("teams." + name + ".faction"));
    this.plugin = plugin;
    List<String> playerUUIDs = (List<String>) emblems.getList("teams." + name + ".players");
    for (String uuid : playerUUIDs) {
      players.add(plugin.getServer().getOfflinePlayer(UUID.fromString(uuid)));
    }

  }

  public Team(String name, Player captain, Main plugin, DyeColor color, Material emblemMaterial, ProtectedRegion region) {
    this.name = name;
    this.captain = plugin.getServer().getOfflinePlayer(captain.getUniqueId());
    this.plugin = plugin;
    this.color = color;
    if (FactionColl.get().getByName(name) != null) faction = FactionColl.get().getByName(name);
    else plugin.createFaction(captain, name);
    addPlayer(captain);
    emblems = plugin.getEmblemData();
    Vector v1 = region.getMinimumPoint();
    Vector v2 = region.getMaximumPoint();
    System.out.println(color.toString());
    System.out.println(emblemMaterial.toString());
    monument = new Monument(this, plugin, captain.getWorld(), v1.getBlockX(), v1.getBlockY(), v1.getBlockZ(), v2.getBlockX(), v2.getBlockY(), v2.getBlockZ());
    emblem = new Emblem(this, plugin, monument, monument, monument.getOwnEmblem(), null, emblemMaterial);
    emblem.updatePosition(monument, null, null);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OfflinePlayer getCaptain() {
    return captain;
  }

  public void setCaptain(Player captain) {
    this.captain = captain;
  }

  public Faction getFaction() {
    return faction;
  }

  public void setFaction(Faction faction) {
    this.faction = faction;
  }

  public Emblem getEmblem() {
    return emblem;
  }

  public DyeColor getColor() {
    return color;
  }

  public Monument getMonument() {
    return monument;
  }

  public void addPlayer(Player p) {
    players.add(players.size(), p);
  }

  public ArrayList<OfflinePlayer> getPlayers() {
    return (ArrayList<OfflinePlayer>) players;
  }

  public void save() {
    emblems.set("teams." + name + ".captain", captain.getUniqueId().toString());
    emblems.set("teams." + name + ".faction", faction.getName());
    List<String> playerUUIDs = new ArrayList<>();
    for (OfflinePlayer player : players) {
      playerUUIDs.add(player.getUniqueId().toString());
    }
    emblems.set("teams." + name + ".players", playerUUIDs);
  }

  public void win() {
    plugin.broadcast(name + " has won!");
  }
}
