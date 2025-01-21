package me.ewahv1.plugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.ewahv1.plugin.Utils.GenerarUserBolsaData;

public class BolsaTrinketsCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                Bukkit.getLogger().info("[DEBUG][BolsaTrinketsCommand.java][onCommand] Ejecutando comando.");

                if (!(sender instanceof Player)) {
                        Bukkit.getLogger().warning(
                                        "[DEBUG][BolsaTrinketsCommand.java][onCommand] Comando ejecutado por un no jugador: "
                                                        + sender.getName());
                        sender.sendMessage(ChatColor.RED + "Este comando solo puede ser utilizado por jugadores.");
                        return true;
                }

                Player player = (Player) sender;
                String playerUUID = player.getUniqueId().toString();

                Bukkit.getLogger()
                                .info("[DEBUG][BolsaTrinketsCommand.java][onCommand] Comando ejecutado por el jugador: "
                                                + player.getName());

                if (!GenerarUserBolsaData.doesPlayerBolsaExist(playerUUID)) {
                        Bukkit.getLogger().info(
                                        "[DEBUG][BolsaTrinketsCommand.java][onCommand] Bolsa no encontrada para el jugador: "
                                                        + player.getName()
                                                        + ". Creando nueva bolsa.");
                        GenerarUserBolsaData.createPlayerBolsa(playerUUID);
                        player.sendMessage(ChatColor.GREEN + "Se ha generado tu Bolsa de Trinkets.");
                } else {
                        Bukkit.getLogger().info(
                                        "[DEBUG][BolsaTrinketsCommand.java][onCommand] Bolsa existente encontrada para el jugador: "
                                                        + player.getName());
                }

                Inventory bolsa = Bukkit.createInventory(player, 9, ChatColor.GOLD + "Bolsa de Trinkets");

                Bukkit.getLogger().info(
                                "[DEBUG][BolsaTrinketsCommand.java][onCommand] Cargando datos de la bolsa para el jugador: "
                                                + player.getName());
                GenerarUserBolsaData.loadPlayerBolsa(playerUUID, bolsa);

                Bukkit.getLogger()
                                .info("[DEBUG][BolsaTrinketsCommand.java][onCommand] Abriendo inventario de Bolsa de Trinkets para el jugador: "
                                                + player.getName());
                player.openInventory(bolsa);

                return true;
        }
}
