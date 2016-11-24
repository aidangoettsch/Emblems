package network.lcc.factions.emblems.entities;

import network.lcc.factions.emblems.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public class Emblem {
  private Team team;
  private Material material;
  private Monument homeMonument;
  private Monument currentMonument;
  private Block block;
  private ArrayList<Block> additionalBlocks;
  private Player carrier;
  private EmblemState state;

  public Emblem(Team team, Monument homeMonument, Monument currentMonument, Block block, EmblemState state) {
    this.team = team;
    this.homeMonument = homeMonument;
    this.currentMonument = currentMonument;
    this.block = block;
    this.state = state;
  }

  public void updatePosition(Monument newMonument, Player newCarrier, Location groundLoc) {
    if (state == EmblemState.CAPTURED || state == EmblemState.IN_MONUMENT) {
      currentMonument.removeEmblem(this);
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
      for (Block block : additionalBlocks) {
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
    }

    if (state == EmblemState.IN_MONUMENT) {
      if (block != null) {
        block.setType(Material.AIR);
      }
      block = currentMonument.getOwnEmblem();
      block.setType(material);
    } else if (state == EmblemState.CAPTURED) {
      if (block != null) {
        block.setType(Material.AIR);
      }
      block = currentMonument.getCapturePoints().get(team);
      block.setType(material);
      currentMonument.capture(this);
    } else if(state == EmblemState.IN_INVENTORY) {
      PlayerInventory inv = carrier.getInventory();
      for (ItemStack is : inv) {
        if (is.getType() == Material.COMPASS) {
          is.setAmount(1);
          is.setType(material);
        }
      }
    } else if (state == EmblemState.ON_GROUND) {

    }
  }
}
