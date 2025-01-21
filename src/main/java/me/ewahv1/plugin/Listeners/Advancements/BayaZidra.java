package me.ewahv1.plugin.Listeners.Advancements;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ewahv1.plugin.Utils.GenerarUserBolsaData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.File;
import java.util.Map;

public class BayaZidra implements Listener {

  private final JavaPlugin plugin;

  public BayaZidra(JavaPlugin plugin) {
    this.plugin = plugin;
    Bukkit.getPluginManager().registerEvents(this, plugin);
    plugin.getLogger().info("[DEBUG][BayaZidra.java] Listener de Baya Zidra registrado correctamente.");
  }

  @EventHandler
  public void onPlayerDamage(EntityDamageEvent event) {
    Bukkit.getLogger().info("[DEBUG][BayaZidra.java][onPlayerDamage] Evento onPlayerDamage disparado.");

    if (!(event.getEntity() instanceof Player)) {
      Bukkit.getLogger().info("[DEBUG][BayaZidra.java][onPlayerDamage] Entidad no es un jugador, ignorando evento.");
      return;
    }

    Player player = (Player) event.getEntity();
    double health = player.getHealth();
    double maxHealth = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getDefaultValue();

    Bukkit.getLogger()
        .info("[DEBUG][BayaZidra.java][onPlayerDamage] Vida actual del jugador " + player.getName() + ": " + health);

    if (health >= 6) {
      Bukkit.getLogger().info(
          "[DEBUG][BayaZidra.java][onPlayerDamage] La vida del jugador es mayor o igual a 6. No se aplica Baya Zidra.");
      return;
    }

    String playerUUID = player.getUniqueId().toString();
    Inventory bolsa = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Bolsa de Trinkets");
    GenerarUserBolsaData.loadPlayerBolsa(playerUUID, bolsa);

    File trinketsFile = new File(plugin.getDataFolder(), "trinkets.yml");
    if (!trinketsFile.exists()) {
      plugin.getLogger().severe("[ERROR][BayaZidra.java][onPlayerDamage] El archivo trinkets.yml no existe.");
      return;
    }

    FileConfiguration config = YamlConfiguration.loadConfiguration(trinketsFile);
    Map<String, Object> bayaZidraData = config.getConfigurationSection("Trinkets.Advancements.BayaZidra")
        .getValues(false);

    if (bayaZidraData == null) {
      plugin.getLogger().severe(
          "[ERROR][BayaZidra.java][onPlayerDamage] No se encontró la configuración de Baya Zidra en trinkets.yml.");
      return;
    }

    int consumable = (int) bayaZidraData.getOrDefault("consumable", 0);
    Bukkit.getLogger()
        .info("[DEBUG][BayaZidra.java][onPlayerDamage] Propiedad consumable para Baya Zidra: " + consumable);

    for (int i = 0; i < bolsa.getSize(); i++) {
      ItemStack item = bolsa.getItem(i);
      if (item != null && item.hasItemMeta() &&
          item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&2&lBayaZidra"))) {
        Bukkit.getLogger()
            .info("[DEBUG][BayaZidra.java][onPlayerDamage] Baya Zidra encontrada en la bolsa del jugador.");

        double healAmount = Math.min(health + (maxHealth / 2), maxHealth); // Curar la mitad de la vida máxima
        player.setHealth(healAmount);

        sendActionBar(player, ChatColor.GREEN + "La Baya Zidra te ha curado la mitad de tu vida máxima!");
        plugin.getLogger().info("[DEBUG][BayaZidra.java][onPlayerDamage] El jugador " + player.getName()
            + " fue curado por la Baya Zidra.");

        // Verificar si el trinket es consumible
        if (consumable == 1) {
          bolsa.setItem(i, null); // Eliminar el trinket
          GenerarUserBolsaData.savePlayerBolsa(playerUUID, bolsa);
          plugin.getLogger()
              .info("[DEBUG][BayaZidra.java][onPlayerDamage] La Baya Zidra fue consumida y eliminada de la bolsa.");
        } else {
          Bukkit.getLogger().info("[DEBUG][BayaZidra.java][onPlayerDamage] La Baya Zidra no es consumible.");
        }
        break;
      }
    }
  }

  private void sendActionBar(Player player, String message) {
    Bukkit.getLogger().info("[DEBUG][BayaZidra.java][sendActionBar] Enviando mensaje al ActionBar: " + message);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
  }
}
