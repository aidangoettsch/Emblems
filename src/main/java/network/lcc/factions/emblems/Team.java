package network.lcc.factions.emblems;

import org.bukkit.entity.Player;

public class Team {
  private String name;
  private Player capitan;
  private Factions faction;

  public Team(String name, Player capitan) {
    this.name = name;
    this.capitan = capitan;
  }

  public Player getCapitan() {
    return capitan;
  }

  public void setCapitan(Player capitan) {
    this.capitan = capitan;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
