package network.lcc.factions.emblems.entities;

import network.lcc.factions.emblems.Main;
import network.lcc.factions.emblems.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public class Emblem implements Listener {
  private Team team;
  private Main plugin;
  private Material material;
  private Monument homeMonument;
  private Monument currentMonument;
  private Block block;
  private ArrayList<Block> additionalBlocks = new ArrayList<>();
  private Player carrier;
  private EmblemState state;

  public Emblem(Team team, Main plugin, Monument homeMonument, Monument currentMonument, Block block, EmblemState state, Material material) {
    this.team = team;
    this.plugin = plugin;
    this.material = material;
    this.homeMonument = homeMonument;
    this.currentMonument = currentMonument;
    this.block = block;
    this.state = state;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void updatePosition(Monument newMonument, Player newCarrier, Location groundLoc) {
    if (state == EmblemState.CAPTURED) {
      currentMonument.removeEmblem(this);
      block.setType(Material.AIR);
      block = null;
    } else if (state == EmblemState.IN_MONUMENT) {
      homeMonument.setHasOwn(false);
      block.setType(Material.AIR);
      block = null;
    } else if (state == EmblemState.IN_INVENTORY) {
      PlayerInventory inv = carrier.getInventory();
      for (ItemStack is : inv) {
        if (is.getType() == material) {
          is.setAmount(1);
          is.setType(Material.COMPASS);
        }
      }
    } else if (state == EmblemState.ON_GROUND) {
      block.setType(Material.AIR);
      block = null;
      for (Block block : additionalBlocks) {
        additionalBlocks.remove(block);
        block.setType(Material.AIR);
      }
    }

    if (newMonument != null) {
      state = EmblemState.CAPTURED;
      if (newMonument == homeMonument) {
        state = EmblemState.IN_MONUMENT;
      }
    } else if (newCarrier != null) {
      state = EmblemState.IN_INVENTORY;
    } else if (groundLoc != null) {
      state = EmblemState.ON_GROUND;
    } else {
      return;
    }

    if (state == EmblemState.IN_MONUMENT) {
      block = currentMonument.getOwnEmblem();
      plugin.upOne(block.getLocation()).getBlock().setType(material);
      currentMonument.setHasOwn(true);
    } else if (state == EmblemState.CAPTURED) {
      block = currentMonument.getCapturePoints().get(team);
      plugin.upOne(block.getLocation()).getBlock().setType(material);
      currentMonument.capture(this);
    } else if(state == EmblemState.IN_INVENTORY) {
      carrier = newCarrier;
      PlayerInventory inv = carrier.getInventory();
      for (ItemStack is : inv) {
        if (is.getType() == Material.COMPASS) {
          is.setAmount(1);
          is.setType(material);
        }
      }
    } else if (state == EmblemState.ON_GROUND) {
      additionalBlocks.add(groundLoc.getBlock());
      groundLoc.getBlock().setType(Material.STONE_SLAB2);
      groundLoc = plugin.upOne(groundLoc);
      groundLoc.getBlock().setType(material);
    }
  }

  public Team getTeam() {
    return team;
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    System.out.println(state.toString());
    if (state == EmblemState.IN_MONUMENT || state == EmblemState.CAPTURED || state == EmblemState.ON_GROUND) {
      if (e.getClickedBlock() == block || additionalBlocks.contains(e.getClickedBlock())) {
        updatePosition(null, e.getPlayer(), null);
      }
    } else if (state == EmblemState.IN_INVENTORY && e.getPlayer() == carrier) {
      for (Team team : plugin.getTeams()) {
        for (Team monuTeam : plugin.getTeams()) {
          Monument monument = monuTeam.getMonument();
          if (plugin.downOne(e.getClickedBlock().getLocation()).getBlock() == monument.getCapturePoints().get(team)) {
            updatePosition(monument, null, null);
          }
        }
      }
    }
  }
}
