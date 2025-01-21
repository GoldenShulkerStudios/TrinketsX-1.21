package me.ewahv1.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("==============================");
        getLogger().info("[DEBUG][Main.java] TrinketsX Iniciado");
        getLogger().info("[DEBUG][Main.java] Plugin desarrollado por ewahv1");
        getLogger().info("==============================");

        // Copiar archivos YML predeterminados
        getLogger().info("[DEBUG][Main.java][onEnable] Copiando archivos YML predeterminados...");
        new me.ewahv1.plugin.Utils.CopyYMLs(this).copyDefaultYMLs();

        // Registrar comandos
        getLogger().info("[DEBUG][Main.java][onEnable] Registrando comandos...");
        getCommand("bolsa").setExecutor(new me.ewahv1.plugin.Commands.BolsaTrinketsCommand());

        // Registrar listeners
        getLogger().info("[DEBUG][Main.java][onEnable] Registrando listeners...");
        getServer().getPluginManager().registerEvents(new me.ewahv1.plugin.Utils.BolsaListener(), this);
        getServer().getPluginManager().registerEvents(new me.ewahv1.plugin.Utils.GenerarBayaZidra(this), this);
        getServer().getPluginManager()
                .registerEvents(new me.ewahv1.plugin.Listeners.Advancements.BayaZidra(this), this);
        getServer().getPluginManager().registerEvents(new me.ewahv1.plugin.Utils.GenerarMissingNo(this), this);
        getServer().getPluginManager()
                .registerEvents(new me.ewahv1.plugin.Listeners.Advancements.MissingNo(this), this);

        getLogger().info("[DEBUG][Main.java][onEnable] Todos los listeners y comandos registrados correctamente.");
    }

    @Override
    public void onDisable() {
        getLogger().info("==============================");
        getLogger().info("[DEBUG][Main.java] TrinketsX Deshabilitado");
        getLogger().info("==============================");
    }
}
