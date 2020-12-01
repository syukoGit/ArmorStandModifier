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

public class CommandManager implements CommandExecutor {

    public CommandManager(ArmorStandModifier armorStandModifier) {

        EventListener peManager = new EventListener();
        Bukkit.getPluginManager().registerEvents(peManager, armorStandModifier);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arg) {
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
                pe.sendMessageWithoutSuffix(ChatColor.RED + ArmorStandModifier.getInstance().getDescription().getName() + ChatColor.WHITE + " :");
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Auteur : " + ChatColor.GRAY + ArmorStandModifier.getInstance().getDescription().getAuthors().get(0));
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Description : " + ChatColor.GRAY + ArmorStandModifier.getInstance().getDescription().getDescription());
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Version : " + ChatColor.GRAY + ArmorStandModifier.getInstance().getDescription().getVersion());
                return true;
            }

            if (arg[0].equalsIgnoreCase("setMode")) {
                if(arg.length < 2)
                    return true;
                changeMode(pe, arg[1]);

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

    private void helpCommand(PlayerEditor pe, int page) {
        int nbPageMax = 1;

        pe.sendMessageWithoutSuffix("");
        pe.sendMessageWithoutSuffix("");
        pe.sendMessage(ChatColor.YELLOW + "Help command" + ChatColor.GRAY + " | Page " + page);
        pe.sendMessageWithoutSuffix("");
        switch (page) {
            case 1:
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Ouvrir l'aide" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm help " + ChatColor.GRAY + "[page]");
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Ouvrir Gui" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm gui");
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Changer mode" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm setMode " + ChatColor.DARK_GREEN + "<mode>");
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Changer axe de travail" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm setAxis " + ChatColor.DARK_GREEN + "<axe>");
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Changer ajustement" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm setAdj " + ChatColor.DARK_GREEN + "<ajustement>");
                pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Afficher liste d'armorstand" + ChatColor.WHITE + ": " + ChatColor.GREEN + "/asm list " + ChatColor.GRAY + "[pseudo]");
                break;
            case 2:
                break;
        }

        pe.sendMessageWithoutSuffix("");
        pe.sendMessageWithoutSuffix(ChatColor.YELLOW + "Page " + page + " sur " + nbPageMax);
    }

    private void changeMode(PlayerEditor pe, String mode) {
        switch(mode) {
            case "head" :
                if (pe.hasPermission("asm.mode.bodypart.head")) {
                    pe.setMode(PlayerEditor.Mode.BodyPart);
                    pe.setBodyPart(PlayerEditor.BodyPart.Head);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "rename" :
                if (pe.hasPermission("asm.mode.rename")) {
                    pe.setMode(PlayerEditor.Mode.Rename);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "gravity" :
                if (pe.hasPermission("asm.mode.gravity")) {
                    pe.setMode(PlayerEditor.Mode.Gravity);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "invincibility" :
                if (pe.hasPermission("asm.mode.invulnerable")) {
                    pe.setMode(PlayerEditor.Mode.Invulnerable);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "showArm" :
                if (pe.hasPermission("asm.mode.showarm")) {
                    pe.setMode(PlayerEditor.Mode.ShowArm);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "reset" :
                if (pe.hasPermission("asm.mode.reset")) {
                    pe.setMode(PlayerEditor.Mode.Reset);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "leftArm" :
                if (pe.hasPermission("asm.mode.bodypart.leftarm")) {
                    pe.setMode(PlayerEditor.Mode.BodyPart);
                    pe.setBodyPart(PlayerEditor.BodyPart.LeftArm);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "chest" :
                if (pe.hasPermission("asm.mode.bodypart.chest")) {
                    pe.setMode(PlayerEditor.Mode.BodyPart);
                    pe.setBodyPart(PlayerEditor.BodyPart.Chest);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "rightArm" :
                if (pe.hasPermission("asm.mode.bodypart.rightarm")) {
                    pe.setMode(PlayerEditor.Mode.BodyPart);
                    pe.setBodyPart(PlayerEditor.BodyPart.RightArm);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "equipment" :
                if (pe.hasPermission("asm.mode.equipment")) {
                    pe.setMode(PlayerEditor.Mode.Equipment);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "invisibility" :
                if (pe.hasPermission("asm.mode.invisibility")) {
                    pe.setMode(PlayerEditor.Mode.Invisibility);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "leftLeg" :
                if (pe.hasPermission("asm.mode.bodypart.leftleg")) {
                    pe.setMode(PlayerEditor.Mode.BodyPart);
                    pe.setBodyPart(PlayerEditor.BodyPart.LeftLeg);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "plate" :
                if (pe.hasPermission("asm.mode.plate")) {
                    pe.setMode(PlayerEditor.Mode.Plate);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "rightLeg" :
                if (pe.hasPermission("asm.mode.bodypart.rightleg")) {
                    pe.setMode(PlayerEditor.Mode.BodyPart);
                    pe.setBodyPart(PlayerEditor.BodyPart.RightLeg);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "size" :
                if (pe.hasPermission("asm.mode.size")) {
                    pe.setMode(PlayerEditor.Mode.Size);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "armorstand" :
                if (pe.hasPermission("asm.mode.movearmorstand")) {
                    pe.setMode(PlayerEditor.Mode.ArmorStand);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "rotation" :
                if (pe.hasPermission("asm.mode.rotation")) {
                    pe.setMode(PlayerEditor.Mode.Rotation);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "info" :
                if (pe.hasPermission("asm.mode.info")) {
                    pe.setMode(PlayerEditor.Mode.Info);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "showName":
                if (pe.hasPermission("asm.mode.showname")) {
                    pe.setMode(PlayerEditor.Mode.ShowName);
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            default:
                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.misuseCommand"));
                break;
        }
    }
}
