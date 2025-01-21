package me.ewahv1.plugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TrinketsDiscover {

  private final JavaPlugin plugin;
  private File ymlFile;
  private FileConfiguration ymlConfig;

  public TrinketsDiscover(JavaPlugin plugin) {
    this.plugin = plugin;

    // Inicializar el archivo YML
    setupYML();
  }

  private void setupYML() {
    ymlFile = new File(plugin.getDataFolder(), "trinkets_discover.yml");
    if (!ymlFile.exists()) {
      try {
        ymlFile.createNewFile();
        ymlConfig = YamlConfiguration.loadConfiguration(ymlFile);
        ymlConfig.createSection("Discoveries");
        saveYML();
        Bukkit.getLogger()
            .info("[DEBUG][TrinketsDiscover.java][setupYML] Archivo trinkets_discover.yml creado correctamente.");
      } catch (IOException e) {
        plugin.getLogger()
            .severe("[ERROR][TrinketsDiscover.java][setupYML] No se pudo crear el archivo trinkets_discover.yml: "
                + e.getMessage());
        e.printStackTrace();
      }
    } else {
      ymlConfig = YamlConfiguration.loadConfiguration(ymlFile);
      Bukkit.getLogger()
          .info("[DEBUG][TrinketsDiscover.java][setupYML] Archivo trinkets_discover.yml cargado correctamente.");
    }
  }

  public boolean isTrinketDiscovered(String trinketName) {
    Bukkit.getLogger()
        .info("[DEBUG][TrinketsDiscover.java][isTrinketDiscovered] Consultando estado de descubrimiento en YML para: "
            + trinketName);
    return ymlConfig.getBoolean("Discoveries." + trinketName, false);
  }

  public void setTrinketDiscovered(String trinketName) {
    Bukkit.getLogger()
        .info("[DEBUG][TrinketsDiscover.java][setTrinketDiscovered] Actualizando descubrimiento en YML para: "
            + trinketName);
    ymlConfig.set("Discoveries." + trinketName, true);
    saveYML();
  }

  private void saveYML() {
    try {
      ymlConfig.save(ymlFile);
      Bukkit.getLogger().info("[DEBUG][TrinketsDiscover.java][saveYML] Archivo YML guardado correctamente.");
    } catch (IOException e) {
      plugin.getLogger()
          .severe("[ERROR][TrinketsDiscover.java][saveYML] No se pudo guardar el archivo trinkets_discover.yml: "
              + e.getMessage());
      e.printStackTrace();
    }
  }
}
