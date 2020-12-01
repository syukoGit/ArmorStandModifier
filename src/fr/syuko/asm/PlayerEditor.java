package fr.syuko.asm;

import org.bukkit.Axis;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

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

    public PlayerEditor(UUID uuid) {
        this.uuid = uuid;

        currentMode = Mode.None;
        currentAxis = Axis.X;
        currentBodyPart = BodyPart.None;

        adjustment = 15;
        renameModeEnabled = false;

        currentTarget = null;
    }

    public void setMode(Mode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            sendMessage(ChatColor.WHITE + "Mode " + mode);
        }
    }

    public Mode getMode() {
        return currentMode;
    }

    public void setAxis(Axis axis) {
        if (currentAxis != axis) {
            currentAxis = axis;
            sendMessage(ChatColor.WHITE + "Axe de travail = " + axis);
        }
    }

    public Axis getAxis() {
        return currentAxis;
    }

    public void setBodyPart(BodyPart bodyPart) {
        if (currentBodyPart != bodyPart) {
            currentBodyPart = bodyPart;
            sendMessage(ChatColor.WHITE + "" + bodyPart + " selectionné");
        }
    }

    public BodyPart getBodyPart() {
        return currentBodyPart;
    }

    public void setAdjustment(int adj) {
        adjustment = adj;
        sendMessage(ChatColor.WHITE + "Ajustement de " + adj + " degrés selectionné");
    }

    public int getAdjustment() {
        return adjustment;
    }

    public Player getPlayer() {
        return ArmorStandModifier.getInstance().getServer().getPlayer(uuid);
    }

    public boolean modeRenameIsEnabled() {
        return renameModeEnabled;
    }

    public void setRenameMode(boolean arg) {
        renameModeEnabled = arg;
    }

    public ArmorStand getTarget() {
        return currentTarget;
    }

    public void setTarget(ArmorStand target) {
        currentTarget = target;
    }

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

    public void removeArmorStand(ArmorStand armorStand) {
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

    public List<UUID> getArmorStands() {
        return armorStands;
    }

    public boolean hasPermission(String perm) {
        return getPlayer().hasPermission(perm);
    }

    public void sendMessage(String message) {
        message = ArmorStandModifier.getInstance().getConfig().getString("message.prefix") + message;
        sendMessageWithoutSuffix(message);
    }

    public void sendMessageWithoutSuffix(String message) {
        if (getPlayer() != null)
            getPlayer().sendMessage(ArmorStandModifier.colorizeString(message));
    }

    public boolean isArmorStandOwner(ArmorStand as) {
        return armorStands.contains(as.getUniqueId());
    }

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

    public static PlayerEditor getArmorStandOwner(ArmorStand target) {
        for (PlayerEditor pe: players.values()) {
            if (pe.armorStands.contains(target.getUniqueId()))
                return pe;
        }

        return null;
    }

    public static PlayerEditor getPlayerByUUID(UUID uuid) {
        return players.containsKey(uuid) ? players.get(uuid) : addPlayer(uuid);
    }

    public static void removeAllPlayer() {
        players.clear();
    }

    private static PlayerEditor addPlayer(UUID uuid) {
        players.put(uuid, new PlayerEditor(uuid));
        return players.get(uuid);
    }
}
