package fr.syuko.asm;

import fr.syuko.asm.Menu.TypeMenu;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EventListener implements Listener {

    private EulerAngle subEulerAngle(Axis axis, EulerAngle angle, int adj) {
        switch(axis){
            case X: angle = angle.setX(angle.getX() - (adj * Math.PI / 180));
                break;
            case Y: angle = angle.setY(angle.getY() - (adj * Math.PI / 180));
                break;
            case Z: angle = angle.setZ(angle.getZ() - (adj * Math.PI / 180));
                break;
            default:
                break;
        }
        return angle;
    }

    private EulerAngle addEulerAngle(Axis axis, EulerAngle angle, int adj) {
        switch(axis){
            case X: angle = angle.setX(angle.getX() + (adj * Math.PI / 180));
                break;
            case Y: angle = angle.setY(angle.getY() + (adj * Math.PI / 180));
                break;
            case Z: angle = angle.setZ(angle.getZ() + (adj * Math.PI / 180));
                break;
            default:
                break;
        }
        return angle;
    }

	/*/////////////
		 EVENTS
	*//////////////

    @EventHandler
    void onMenuListener(InventoryClickEvent event) {
        if(event.getView().getTitle().equals("Armor Stand Editor Interface")) {
            if (event.isLeftClick() || event.isRightClick() || event.isShiftClick())
                event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            PlayerEditor pe = PlayerEditor.getPlayerByUUID(event.getWhoClicked().getUniqueId());
            if (item == null || !item.hasItemMeta() || !Objects.requireNonNull(item.getItemMeta()).hasLore() || Objects.requireNonNull(item.getItemMeta().getLore()).size() < 2)
                return;
            String command = item.getItemMeta().getLore().get(1).replace(ChatColor.GRAY + "", "");
            pe.getPlayer().performCommand(command);
            pe.getPlayer().closeInventory();

            if (event.getRawSlot() != 53) {
                pe.getPlayer().openInventory(new Menu(TypeMenu.GuiMenu, pe).getMenu());
            }
            return;
        }

        if(event.getView().getTitle().equals("Armor Stand Editor Equipment")) {
            if(event.getRawSlot() >= 0 && event.getRawSlot() <= 53 && event.getRawSlot() != 2 && event.getRawSlot() != 11 && event.getRawSlot() != 20 &&
                    event.getRawSlot() != 29 && event.getRawSlot() != 38 && event.getRawSlot() != 47) {
                event.setCancelled(true);
            }

            PlayerEditor pe = PlayerEditor.getPlayerByUUID(event.getWhoClicked().getUniqueId());
            if (pe.getTarget() == null)
                return;

            if (pe.hasPermission("asm.equipment.lock")) {
                if (event.getRawSlot() % 9 >= 6 && event.getRawSlot() < 54) {
                    EquipmentSlot slot;
                    ArmorStand.LockType lockType;
                    String name;
                    switch (event.getRawSlot() % 9) {
                        default:
                        case 6:
                            lockType = ArmorStand.LockType.ADDING;
                            name = "Ajouter ";
                            break;
                        case 7:
                            lockType = ArmorStand.LockType.ADDING_OR_CHANGING;
                            name = "Ajouter ou changer ";
                            break;
                        case 8:
                            lockType = ArmorStand.LockType.REMOVING_OR_CHANGING;
                            name = "Retirer ou changer ";
                            break;
                    }

                    switch (event.getRawSlot() / 9) {
                        default:
                        case 0:
                            slot = EquipmentSlot.HEAD;
                            name += "équipement ";
                            break;
                        case 1:
                            slot = EquipmentSlot.CHEST;
                            name += "équipement ";
                            break;
                        case 2:
                            slot = EquipmentSlot.LEGS;
                            name += "équipement ";
                            break;
                        case 3:
                            slot = EquipmentSlot.FEET;
                            name += "équipement ";
                            break;
                        case 4:
                            slot = EquipmentSlot.HAND;
                            name += "item ";
                            break;
                        case 5:
                            slot = EquipmentSlot.OFF_HAND;
                            name += "item ";
                            break;
                    }

                    Material material;
                    if (pe.getTarget().hasEquipmentLock(slot, lockType)) {
                        material = Material.GREEN_WOOL;
                        name += "(Autorisé)";
                        pe.getTarget().removeEquipmentLock(slot, lockType);
                    } else {
                        material = Material.RED_WOOL;
                        name += "(Interdit)";
                        pe.getTarget().addEquipmentLock(slot, lockType);
                    }

                    event.getView().setItem(event.getRawSlot(), Menu.createIcon(new ItemStack(material), name, ""));

                    return;
                }
            }
        }

        if (event.getView().getTitle().equals("Armor Stand List")) {
            if(event.isLeftClick() || event.isRightClick() || event.isShiftClick())
                event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item == null || item.getItemMeta() == null)
                return;

            ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(UUID.fromString(item.getItemMeta().getDisplayName()));
            if (armorStand != null)
                armorStand.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
        }
    }

    @EventHandler
    void onLeftClickOnArmorStand(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof ArmorStand) {
            PlayerEditor pe = PlayerEditor.getPlayerByUUID(event.getDamager().getUniqueId());
            ArmorStand target = (ArmorStand) event.getEntity();

            if (pe.isArmorStandOwner(target) || pe.hasPermission("asm.admin") || pe.getMode() == PlayerEditor.Mode.Info) {
                if (pe.getPlayer().getInventory().getItemInMainHand().getType() == ArmorStandModifier.getInstance().getTool() || pe.getMode() == PlayerEditor.Mode.Info) {
                    event.setCancelled(true);

                    pe.setTarget(target);
                    switch (pe.getMode()) {
                        case BodyPart:
                            Axis axis = pe.getAxis();
                            switch (pe.getBodyPart()) {
                                case Chest:
                                    if (pe.hasPermission("asm.mode.bodypart.chest")) {
                                        target.setBodyPose(subEulerAngle(axis, target.getBodyPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case Head:
                                    if (pe.hasPermission("asm.mode.bodypart.head")) {
                                        target.setHeadPose(subEulerAngle(axis, target.getHeadPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case LeftArm:
                                    if (pe.hasPermission("asm.mode.bodypart.leftarm")) {
                                        target.setLeftArmPose(subEulerAngle(axis, target.getLeftArmPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case LeftLeg:
                                    if (pe.hasPermission("asm.mode.bodypart.leftleg")) {
                                        target.setLeftLegPose(subEulerAngle(axis, target.getLeftLegPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case RightArm:
                                    if (pe.hasPermission("asm.mode.bodypart.rightarm")) {
                                        target.setRightArmPose(subEulerAngle(axis, target.getRightArmPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case RightLeg:
                                    if (pe.hasPermission("asm.mode.bodypart.rightleg")) {
                                        target.setRightLegPose(subEulerAngle(axis, target.getRightLegPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case Equipment:
                            if (pe.hasPermission("asm.mode.equipment")) {
                                Menu menuInv = new Menu(TypeMenu.EquipmentMenu, pe);
                                pe.getPlayer().openInventory(menuInv.getMenu());
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Gravity:
                            if (pe.hasPermission("asm.mode.gravity")) {
                                target.setGravity(!target.hasGravity());
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Invulnerable:
                            if (pe.hasPermission("asm.mode.invulnerable")) {
                                target.setInvulnerable(!target.isInvulnerable());
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Invisibility:
                            if (pe.hasPermission("asm.mode.invisibility")) {
                                target.setVisible(!target.isVisible());
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Plate:
                            if (pe.hasPermission("asm.mode.plate")) {
                                target.setBasePlate(!target.hasBasePlate());
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Rename:
                            if (pe.hasPermission("asm.mode.rename")) {
                                target.setCustomNameVisible(true);
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.rename"));
                                pe.setRenameMode(true);
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Reset:
                            if (pe.hasPermission("asm.mode.reset")) {
                                target.setHeadPose(new EulerAngle(0, 0, 0));
                                target.setBodyPose(new EulerAngle(0, 0, 0));
                                target.setLeftArmPose(new EulerAngle(0, 0, 0));
                                target.setRightArmPose(new EulerAngle(0, 0, 0));
                                target.setLeftLegPose(new EulerAngle(0, 0, 0));
                                target.setRightLegPose(new EulerAngle(0, 0, 0));
                                target.setRotation(0, 0);
                                target.setSmall(false);
                                target.setVisible(true);
                                target.setCustomNameVisible(false);
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case ShowArm:
                            if (pe.hasPermission("asm.mode.showarm")) {
                                target.setArms(!target.hasArms());
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case ArmorStand:
                            if (pe.hasPermission("asm.mode.movearmorstand")) {
                                double adjustment = (pe.getAdjustment() == 15) ? 0.5 : 0.1;
                                Location loc = target.getLocation();
                                switch (pe.getAxis()) {
                                    case X:
                                        loc.setX(loc.getX() + adjustment);
                                        target.teleport(loc);
                                        break;
                                    case Y:
                                        loc.setY(loc.getY() + adjustment);
                                        target.teleport(loc);
                                        break;
                                    case Z:
                                        loc.setZ(loc.getZ() + adjustment);
                                        target.teleport(loc);
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Size:
                            if (pe.hasPermission("asm.mode.size")) {
                                target.setSmall(!target.isSmall());
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Rotation:
                            if (pe.hasPermission("asm.mode.rotation")) {
                                Location loc1 = target.getLocation();
                                float yaw = loc1.getYaw();
                                loc1.setYaw((yaw + 180 - (float) pe.getAdjustment()) % 360 - 180);
                                target.teleport(loc1);
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case Info:
                            if (pe.hasPermission("asm.mode.info")) {
                                String align = "   ";
                                HashMap<String, String> targetInfo = PlayerEditor.getArmorStandInfo(target);

                                pe.sendMessage(ChatColor.GREEN + "Information sur l'armor stand");
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Nom" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("customName"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "UUID" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("uuid"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Propriétaire" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("owner"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Coordonnées" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("location"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Rotation" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("rotation"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Bras visible" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("showArms"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Plaque visible" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("showPlate"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Taille" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("size"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Nom visible" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("showName"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Invisible" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("invisibility"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Invincible" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("invulnerable"));
                                pe.sendMessageWithoutSuffix(align + ChatColor.YELLOW + "Gravité" + ChatColor.WHITE + ": " + ChatColor.GRAY + targetInfo.get("gravity"));
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case ShowName:
                            if (pe.hasPermission("asm.mode.showname")) {
                                target.setCustomNameVisible(!target.isCustomNameVisible());
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                    }
                }
            } else {
                if (PlayerEditor.getArmorStandOwner(target) == null) {
                    if (pe.getPlayer().getInventory().getItemInMainHand().getType() == ArmorStandModifier.getInstance().getTool()) {
                        event.setCancelled(true);
                        if (pe.tryAddArmorStand(target)) {
                            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.newArmorstand"));
                        }
                    }
                } else {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.notOwn"));
                }
            }
        }
    }

    @EventHandler
    void onRightClickOnArmorStand(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() instanceof ArmorStand) {
            ArmorStand target = (ArmorStand) event.getRightClicked();
            PlayerEditor pe = PlayerEditor.getPlayerByUUID(event.getPlayer().getUniqueId());

            if (pe.isArmorStandOwner(target) || pe.hasPermission("asm.admin") || pe.getMode() == PlayerEditor.Mode.Info) {
                if (pe.getPlayer().getInventory().getItemInMainHand().getType() == ArmorStandModifier.getInstance().getTool() || pe.getMode() == PlayerEditor.Mode.Info) {
                    event.setCancelled(true);

                    pe.setTarget(target);
                    switch (pe.getMode()) {
                        case BodyPart:
                            Axis axis = pe.getAxis();
                            switch (pe.getBodyPart()) {
                                case Chest:
                                    if (pe.hasPermission("asm.mode.bodypart.chest")) {
                                        target.setBodyPose(addEulerAngle(axis, target.getBodyPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case Head:
                                    if (pe.hasPermission("asm.mode.bodypart.head")) {
                                        target.setHeadPose(addEulerAngle(axis, target.getHeadPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case LeftArm:
                                    if (pe.hasPermission("asm.mode.bodypart.leftarm")) {
                                        target.setLeftArmPose(addEulerAngle(axis, target.getLeftArmPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case LeftLeg:
                                    if (pe.hasPermission("asm.mode.bodypart.leftleg")) {
                                        target.setLeftLegPose(addEulerAngle(axis, target.getLeftLegPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case RightArm:
                                    if (pe.hasPermission("asm.mode.bodypart.rightarm")) {
                                        target.setRightArmPose(addEulerAngle(axis, target.getRightArmPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case RightLeg:
                                    if (pe.hasPermission("asm.mode.bodypart.rightleg")) {
                                        target.setRightLegPose(addEulerAngle(axis, target.getRightLegPose(), pe.getAdjustment()));
                                    } else {
                                        pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                                    }
                                    break;
                                case None:
                                default:
                                    break;
                            }
                            break;
                        case Rotation:
                            if (pe.hasPermission("asm.mode.rotation")) {
                                Location loc1 = target.getLocation();
                                float yaw = loc1.getYaw();
                                loc1.setYaw((yaw - 180 + (float) pe.getAdjustment()) % 360 - 180);
                                target.teleport(loc1);
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        case ArmorStand:
                            if (pe.hasPermission("asm.mode.movearmorstand")) {
                                double adjustment = (pe.getAdjustment() == 15) ? 0.5 : 0.1;
                                Location loc = target.getLocation();
                                switch (pe.getAxis()) {
                                    case X:
                                        loc.setX(loc.getX() - adjustment);
                                        target.teleport(loc);
                                        break;
                                    case Y:
                                        loc.setY(loc.getY() - adjustment);
                                        target.teleport(loc);
                                        break;
                                    case Z:
                                        loc.setZ(loc.getZ() - adjustment);
                                        target.teleport(loc);
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.nopermissions"));
                            }
                            break;
                        default:
                            break;

                    }
                }
            } else {
                if (PlayerEditor.getArmorStandOwner(target) == null) {
                    if (pe.getPlayer().getInventory().getItemInMainHand().getType() == ArmorStandModifier.getInstance().getTool()) {
                        event.setCancelled(true);
                        if (pe.tryAddArmorStand(target)) {
                            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.newArmorstand"));
                        }
                    }
                } else {
                    event.setCancelled(true);
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.notOwn"));
                }
            }
        }
    }

    @EventHandler
    void onRightClickWithAdminTool(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == ArmorStandModifier.getInstance().getAdminTool()) {
                PlayerEditor pe = PlayerEditor.getPlayerByUUID(event.getPlayer().getUniqueId());
                if (pe.hasPermission("asm.admin.admintool")) {
                    Location loc = pe.getPlayer().getLocation();
                    pe.sendMessage("Loc : " + loc.getX() + "/" + loc.getY() + "/" + loc.getZ());
                    if (loc.getWorld() != null) {
                        Collection<Entity> nearbyEntities = loc.getWorld().getNearbyEntities(loc, 50, 50, 50);
                        pe.sendMessage(nearbyEntities.size() + "");
                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof ArmorStand) {
                                ((ArmorStand)entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 400, 1));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    void onRenameArmorStand(AsyncPlayerChatEvent event) {
        PlayerEditor pe = PlayerEditor.getPlayerByUUID(event.getPlayer().getUniqueId());
        if(pe.modeRenameIsEnabled()) {
            pe.setRenameMode(false);
            ArmorStand target = pe.getTarget();
            if(target == null)
                return;
            if (pe.hasPermission("asm.mode.rename.colorize"))
                target.setCustomName(ArmorStandModifier.colorizeString(event.getMessage()));
            else
                target.setCustomName(event.getMessage());
            pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.renameComplete"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onArmorStandKilled(EntityDeathEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) event.getEntity();
            PlayerEditor armorStandOwner = PlayerEditor.getArmorStandOwner(armorStand);

            if (armorStandOwner == null)
                return;

            armorStandOwner.removeArmorStand(armorStand);
        }
    }

    @EventHandler
    void onMenuClosed(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Armor Stand Editor Equipment")) {
            PlayerEditor pe = PlayerEditor.getPlayerByUUID(event.getPlayer().getUniqueId());
            ArmorStand target = pe.getTarget();

            if (target == null || target.getEquipment() == null) {
                return;
            }

            target.getEquipment().setHelmet(event.getInventory().getItem(2));
            target.getEquipment().setChestplate(event.getInventory().getItem(11));
            target.getEquipment().setLeggings(event.getInventory().getItem(20));
            target.getEquipment().setBoots(event.getInventory().getItem(29));
            target.getEquipment().setItemInMainHand(event.getInventory().getItem(38));
            target.getEquipment().setItemInOffHand(event.getInventory().getItem(47));
        }
    }
}
