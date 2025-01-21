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

public class GenerarMissingNo implements Listener {

    private final JavaPlugin plugin;
    private final File trinketsDiscoverFile;
    private final FileConfiguration trinketsDiscoverConfig;
    private final Set<String> processedPlayers = new HashSet<>();

    public GenerarMissingNo(JavaPlugin plugin) {
        this.plugin = plugin;

        // Inicializar archivo y configuración predeterminada
        this.trinketsDiscoverFile = new File(plugin.getDataFolder(), "trinkets_discover.yml");
        this.trinketsDiscoverConfig = YamlConfiguration.loadConfiguration(trinketsDiscoverFile);

        if (!trinketsDiscoverFile.exists()) {
            try {
                trinketsDiscoverFile.createNewFile();
                Bukkit.getLogger()
                        .info("[DEBUG][GenerarMissingNo.java][Constructor] Archivo trinkets_discover.yml creado.");
            } catch (IOException e) {
                Bukkit.getLogger()
                        .severe("[ERROR][GenerarMissingNo.java][Constructor] No se pudo crear trinkets_discover.yml: "
                                + e.getMessage());
                e.printStackTrace();
            }
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getLogger().info("[DEBUG][GenerarMissingNo.java][Constructor] Listener registrado correctamente.");
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        NamespacedKey advancementKey = event.getAdvancement().getKey();

        Bukkit.getLogger()
                .info("[DEBUG][GenerarMissingNo.java][onPlayerAdvancementDone] Evento detectado para el jugador: "
                        + player.getName());
        Bukkit.getLogger()
                .info("[DEBUG][GenerarMissingNo.java][onPlayerAdvancementDone] Clave del avance: " + advancementKey);

        // Evitar procesamiento múltiple para el mismo avance
        String playerKey = player.getUniqueId().toString() + ":" + advancementKey.toString();
        if (processedPlayers.contains(playerKey)) {
            Bukkit.getLogger().info("[DEBUG][GenerarMissingNo.java][onPlayerAdvancementDone] Avance ya procesado.");
            return;
        }

        if (advancementKey.toString().endsWith("missigno")) {
            Bukkit.getLogger()
                    .info("[DEBUG][GenerarMissingNo.java][onPlayerAdvancementDone] Avance MissingNo detectado.");

            processedPlayers.add(playerKey); // Marcar como procesado

            updateYMLDiscoveries(player);

            // Entregar el trinket al jugador cada vez que se obtenga el logro
            giveTrinket(player);
        } else {
            Bukkit.getLogger().info(
                    "[DEBUG][GenerarMissingNo.java][onPlayerAdvancementDone] Avance no relevante para MissingNo: "
                            + advancementKey);
        }
    }

    private void updateYMLDiscoveries(Player player) {
        if (!trinketsDiscoverConfig.getBoolean("Discoveries.MissingNo", false)) {
            trinketsDiscoverConfig.set("Discoveries.MissingNo", true);
            saveTrinketsDiscoverConfig();
            sendDiscoveryMessage(player);
        } else {
            Bukkit.getLogger().info(
                    "[DEBUG][GenerarMissingNo.java][updateYMLDiscoveries] MissingNo ya estaba descubierto en YML.");
        }
    }

    private void sendDiscoveryMessage(Player player) {
        String discoveryMessage = "§a§l" + player.getName() + " descubrió un nuevo trinket: §7MissingNo";
        String instructionMessage = "§ePosee un efecto único al recibir daño.";

        // Enviar los mensajes a todos los jugadores
        Bukkit.broadcastMessage(discoveryMessage);
        Bukkit.broadcastMessage(instructionMessage);
    }

    private void giveTrinket(Player player) {
        Bukkit.getLogger()
                .info("[DEBUG][GenerarMissingNo.java][giveTrinket] Entregando el trinket MissingNo al jugador.");

        File trinketsFile = new File(plugin.getDataFolder(), "trinkets.yml");
        if (!trinketsFile.exists()) {
            Bukkit.getLogger()
                    .severe("[ERROR][GenerarMissingNo.java][giveTrinket] Archivo trinkets.yml no encontrado.");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(trinketsFile);

        String name = translateColorCodes(config.getString("Trinkets.Advancements.MissigNo.name", "§7§lMissingNo"));
        List<String> lore = translateLore(config.getStringList("Trinkets.Advancements.MissigNo.lore"));
        int customModelData = config.getInt("Trinkets.Advancements.MissigNo.customModelData", 2);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.setCustomModelData(customModelData);
            item.setItemMeta(meta);
        }

        if (player.getInventory().addItem(item).isEmpty()) {
            Bukkit.getLogger().info("[DEBUG][GenerarMissingNo.java][giveTrinket] MissingNo añadido al inventario de "
                    + player.getName());
        } else {
            // Dejar caer el ítem si el inventario está lleno
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            Bukkit.getLogger()
                    .warning("[WARNING][GenerarMissingNo.java][giveTrinket] Inventario lleno, MissingNo dejado en el mundo para "
                            + player.getName());
        }
    }

    private void saveTrinketsDiscoverConfig() {
        try {
            trinketsDiscoverConfig.save(trinketsDiscoverFile);
            Bukkit.getLogger().info(
                    "[DEBUG][GenerarMissingNo.java][saveTrinketsDiscoverConfig] Archivo trinkets_discover.yml guardado correctamente.");
        } catch (IOException e) {
            Bukkit.getLogger().severe(
                    "[ERROR][GenerarMissingNo.java][saveTrinketsDiscoverConfig] No se pudo guardar trinkets_discover.yml: "
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
