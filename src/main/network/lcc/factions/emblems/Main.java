package network.lcc.factions.emblems;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Main extends JavaPlugin {
  private File emblemsFile;
  private FileConfiguration emblems;

  public FileConfiguration getEmblemData() {
    return  emblems;
  }

  @Override
  public void onEnable(){
    emblemsFile = new File(this.getDataFolder(), "emblems.yml");
    if (!emblemsFile.exists()) {
      saveResource("emblems.yml", false);
      emblemsFile = new File(this.getDataFolder(), "emblems.yml");
    }
    emblems = YamlConfiguration.loadConfiguration(emblemsFile);
  }

  @Override
  public void onDisable(){
    saveEmblemData();
  }

  private void saveEmblemData() {
    try {
      emblem.save(emblemsFile);
    } catch (IOException e) {
      this.getLogger().log(Level.SEVERE, "Could not save config to " + emblemsFile, e);
    }
  }
}
