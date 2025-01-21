package me.ewahv1.plugin.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CopyYMLs {

    private final JavaPlugin plugin;

    public CopyYMLs(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Copia un archivo YML del directorio de recursos del plugin al directorio de
     * datos del plugin.
     * Si el archivo ya existe, no se sobrescribe.
     *
     * @param fileName Nombre del archivo a copiar.
     */
    public void copyFile(String fileName) {
        try {
            File pluginFolder = plugin.getDataFolder();
            if (!pluginFolder.exists()) {
                if (pluginFolder.mkdirs()) {
                    plugin.getLogger().info("[DEBUG][CopyYMLs.java][copyFile] Carpeta del plugin creada: "
                            + pluginFolder.getAbsolutePath());
                } else {
                    plugin.getLogger()
                            .severe("[ERROR][CopyYMLs.java][copyFile] No se pudo crear la carpeta del plugin.");
                    return;
                }
            }

            File targetFile = new File(pluginFolder, fileName);
            if (!targetFile.exists()) {
                InputStream inputStream = plugin.getResource(fileName);
                if (inputStream != null) {
                    Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().info("[DEBUG][CopyYMLs.java][copyFile] Archivo " + fileName
                            + " copiado correctamente al directorio del plugin.");
                } else {
                    plugin.getLogger().severe("[ERROR][CopyYMLs.java][copyFile] No se encontró el archivo " + fileName
                            + " en los recursos del plugin.");
                }
            } else {
                plugin.getLogger().info("[DEBUG][CopyYMLs.java][copyFile] Archivo " + fileName
                        + " ya existe, no se realizó ninguna copia.");
            }
        } catch (Exception e) {
            plugin.getLogger().severe(
                    "[ERROR][CopyYMLs.java][copyFile] Error al copiar el archivo " + fileName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Copia todos los archivos YML predeterminados necesarios para el plugin.
     */
    public void copyDefaultYMLs() {
        plugin.getLogger()
                .info("[DEBUG][CopyYMLs.java][copyDefaultYMLs] Iniciando copia de archivos YML predeterminados.");
        copyFile("config.yml");
        copyFile("trinkets.yml");
        copyFile("trinkets_discover.yml");
        plugin.getLogger()
                .info("[DEBUG][CopyYMLs.java][copyDefaultYMLs] Finalizó la copia de archivos YML predeterminados.");
    }
}
