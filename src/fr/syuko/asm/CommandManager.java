package fr.syuko.asm;

import fr.syuko.asm.Menu.TypeMenu;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class CommandManager implements CommandExecutor {

    /**
     * Constructor
     * @param armorStandModifier ArmorStandModifier instance
     */
    public CommandManager(ArmorStandModifier armorStandModifier) {

        EventListener peManager = new EventListener();
        Bukkit.getPluginManager().registerEvents(peManager, armorStandModifier);
    }

    /**
     * Method called when the plugin received a command send by sender.
     * @param sender the one who sent the command
     * @param command the command sent
     * @param label the command sent with the arguments
     * @param arg a array contained the all arguments
     * @return if the command is treated
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] arg) {
        if (sender instanceof Player) {
            PlayerEditor pe = PlayerEditor.getPlayerByUUID(((Player) sender).getUniqueId());

            if (arg.length == 0) {
                helpCommand(pe, 1);
                return true;
            }

            if (arg[0].equalsIgnoreCase("gui")) {
                if (pe.hasPermission("asm.command.gui")) {
                    Menu menuInv = new Menu(TypeMenu.GuiMenu, pe);
                    pe.getPlayer().openInventory(menuInv.getMenu());
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }

                return true;
            }

            if (arg[0].equalsIgnoreCase("setAxe")) {
                if(arg.length < 2)
                    return true;

                switch(arg[1]) {
                    case "X" :
                    case "x" :
                        if (pe.hasPermission("asm.axis.x")) {
                            pe.setAxis(Axis.X);
                        } else {
                            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                        }
                        break;

                    case "Y" :
                    case "y" :
                        if (pe.hasPermission("asm.axis.y")) {
                            pe.setAxis(Axis.Y);
                        } else {
                            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                        }
                        break;

                    case "Z" :
                    case "z" :
                        if (pe.hasPermission("asm.axis.z")) {
                            pe.setAxis(Axis.Z);
                        } else {
                            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                        }
                        break;

                    default:
                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.misuseCommand"));
                        break;
                }

                return true;
            }

            if (arg[0].equalsIgnoreCase("plugin")) {
                pe.sendMessageWithoutPrefix(ChatColor.RED + ArmorStandModifier.getInstance().getDescription().getName() + ChatColor.WHITE + " :");
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Auteur : " + ChatColor.GRAY + ArmorStandModifier.getInstance().getDescription().getAuthors().get(0));
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Description : " + ChatColor.GRAY + ArmorStandModifier.getInstance().getDescription().getDescription());
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Version : " + ChatColor.GRAY + ArmorStandModifier.getInstance().getDescription().getVersion());
                return true;
            }

            if (arg[0].equalsIgnoreCase("setMode")) {
                if(arg.length < 2)
                    return true;
                pe.changeMode(arg[1]);

                return true;
            }

            if (arg[0].equalsIgnoreCase("setAdj")) {
                if(arg.length < 2)
                    return true;
                switch(arg[1]) {
                    case "coarse" :
                        if (pe.hasPermission("asm.adjustment.coarse")) {
                            pe.setAdjustment(ArmorStandModifier.getInstance().getConfig().getInt("adjustment.coarse"));
                        } else {
                            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                        }
                        break;

                    case "fine" :
                        if (pe.hasPermission("asm.adjustment.fine")) {
                            pe.setAdjustment(ArmorStandModifier.getInstance().getConfig().getInt("adjustment.fine"));
                        } else {
                            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                        }
                        break;

                    default:
                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.misuseCommand"));
                        break;
                }

                return true;
            }

            if (arg[0].equalsIgnoreCase("help")) {
                if (pe.hasPermission("asm.command.help")) {
                    if (arg.length > 1) {
                        try {
                            int page = Integer.parseInt(arg[1]);
                            helpCommand(pe, page);
                        } catch (NumberFormatException e) {
                            pe.sendMessage(ChatColor.RED + "Numero de page invalide");
                        }
                    } else {
                        helpCommand(pe, 1);
                    }
                }
                return true;
            }

            if (arg[0].equalsIgnoreCase("list")) {
                if (arg.length < 2) {
                    if (pe.hasPermission("asm.command.list.me")) {
                        Menu menu = new Menu(TypeMenu.ArmorStandList, pe);
                        Inventory inventory = menu.getMenu();
                        if (inventory != null)
                            pe.getPlayer().openInventory(inventory);
                    } else {
                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                    }
                } else {
                    if (pe.hasPermission("asm.command.list.other")) {
                        Player otherPlayer = Bukkit.getServer().getPlayer(arg[1]);

                        if (otherPlayer == null) {
                            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.noFoundPlayer"));
                        } else {
                            PlayerEditor otherPE = PlayerEditor.getPlayerByUUID(otherPlayer.getUniqueId());
                            Menu menu = new Menu(TypeMenu.ArmorStandList, otherPE);
                            Inventory inventory = menu.getMenu();
                            if (inventory != null)
                                pe.getPlayer().openInventory(inventory);
                        }
                    } else {
                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                    }
                }
                return true;
            }

            if (arg[0].equalsIgnoreCase("reload")) {
                if (pe.hasPermission("asm.admin.reload")) {
                    ArmorStandModifier.getInstance().reloadConfig();
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.reloadComplete"));
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                return true;
            }

            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.unknowCommand"));
            return true;
        }

        return true;
    }

    /**
     * Display the wanted page of the help.
     * @param pe PlayerEditor who ask the help
     * @param page page number
     */
    private void helpCommand(PlayerEditor pe, int page) {
        int nbPageMax = 1;

        pe.sendMessageWithoutPrefix("");
        pe.sendMessageWithoutPrefix("");
        pe.sendMessage(ChatColor.YELLOW + "Help command" + ChatColor.GRAY + " | Page " + page);
        pe.sendMessageWithoutPrefix("");
        switch (page) {
            case 1:
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Ouvrir l'aide" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm help " + ChatColor.GRAY + "[page]");
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Ouvrir Gui" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm gui");
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Changer mode" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm setMode " + ChatColor.DARK_GREEN + "<mode>");
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Changer axe de travail" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm setAxis " + ChatColor.DARK_GREEN + "<axe>");
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Changer ajustement" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm setAdj " + ChatColor.DARK_GREEN + "<ajustement>");
                pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Afficher liste d'armorstand" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm list " + ChatColor.GRAY + "[pseudo]");
                break;
            case 2:
                break;
        }

        pe.sendMessageWithoutPrefix("");
        pe.sendMessageWithoutPrefix(ChatColor.YELLOW + "Page " + page + " sur " + nbPageMax);
    }
}
