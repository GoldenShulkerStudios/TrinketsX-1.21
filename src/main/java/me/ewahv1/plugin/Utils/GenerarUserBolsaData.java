package me.ewahv1.plugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerarUserBolsaData {

    private static final File DATA_FILE = new File(Bukkit.getPluginManager().getPlugin("TrinketsX").getDataFolder(),
            "BolsaDeTrinkets.yml");

    public static boolean doesPlayerBolsaExist(String uuid) {
        Bukkit.getLogger().info(
                "[DEBUG][GenerarUserBolsaData.java][doesPlayerBolsaExist] Verificando si la bolsa existe para el UUID: "
                        + uuid);

        if (!DATA_FILE.exists()) {
            Bukkit.getLogger().warning(
                    "[DEBUG][GenerarUserBolsaData.java][doesPlayerBolsaExist] El archivo BolsaDeTrinkets.yml no existe.");
            return false;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(DATA_FILE);
        boolean exists = config.isSet("Bolsas." + uuid);
        Bukkit.getLogger().info("[DEBUG][GenerarUserBolsaData.java][doesPlayerBolsaExist] Bolsa "
                + (exists ? "existe" : "no existe") + " para el UUID: " + uuid);
        return exists;
    }

    public static void createPlayerBolsa(String uuid) {
        Bukkit.getLogger().info(
                "[DEBUG][GenerarUserBolsaData.java][createPlayerBolsa] Creando una nueva bolsa para el UUID: " + uuid);

        if (!DATA_FILE.exists()) {
            try {
                DATA_FILE.getParentFile().mkdirs();
                DATA_FILE.createNewFile();
                Bukkit.getLogger().info(
                        "[DEBUG][GenerarUserBolsaData.java][createPlayerBolsa] Archivo BolsaDeTrinkets.yml creado.");
            } catch (IOException e) {
                Bukkit.getLogger().severe(
                        "[ERROR][GenerarUserBolsaData.java][createPlayerBolsa] No se pudo crear el archivo BolsaDeTrinkets.yml");
                e.printStackTrace();
                return;
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(DATA_FILE);
        config.createSection("Bolsas." + uuid + ".trinkets");

        try {
            config.save(DATA_FILE);
            Bukkit.getLogger()
                    .info("[DEBUG][GenerarUserBolsaData.java][createPlayerBolsa] Bolsa creada y guardada para el UUID: "
                            + uuid);
        } catch (IOException e) {
            Bukkit.getLogger().severe(
                    "[ERROR][GenerarUserBolsaData.java][createPlayerBolsa] Error al guardar el archivo BolsaDeTrinkets.yml para "
                            + uuid);
            e.printStackTrace();
        }
    }

    public static void loadPlayerBolsa(String uuid, Inventory inventory) {
        Bukkit.getLogger()
                .info("[DEBUG][GenerarUserBolsaData.java][loadPlayerBolsa] Cargando la bolsa para el UUID: " + uuid);

        if (!DATA_FILE.exists()) {
            Bukkit.getLogger().warning(
                    "[DEBUG][GenerarUserBolsaData.java][loadPlayerBolsa] El archivo BolsaDeTrinkets.yml no existe.");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(DATA_FILE);
        List<Map<String, Object>> trinkets = (List<Map<String, Object>>) config.getList("Bolsas." + uuid + ".trinkets");

        if (trinkets != null) {
            for (Map<String, Object> trinketData : trinkets) {
                int slot = (int) trinketData.get("slot");
                ItemStack item = ItemStack.deserialize((Map<String, Object>) trinketData.get("item"));
                inventory.setItem(slot, item);
                Bukkit.getLogger().info("[DEBUG][GenerarUserBolsaData.java][loadPlayerBolsa] Ítem cargado en el slot: "
                        + slot + " para el UUID: " + uuid);
            }
        } else {
            Bukkit.getLogger()
                    .info("[DEBUG][GenerarUserBolsaData.java][loadPlayerBolsa] No se encontraron ítems para el UUID: "
                            + uuid);
        }
    }

    public static void savePlayerBolsa(String uuid, Inventory inventory) {
        Bukkit.getLogger()
                .info("[DEBUG][GenerarUserBolsaData.java][savePlayerBolsa] Guardando la bolsa para el UUID: " + uuid);

        if (!DATA_FILE.exists()) {
            Bukkit.getLogger().warning(
                    "[DEBUG][GenerarUserBolsaData.java][savePlayerBolsa] El archivo BolsaDeTrinkets.yml no existe.");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(DATA_FILE);
        List<Map<String, Object>> trinkets = new ArrayList<>();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                Map<String, Object> trinketData = new HashMap<>();
                trinketData.put("slot", i);
                trinketData.put("item", item.serialize());
                trinkets.add(trinketData);
                Bukkit.getLogger().info("[DEBUG][GenerarUserBolsaData.java][savePlayerBolsa] Ítem guardado en el slot: "
                        + i + " para el UUID: " + uuid);
            }
        }

        config.set("Bolsas." + uuid + ".trinkets", trinkets);

        try {
            config.save(DATA_FILE);
            Bukkit.getLogger().info(
                    "[DEBUG][GenerarUserBolsaData.java][savePlayerBolsa] Bolsa guardada correctamente para el UUID: "
                            + uuid);
        } catch (IOException e) {
            Bukkit.getLogger().severe(
                    "[ERROR][GenerarUserBolsaData.java][savePlayerBolsa] Error al guardar el archivo BolsaDeTrinkets.yml para "
                            + uuid);
            e.printStackTrace();
        }
    }
}