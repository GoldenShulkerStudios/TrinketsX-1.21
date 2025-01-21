package me.ewahv1.plugin.Listeners.Advancements;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.ewahv1.plugin.Utils.GenerarUserBolsaData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MissingNo implements Listener {

  private final JavaPlugin plugin;
  private final Random random = new Random();

  public MissingNo(JavaPlugin plugin) {
    this.plugin = plugin;
    Bukkit.getPluginManager().registerEvents(this, plugin);
    plugin.getLogger().info("[DEBUG][MissingNo.java] Listener de MissingNo registrado correctamente.");
  }

  @EventHandler
  public void onPlayerDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();
    String playerUUID = player.getUniqueId().toString();
    Inventory bolsa = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Bolsa de Trinkets");
    GenerarUserBolsaData.loadPlayerBolsa(playerUUID, bolsa);

    File trinketsFile = new File(plugin.getDataFolder(), "trinkets.yml");
    if (!trinketsFile.exists()) {
      plugin.getLogger().severe("[ERROR][MissingNo.java][onPlayerDamage] El archivo trinkets.yml no existe.");
      return;
    }

    FileConfiguration config = YamlConfiguration.loadConfiguration(trinketsFile);
    Map<String, Object> missingNoData = config.getConfigurationSection("Trinkets.Advancements.MissigNo")
        .getValues(false);
    if (missingNoData == null) {
      plugin.getLogger().severe(
          "[ERROR][MissingNo.java][onPlayerDamage] No se encontró la configuración de MissingNo en trinkets.yml.");
      return;
    }

    int consumable = (int) missingNoData.getOrDefault("consumable", -1);
    for (int i = 0; i < bolsa.getSize(); i++) {
      ItemStack item = bolsa.getItem(i);
      if (item != null && item.hasItemMeta() &&
          item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&7&lMissingNo"))) {
        if (random.nextInt(100) < 5) { // 5% de probabilidad
          Bukkit.getLogger().info("[DEBUG][MissingNo.java][onPlayerDamage] Activando efecto de MissingNo.");
          changeArmor(player);

          if (consumable == 1) {
            bolsa.setItem(i, null); // Eliminar el trinket si es consumible
            GenerarUserBolsaData.savePlayerBolsa(playerUUID, bolsa);
            plugin.getLogger()
                .info("[DEBUG][MissingNo.java][onPlayerDamage] MissingNo consumido y eliminado de la bolsa.");
          } else {
            plugin.getLogger().info("[DEBUG][MissingNo.java][onPlayerDamage] MissingNo no es consumible.");
          }
        }
        break;
      }
    }
  }

  private void changeArmor(Player player) {
    ItemStack[] armor = player.getInventory().getArmorContents();
    Map<Material, Integer> durabilityMap = getDurabilityMap();

    for (int i = 0; i < armor.length; i++) {
      ItemStack piece = armor[i];
      if (piece != null && piece.getType() != Material.AIR) {
        Material newMaterial = getRandomArmorMaterial(piece.getType());
        if (newMaterial != null) {
          ItemStack newPiece = new ItemStack(newMaterial);

          // Copiar metadatos y encantamientos
          ItemMeta meta = piece.getItemMeta();
          if (meta != null) {
            newPiece.setItemMeta(meta);
          }

          // Calcular durabilidad proporcional
          int maxDurabilityOld = durabilityMap.getOrDefault(piece.getType(), (int) piece.getType().getMaxDurability());

          int maxDurabilityNew = durabilityMap.getOrDefault(newMaterial, (int) newMaterial.getMaxDurability());

          short newDurability = (short) ((piece.getDurability() / (double) maxDurabilityOld) * maxDurabilityNew);
          newPiece.setDurability(newDurability);

          armor[i] = newPiece;
          plugin.getLogger().info("[DEBUG][MissingNo.java][changeArmor] Cambió una pieza de armadura: "
              + piece.getType() + " -> " + newMaterial);
        }
      }
    }
    player.getInventory().setArmorContents(armor);
  }

  private Material getRandomArmorMaterial(Material current) {
    Material[][] armorTiers = {
        { Material.LEATHER_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET,
            Material.NETHERITE_HELMET },
        { Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE,
            Material.DIAMOND_CHESTPLATE,
            Material.NETHERITE_CHESTPLATE },
        { Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS,
            Material.NETHERITE_LEGGINGS },
        { Material.LEATHER_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_BOOTS }
    };

    for (Material[] tier : armorTiers) {
      for (Material material : tier) {
        if (material == current) {
          return tier[random.nextInt(tier.length)];
        }
      }
    }
    return null;
  }

  private Map<Material, Integer> getDurabilityMap() {
    Map<Material, Integer> durabilityMap = new HashMap<>();
    durabilityMap.put(Material.LEATHER_HELMET, 55);
    durabilityMap.put(Material.IRON_HELMET, 165);
    durabilityMap.put(Material.GOLDEN_HELMET, 77);
    durabilityMap.put(Material.DIAMOND_HELMET, 363);
    durabilityMap.put(Material.NETHERITE_HELMET, 407);

    durabilityMap.put(Material.LEATHER_CHESTPLATE, 80);
    durabilityMap.put(Material.IRON_CHESTPLATE, 240);
    durabilityMap.put(Material.GOLDEN_CHESTPLATE, 112);
    durabilityMap.put(Material.DIAMOND_CHESTPLATE, 528);
    durabilityMap.put(Material.NETHERITE_CHESTPLATE, 592);

    durabilityMap.put(Material.LEATHER_LEGGINGS, 75);
    durabilityMap.put(Material.IRON_LEGGINGS, 225);
    durabilityMap.put(Material.GOLDEN_LEGGINGS, 105);
    durabilityMap.put(Material.DIAMOND_LEGGINGS, 495);
    durabilityMap.put(Material.NETHERITE_LEGGINGS, 555);

    durabilityMap.put(Material.LEATHER_BOOTS, 65);
    durabilityMap.put(Material.IRON_BOOTS, 195);
    durabilityMap.put(Material.GOLDEN_BOOTS, 91);
    durabilityMap.put(Material.DIAMOND_BOOTS, 429);
    durabilityMap.put(Material.NETHERITE_BOOTS, 481);

    return durabilityMap;
  }
}
