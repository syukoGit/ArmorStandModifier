package fr.syuko.asm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Main class extends JavaPlugin
 */
public class ArmorStandModifier extends JavaPlugin {

    private static ArmorStandModifier instance;
    private Material tool;
    private Material adminTool;

    /**
     * Method called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        instance = this;

        CommandManager command = new CommandManager(this);
        Objects.requireNonNull(getCommand("asm")).setExecutor(command);

        consoleMessage(getConfig().getString("message.startMessage"));
    }

    /**
     * Method called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        consoleMessage(getConfig().getString("message.endMessage"));
    }

    /**
     * Method for reload the plugin's config.
     * If the config don't exist then we save the default config.
     */
    @Override
    public void reloadConfig() {
        boolean configExist = true;
        if (!new File("plugins/ArmorStandModifier/config.yml").exists()) {
            saveDefaultConfig();
            configExist = false;
        }

        super.reloadConfig();

        if (configExist) {
            consoleMessage("Fichier config existant");
        } else {
            consoleMessage("Fichier config inexistant");
        }

        tool = Material.getMaterial(Objects.requireNonNull(getConfig().getString("tool")));
        assert tool != null;
        consoleMessage(ChatColor.WHITE + "Tool item : " + ChatColor.ITALIC + ChatColor.AQUA + tool.toString());

        adminTool = Material.getMaterial(Objects.requireNonNull(getConfig().getString("adminTool")));
        assert adminTool != null;
        consoleMessage(ChatColor.WHITE + "Admin tool item : " + ChatColor.ITALIC + ChatColor.AQUA + adminTool.toString());

        getPlayerEditorList();

        consoleMessage(getConfig().getString("message.reloadComplete"));
    }

    /**
     * Initialize the PlayerEditor list.
     *
     * In the plugin directory ArmorStandModifier/PlayerEditor/, each file is a armorstand list of a PlayerEditor.
     *
     * The file name is the Player's UUID.
     * The file content is the list of armorstands belonging the player.
     */
    private void getPlayerEditorList() {
        File directory = new File("plugins/ArmorStandModifier/PlayerEditor/");
        if (!directory.exists() && !directory.mkdir())
            return;

        File[] fileList = directory.listFiles();

        if (fileList == null || fileList.length == 0)
            return;

        PlayerEditor.removeAllPlayer();

        consoleMessage(ChatColor.BLUE + "Ajout des PlayerEditors :");

        for (File file : fileList) {
            try {
                PlayerEditor pe = PlayerEditor.getPlayerByUUID(UUID.fromString(file.getName()));

                consoleMessage(ChatColor.YELLOW + "  " +file.getName());

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    readLine = readLine.replace("\n", "");
                    UUID uuid;

                    try {
                        uuid = UUID.fromString(readLine);
                    } catch (Exception e) {
                        continue;
                    }

                    if (pe.tryAddArmorStand(uuid))
                        consoleMessage("    "  + readLine);
                }

                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the material of the tool for customize armorstands.
     * @return tool's material
     */
    public Material getTool() {
        return tool;
    }

    /**
     * Get the material of the admin tool.
     * @return admin tool's material
     */
    public Material getAdminTool() {
        return adminTool;
    }

    /**
     * Send a message to the server console.
     * The message will be prefixed by the string set in config file with "message.consolesuffix"
     * @param message message to sent
     */
    public static void consoleMessage(String message) {
        String str = getInstance().getConfig().getString("message.consolesuffix") + ChatColor.RESET + message;
        Bukkit.getConsoleSender().sendMessage(colorizeString(str));
    }

    /**
     * Colorize text for message or armorstand's name.
     * Replace the colors' characters to the Minecraft Colors.
     * @param str text to colorize
     * @return colored text
     */
    public static String colorizeString(String str) {
        return str.replaceAll("§0|&0", ChatColor.BLACK + "")
                .replaceAll("§1|&1", ChatColor.DARK_BLUE + "")
                .replaceAll("§2|&2", ChatColor.DARK_GREEN + "")
                .replaceAll("§3|&3", ChatColor.DARK_AQUA + "")
                .replaceAll("§4|&4", ChatColor.DARK_RED + "")
                .replaceAll("§5|&5", ChatColor.DARK_PURPLE + "")
                .replaceAll("§6|&6", ChatColor.GOLD + "")
                .replaceAll("§7|&7", ChatColor.GRAY + "")
                .replaceAll("§8|&8", ChatColor.DARK_GRAY + "")
                .replaceAll("§9|&9", ChatColor.BLUE + "")
                .replaceAll("§a|&a", ChatColor.GREEN + "")
                .replaceAll("§b|&b", ChatColor.AQUA + "")
                .replaceAll("§c|&c", ChatColor.RED + "")
                .replaceAll("§d|&d", ChatColor.LIGHT_PURPLE + "")
                .replaceAll("§e|&e", ChatColor.YELLOW + "")
                .replaceAll("§f|&f", ChatColor.WHITE + "")
                .replaceAll("§k|&k", ChatColor.MAGIC + "")
                .replaceAll("§l|&l", ChatColor.BOLD + "")
                .replaceAll("§m|&m", ChatColor.STRIKETHROUGH + "")
                .replaceAll("§n|&n", ChatColor.UNDERLINE + "")
                .replaceAll("§o|&o", ChatColor.ITALIC + "")
                .replaceAll("§r|&r", ChatColor.RESET + "");
    }

    /**
     * Static methode for get the instance of the JavaPlugin.
     * @return the ArmorStandModifier instance
     */
    public static ArmorStandModifier getInstance() {
        return instance;
    }
}