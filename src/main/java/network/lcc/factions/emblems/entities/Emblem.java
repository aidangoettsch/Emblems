package network.lcc.factions.emblems.entities;

import network.lcc.factions.emblems.Team;
import org.bukkit.block.Block;

public class Emblem {
  private Team team;
  private Monument homeMonument;
  private Monument currentMonument;
  private Block block;
  private EmblemState state;

  public Emblem(Team team, Monument homeMonument, Monument currentMonument, Block block, EmblemState state) {
    this.team = team;
    this.homeMonument = homeMonument;
    this.currentMonument = currentMonument;
    this.block = block;
    this.state = state;
  }
}
