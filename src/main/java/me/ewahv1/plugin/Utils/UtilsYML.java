package me.ewahv1.plugin.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UtilsYML {

    private final File file;
    private final FileConfiguration config;

    public UtilsYML(File pluginFolder, String fileName) {
        // Crear la carpeta del plugin si no existe
        if (!pluginFolder.exists()) {
            boolean created = pluginFolder.mkdirs();
            if (created) {
                System.out.println("[DEBUG][UtilsYML.java][Constructor] Directorio del plugin creado: "
                        + pluginFolder.getAbsolutePath());
            } else {
                System.err.println("[ERROR][UtilsYML.java][Constructor] Error al crear el directorio del plugin: "
                        + pluginFolder.getAbsolutePath());
            }
        }

        this.file = new File(pluginFolder, fileName);
        this.config = YamlConfiguration.loadConfiguration(file);

        // Crear el archivo YML si no existe
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("[DEBUG][UtilsYML.java][Constructor] Archivo creado: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println(
                        "[ERROR][UtilsYML.java][Constructor] Error al crear el archivo: " + file.getAbsolutePath());
                e.printStackTrace();
            }
        } else {
            System.out.println("[DEBUG][UtilsYML.java][Constructor] Archivo cargado: " + file.getAbsolutePath());
        }
    }

    /**
     * Guarda los cambios en el archivo YML.
     */
    public void saveConfig() {
        try {
            config.save(file);
            System.out.println("[DEBUG][UtilsYML.java][saveConfig] Configuración guardada correctamente: "
                    + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println(
                    "[ERROR][UtilsYML.java][saveConfig] Error al guardar la configuración: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    /**
     * Verifica si un jugador ya ha desbloqueado un trinket específico.
     *
     * @param playerUUID UUID del jugador.
     * @param trinketKey Clave del trinket.
     * @return true si el jugador ya desbloqueó el trinket, de lo contrario false.
     */
    public boolean hasUnlockedTrinket(UUID playerUUID, String trinketKey) {
        boolean result = config.contains("Players." + playerUUID + "." + trinketKey);
        System.out.println("[DEBUG][UtilsYML.java][hasUnlockedTrinket] Verificación para jugador: " + playerUUID
                + ", Trinket: " + trinketKey + " -> " + result);
        return result;
    }

    /**
     * Marca un trinket como desbloqueado para un jugador.
     *
     * @param playerUUID UUID del jugador.
     * @param trinketKey Clave del trinket.
     */
    public void unlockTrinket(UUID playerUUID, String trinketKey) {
        System.out.println("[DEBUG][UtilsYML.java][unlockTrinket] Desbloqueando trinket para jugador: " + playerUUID
                + ", Trinket: " + trinketKey);
        config.set("Players." + playerUUID + "." + trinketKey, true);
        saveConfig();
    }
}
