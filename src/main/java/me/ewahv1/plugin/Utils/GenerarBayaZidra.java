package me.ewahv1.plugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenerarBayaZidra implements Listener {

    private final JavaPlugin plugin;
    private final File trinketsDiscoverFile;
    private final FileConfiguration trinketsDiscoverConfig;
    private final Set<String> processedPlayers = new HashSet<>();

    public GenerarBayaZidra(JavaPlugin plugin) {
        this.plugin = plugin;

        // Inicializar archivo y configuración predeterminada
        this.trinketsDiscoverFile = new File(plugin.getDataFolder(), "trinkets_discover.yml");
        this.trinketsDiscoverConfig = YamlConfiguration.loadConfiguration(trinketsDiscoverFile);

        if (!trinketsDiscoverFile.exists()) {
            try {
                trinketsDiscoverFile.createNewFile();
                Bukkit.getLogger()
                        .info("[DEBUG][GenerarBayaZidra.java][Constructor] Archivo trinkets_discover.yml creado.");
            } catch (IOException e) {
                Bukkit.getLogger()
                        .severe("[ERROR][GenerarBayaZidra.java][Constructor] No se pudo crear trinkets_discover.yml: "
                                + e.getMessage());
                e.printStackTrace();
            }
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getLogger().info("[DEBUG][GenerarBayaZidra.java][Constructor] Listener registrado correctamente.");
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        NamespacedKey advancementKey = event.getAdvancement().getKey();

        Bukkit.getLogger()
                .info("[DEBUG][GenerarBayaZidra.java][onPlayerAdvancementDone] Evento detectado para el jugador: "
                        + player.getName());
        Bukkit.getLogger()
                .info("[DEBUG][GenerarBayaZidra.java][onPlayerAdvancementDone] Clave del avance: " + advancementKey);

        // Evitar procesamiento múltiple para el mismo avance
        String playerKey = player.getUniqueId().toString() + ":" + advancementKey.toString();
        if (processedPlayers.contains(playerKey)) {
            Bukkit.getLogger().info("[DEBUG][GenerarBayaZidra.java][onPlayerAdvancementDone] Avance ya procesado.");
            return;
        }

        if (advancementKey.toString().endsWith("baya_zidra")) {
            Bukkit.getLogger()
                    .info("[DEBUG][GenerarBayaZidra.java][onPlayerAdvancementDone] Avance baya_zidra detectado.");

            processedPlayers.add(playerKey); // Marcar como procesado

            updateYMLDiscoveries(player);

            // Entregar el trinket al jugador cada vez que se obtenga el logro
            giveTrinket(player);
        } else {
            Bukkit.getLogger().info(
                    "[DEBUG][GenerarBayaZidra.java][onPlayerAdvancementDone] Avance no relevante para Baya Zidra: "
                            + advancementKey);
        }
    }

    private void updateYMLDiscoveries(Player player) {
        if (!trinketsDiscoverConfig.getBoolean("Discoveries.BayaZidra", false)) {
            trinketsDiscoverConfig.set("Discoveries.BayaZidra", true);
            saveTrinketsDiscoverConfig();
            sendDiscoveryMessage(player);
        } else {
            Bukkit.getLogger().info(
                    "[DEBUG][GenerarBayaZidra.java][updateYMLDiscoveries] Baya Zidra ya estaba descubierta en YML.");
        }
    }

    private void sendDiscoveryMessage(Player player) {
        String discoveryMessage = "§a§l" + player.getName() + " descubrió un nuevo trinket: §6Baya Zidra";
        String instructionMessage = "§eTener 10 §dsweet_berries §een el inventario para obtenerlo.";

        // Enviar los mensajes a todos los jugadores
        Bukkit.broadcastMessage(discoveryMessage);
        Bukkit.broadcastMessage(instructionMessage);
    }

    private void giveTrinket(Player player) {
        Bukkit.getLogger()
                .info("[DEBUG][GenerarBayaZidra.java][giveTrinket] Entregando el trinket Baya Zidra al jugador.");

        File trinketsFile = new File(plugin.getDataFolder(), "trinkets.yml");
        if (!trinketsFile.exists()) {
            Bukkit.getLogger()
                    .severe("[ERROR][GenerarBayaZidra.java][giveTrinket] Archivo trinkets.yml no encontrado.");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(trinketsFile);

        String name = translateColorCodes(config.getString("Trinkets.Advancements.BayaZidra.name", "§f§lBaya Zidra"));
        List<String> lore = translateLore(config.getStringList("Trinkets.Advancements.BayaZidra.lore"));
        int customModelData = config.getInt("Trinkets.Advancements.BayaZidra.customModelData", 0);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.setCustomModelData(customModelData);
            item.setItemMeta(meta);
        }

        if (player.getInventory().addItem(item).isEmpty()) {
            Bukkit.getLogger().info("[DEBUG][GenerarBayaZidra.java][giveTrinket] Baya Zidra añadida al inventario de "
                    + player.getName());
        } else {
            Bukkit.getLogger()
                    .warning("[WARNING][GenerarBayaZidra.java][giveTrinket] Inventario lleno para el jugador: "
                            + player.getName());
        }
    }

    private void saveTrinketsDiscoverConfig() {
        try {
            trinketsDiscoverConfig.save(trinketsDiscoverFile);
            Bukkit.getLogger().info(
                    "[DEBUG][GenerarBayaZidra.java][saveTrinketsDiscoverConfig] Archivo trinkets_discover.yml guardado correctamente.");
        } catch (IOException e) {
            Bukkit.getLogger().severe(
                    "[ERROR][GenerarBayaZidra.java][saveTrinketsDiscoverConfig] No se pudo guardar trinkets_discover.yml: "
                            + e.getMessage());
            e.printStackTrace();
        }
    }

    private String translateColorCodes(String input) {
        return input == null ? "" : input.replace("&", "§");
    }

    private List<String> translateLore(List<String> lore) {
        List<String> translatedLore = new ArrayList<>();
        for (String line : lore) {
            translatedLore.add(translateColorCodes(line));
        }
        return translatedLore;
    }
}
