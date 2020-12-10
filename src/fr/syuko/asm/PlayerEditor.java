package fr.syuko.asm;

import org.bukkit.Axis;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerEditor {
    public enum Mode {
        Rename,
        Gravity,
        Invulnerable,
        Equipment,
        Invisibility,
        ShowArm,
        Plate,
        Reset,
        BodyPart,
        Size,
        ArmorStand,
        Rotation,
        Info,
        ShowName,
        None
    }
    public enum BodyPart {
        LeftArm,
        RightArm,
        LeftLeg,
        RightLeg,
        Head,
        Chest,
        None
    }

    private Mode currentMode;
    private Axis currentAxis;
    private BodyPart currentBodyPart;

    private int adjustment;
    private boolean renameModeEnabled;

    private final List<UUID> armorStands = new ArrayList<>();

    private ArmorStand currentTarget;

    private final UUID uuid;

    private static final HashMap<UUID, PlayerEditor> players = new HashMap<>();

    /**
     * Constructor
     * @param uuid Player's UUID
     */
    public PlayerEditor(UUID uuid) {
        this.uuid = uuid;

        currentMode = Mode.None;
        currentAxis = Axis.X;
        currentBodyPart = BodyPart.None;

        adjustment = 15;
        renameModeEnabled = false;

        currentTarget = null;
    }

    /**
     * Set the current mode
     * @param mode mode to be set
     */
    public void setMode(Mode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            sendMessage(ChatColor.WHITE + "Mode " + mode);
        }
    }

    /**
     * Get the current mode
     * @return current mode
     */
    public Mode getMode() {
        return currentMode;
    }

    /**
     * Change the PlayerEditor's mode
     * @param mode mode to be set
     */
    public void changeMode(String mode) throws IllegalArgumentException {
        switch(mode) {
            case "head" :
                if (hasPermission("asm.mode.bodypart.head")) {
                    setMode(PlayerEditor.Mode.BodyPart);
                    setBodyPart(PlayerEditor.BodyPart.Head);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "rename" :
                if (hasPermission("asm.mode.rename")) {
                    setMode(PlayerEditor.Mode.Rename);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "gravity" :
                if (hasPermission("asm.mode.gravity")) {
                    setMode(PlayerEditor.Mode.Gravity);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "invincibility" :
                if (hasPermission("asm.mode.invulnerable")) {
                    setMode(PlayerEditor.Mode.Invulnerable);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "showArm" :
                if (hasPermission("asm.mode.showarm")) {
                    setMode(PlayerEditor.Mode.ShowArm);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "reset" :
                if (hasPermission("asm.mode.reset")) {
                    setMode(PlayerEditor.Mode.Reset);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "leftArm" :
                if (hasPermission("asm.mode.bodypart.leftarm")) {
                    setMode(PlayerEditor.Mode.BodyPart);
                    setBodyPart(PlayerEditor.BodyPart.LeftArm);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "chest" :
                if (hasPermission("asm.mode.bodypart.chest")) {
                    setMode(PlayerEditor.Mode.BodyPart);
                    setBodyPart(PlayerEditor.BodyPart.Chest);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "rightArm" :
                if (hasPermission("asm.mode.bodypart.rightarm")) {
                    setMode(PlayerEditor.Mode.BodyPart);
                    setBodyPart(PlayerEditor.BodyPart.RightArm);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "equipment" :
                if (hasPermission("asm.mode.equipment")) {
                    setMode(PlayerEditor.Mode.Equipment);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "invisibility" :
                if (hasPermission("asm.mode.invisibility")) {
                    setMode(PlayerEditor.Mode.Invisibility);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "leftLeg" :
                if (hasPermission("asm.mode.bodypart.leftleg")) {
                    setMode(PlayerEditor.Mode.BodyPart);
                    setBodyPart(PlayerEditor.BodyPart.LeftLeg);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "plate" :
                if (hasPermission("asm.mode.plate")) {
                    setMode(PlayerEditor.Mode.Plate);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "rightLeg" :
                if (hasPermission("asm.mode.bodypart.rightleg")) {
                    setMode(PlayerEditor.Mode.BodyPart);
                    setBodyPart(PlayerEditor.BodyPart.RightLeg);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "size" :
                if (hasPermission("asm.mode.size")) {
                    setMode(PlayerEditor.Mode.Size);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "armorstand" :
                if (hasPermission("asm.mode.movearmorstand")) {
                    setMode(PlayerEditor.Mode.ArmorStand);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "rotation" :
                if (hasPermission("asm.mode.rotation")) {
                    setMode(PlayerEditor.Mode.Rotation);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "info" :
                if (hasPermission("asm.mode.info")) {
                    setMode(PlayerEditor.Mode.Info);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            case "showName":
                if (hasPermission("asm.mode.showname")) {
                    setMode(PlayerEditor.Mode.ShowName);
                } else {
                    sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                }
                break;

            default:
                throw new IllegalArgumentException("The mode " + mode + " is invalid");
        }
    }

    /**
     * Set the work axis
     * @param axis work axis to be set
     */
    public void setAxis(Axis axis) {
        if (currentAxis != axis) {
            currentAxis = axis;
            sendMessage(ChatColor.WHITE + "Axe de travail = " + axis);
        }
    }

    /**
     * Get the work axis
     * @return work axis
     */
    public Axis getAxis() {
        return currentAxis;
    }

    /**
     * Set the body part piloted
     * @param bodyPart body part to be set
     */
    public void setBodyPart(BodyPart bodyPart) {
        if (currentBodyPart != bodyPart) {
            currentBodyPart = bodyPart;
            sendMessage(ChatColor.WHITE + "" + bodyPart + " selectionné");
        }
    }

    /**
     * Get the body part piloted
     * @return body part piloted
     */
    public BodyPart getBodyPart() {
        return currentBodyPart;
    }

    /**
     * Set the adjustment
     * @param adj adjustment to be set
     */
    public void setAdjustment(int adj) {
        adjustment = adj;
        sendMessage(ChatColor.WHITE + "Ajustement de " + adj + " degrés selectionné");
    }

    /**
     * Get the adjustment
     * @return current adjustment
     */
    public int getAdjustment() {
        return adjustment;
    }

    /**
     * Get the player linked to this PlayerEditor with the UUID
     * @return player linked
     */
    public Player getPlayer() {
        return ArmorStandModifier.getInstance().getServer().getPlayer(uuid);
    }

    /**
     * Get if the player is going to rename an armorstand
     * @return if the rename mode is enabled
     */
    public boolean modeRenameIsEnabled() {
        return renameModeEnabled;
    }

    /**
     * Set the rename mode so that the player can rename an armorstand
     * @param arg if the player can rename an armorstand
     */
    public void setRenameMode(boolean arg) {
        renameModeEnabled = arg;
    }

    /**
     * Get the current target (armorstand)
     * @return current target
     */
    public ArmorStand getTarget() {
        return currentTarget;
    }

    /**
     * Set the current target (armorstand)
     * @param target armorstand to be set
     */
    public void setTarget(ArmorStand target) {
        currentTarget = target;
    }

    /**
     * Try adding an armorstand to the armorstand list of this PlayerEditor
     * @param armorStand armorstand at add
     * @return if the armorstand is added
     */
    public boolean tryAddArmorStand(ArmorStand armorStand) {
        if (armorStands.size() >= ArmorStandModifier.getInstance().getConfig().getInt("nbMaxArmorstandPerPlayer")) {
            sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.tooMuchArmorstand") + ArmorStandModifier.getInstance().getConfig().getInt("nbMaxArmorstandPerPlayer") + "!");
            return false;
        } else if (armorStands.contains(armorStand.getUniqueId())) {
            return false;
        } else {
            armorStands.add(armorStand.getUniqueId());
            try {
                Path path = Paths.get("plugins/ArmorStandModifier/PlayerEditor/" + getPlayer().getUniqueId().toString());
                if (!Files.exists(path))
                    Files.createFile(path);
                String str = armorStand.getUniqueId().toString() + "\n";
                byte[] bs = str.getBytes();
                Files.write(path, bs, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * Try adding an armorstand with its UUID to the armorstand list of this PlayerEditor
     * @param uuid armorstand's UUID at add
     * @return if the armorstand is added
     */
    public boolean tryAddArmorStand(UUID uuid) {
        if (armorStands.size() >= ArmorStandModifier.getInstance().getConfig().getInt("nbMaxArmorstandPerPlayer"))
            return false;

        if (armorStands.contains(uuid)) {
            ArmorStandModifier.consoleMessage("Armor stand déjà présent");
            return false;
        }

        armorStands.add(uuid);
        return true;
    }

    /**
     * Remove an armorstand to the armorstand list of this PlayerEditor
     * @param armorStand armorstand at remove
     */
    public void removeArmorStand(@NotNull ArmorStand armorStand) {
        armorStands.remove(armorStand.getUniqueId());
        try {
            Path path = Paths.get("plugins/ArmorStandModifier/PlayerEditor/" + uuid.toString());
            if (!Files.exists(path))
                return;

            StringBuilder str = new StringBuilder();
            for (UUID uuid : armorStands) {
                if (!str.toString().equals(""))
                    str.append("\n");
                str.append(uuid.toString()).append("\n");
            }

            Files.write(path, str.toString().getBytes());

            sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.armorstandBroken"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the armorstand list of this PlayerEditor
     * @return armorstand list
     */
    public List<UUID> getArmorStands() {
        return armorStands;
    }

    /**
     * Get if the PlayerEditor has an permission
     * @param perm permission at try
     * @return if the PlayerEditor has the permission
     */
    public boolean hasPermission(String perm) {
        return getPlayer().hasPermission(perm);
    }

    /**
     * Send a message to the player.
     * The message will be prefixed by the string set in config file with "message.suffix"
     * @param message message to sent
     */
    public void sendMessage(String message) {
        message = ArmorStandModifier.getInstance().getConfig().getString("message.suffix") + message;
        sendMessageWithoutPrefix(message);
    }

    /**
     * Send message to the player
     * @param message message to sent
     */
    public void sendMessageWithoutPrefix(String message) {
        if (getPlayer() != null)
            getPlayer().sendMessage(ArmorStandModifier.colorizeString(message));
    }

    /**
     * Check if the PlayerEditor is the owner of the armorstand
     * @param as armorstand at trying
     * @return if the PlayerEditor is the owner of the armorstand
     */
    public boolean isArmorStandOwner(ArmorStand as) {
        return armorStands.contains(as.getUniqueId());
    }

    /*
     *  Static
     */

    /**
     * Get the all information on the armorstand useful for the plugin.
     * @param as armorstand on whom we want information
     * @return HashMap, where key is a string object and data is a string object, contains the all information
     */
    public static HashMap<String, String> getArmorStandInfo(ArmorStand as) {
        PlayerEditor owner = PlayerEditor.getArmorStandOwner(as);
        DecimalFormat f = new DecimalFormat();
        f.setMaximumFractionDigits(2);
        float yaw = as.getLocation().getYaw();

        String ownerStr;
        if (owner == null) {
            ownerStr = "aucun";
        } else {
            if (owner.getPlayer() == null) {
                ownerStr = owner.uuid.toString();
            } else {
                ownerStr = owner.getPlayer().getDisplayName();
            }
        }

        HashMap<String, String> output = new HashMap<>();
        output.put("customName", as.getCustomName());
        output.put("uuid", as.getUniqueId().toString());
        output.put("owner", ownerStr);
        output.put("location", "X:" + f.format(as.getLocation().getX()) + "/Y:" + f.format(as.getLocation().getY()) + "/Z:" + f.format(as.getLocation().getZ()));
        output.put("rotation", f.format((yaw + 180) % 360 - 180) + "°");
        output.put("showArms", as.hasArms() ? "oui" : "non");
        output.put("showPlate", as.hasBasePlate() ? "oui" : "non");
        output.put("size", as.isSmall() ? "petit" : "grand");
        output.put("showName", as.isCustomNameVisible() ? "oui" : "non");
        output.put("invisibility", as.isVisible() ? "oui" : "non");
        output.put("invulnerable", as.isInvulnerable() ? "oui" : "non");
        output.put("gravity", as.hasGravity() ? "oui" : "non");
        return output;
    }

    /**
     * Get the owner of the armorstand
     * @param target armorstand at who we want found the owner
     * @return owner if he has found else null
     */
    public static PlayerEditor getArmorStandOwner(ArmorStand target) {
        for (PlayerEditor pe: players.values()) {
            if (pe.armorStands.contains(target.getUniqueId()))
                return pe;
        }

        return null;
    }

    /**
     * Get the PlayerEditor thanks a UUID.
     * If the PlayerEditor doesn't exists, he is created.
     * @param uuid uuid of the player at found
     * @return PlayerEditor linked at player thanks a UUID
     */
    public static PlayerEditor getPlayerByUUID(UUID uuid) {
        return players.containsKey(uuid) ? players.get(uuid) : addPlayer(uuid);
    }

    /**
     * Clear the PlayerEditor list.
     */
    public static void removeAllPlayer() {
        players.clear();
    }

    /**
     * Add a new PlayerEditor at the PlayerEditor list thanks a UUID
     * @param uuid UUID of the player for create a new PlayerEditor
     * @return new PlayerEditor created
     */
    private static PlayerEditor addPlayer(UUID uuid) {
        players.put(uuid, new PlayerEditor(uuid));
        return players.get(uuid);
    }
}
