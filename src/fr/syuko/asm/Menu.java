package fr.syuko.asm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Menu {

    public enum TypeMenu {
        GuiMenu,
        EquipmentMenu,
        ArmorStandList
    }

    private static String asmGuiTitle = "Armor Stand Modifier GUI";
    private static String asmEquipmentTitle = "Armor Stand Modifier Equipment";
    private static String asmListTitle = "Armor Stand List";

    private Inventory menuInv;
    private final TypeMenu type;

    private final PlayerEditor pe;

    /**
     * Constructor
     * @param type type of menu
     * @param pe PlayerEditor who must open the inventory
     */
    public Menu(TypeMenu type, PlayerEditor pe) {
        this.type = type;
        this.pe = pe;
    }

    /**
     * Get the inventory to be displayed
     * @return inventory to be displayed
     */
    public Inventory getMenu() {
        switch (type) {
            default:
            case GuiMenu:
                menuInv = Bukkit.createInventory(null, 54, Menu.asmGuiTitle);
                menuInv.setContents(createGuiMenu());
                break;

            case EquipmentMenu :
                if(this.pe.getTarget() == null || this.pe.getTarget().getEquipment() == null)
                    break;

                menuInv = Bukkit.createInventory(null, 54, Menu.asmEquipmentTitle);
                menuInv.setContents(createEquipmentMenu());

                this.pe.getTarget().getEquipment().clear();

                break;

            case ArmorStandList:
                if (this.pe.getArmorStands().size() == 0) {
                    pe.sendMessage(ArmorStandModifier.getInstance().getConfig().getString("message.noArmorStandToDisplay"));
                    menuInv = null;
                    break;
                }

                menuInv = Bukkit.createInventory(null, 54, Menu.asmListTitle);
                menuInv.setContents(createArmorStandListMenu());

                break;
        }

        return menuInv;
    }

    /**
     * Create a custom item for the inventory. The item can have a lore for execute specific command.
     * The attributes, the potion effects and the enchants are hidden.
     * @param icon item at custom then to be displayed
     * @param displayName display name at set to item
     * @param command command displayed in lore if the item should be execute
     * @return custom item to be displayed
     */
    public static ItemStack createIcon(ItemStack icon, String displayName, String command){
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET + displayName);
        if(!command.equals("")) {
            ArrayList<String> loreList = new ArrayList<>();
            loreList.add("");
            loreList.add(ChatColor.GRAY + "asm " + command);
            meta.setLore(loreList);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        icon.setItemMeta(meta);
        return icon;
    }

    /**
     * Create the content for the main inventory
     * @return array contents all items for the inventory
     */
    private ItemStack[] createGuiMenu() {
        ItemStack redGlass = createIcon(new ItemStack(Material.RED_STAINED_GLASS_PANE), "", "");

        ItemStack axisX = redGlass, axisY = redGlass, axisZ = redGlass, nameTag = redGlass, gravity = redGlass, invulnerable = redGlass, head = redGlass,
                armorStand = redGlass, rotation = redGlass, reset = redGlass, invisibility = redGlass, showName = redGlass,
                leftArm = redGlass, chest = redGlass, rightArm = redGlass, showArm = redGlass, size = redGlass, equipment = redGlass,
                leftLeg = redGlass, plate = redGlass, rightLeg = redGlass, coarseAdj = redGlass, fineAdj = redGlass, help = redGlass, info = redGlass;

        if(pe.hasPermission("asm.axis.x"))
            axisX = createIcon(new ItemStack(Material.RED_WOOL), "Axe X", "setAxe X");
        if(pe.hasPermission("asm.axis.y"))
            axisY = createIcon(new ItemStack(Material.GREEN_WOOL), "Axe Y", "setAxe Y");
        if(pe.hasPermission("asm.axis.z"))
            axisZ = createIcon(new ItemStack(Material.BLUE_WOOL), "Axe Z", "setAxe Z");
        if(pe.hasPermission("asm.mode.rename"))
            nameTag = createIcon(new ItemStack(Material.NAME_TAG), "Renomer l'armor stand", "setMode rename");
        if(pe.hasPermission("asm.mode.bodypart.head"))
            head = createIcon(new ItemStack(Material.PLAYER_HEAD), "Piloter la tête", "setMode head");
        if(pe.hasPermission("asm.mode.gravity"))
            gravity = createIcon(new ItemStack(Material.FEATHER), "Activer/Desactiver la gravité", "setMode gravity");
        if(pe.hasPermission("asm.mode.invulnerable"))
            invulnerable = createIcon(new ItemStack(Material.GOLDEN_APPLE), "Activer/Desactiver l'invincibilité", "setMode invincibility");
        if(pe.hasPermission("asm.mode.reset"))
            reset = createIcon(new ItemStack(Material.TRIPWIRE_HOOK), "Reset", "setMode reset");
        if(pe.hasPermission("asm.mode.bodypart.leftarm"))
            leftArm = createIcon(new ItemStack(Material.STICK), "Piloter le bras gauche", "setMode leftArm");
        if(pe.hasPermission("asm.mode.equipment"))
            equipment = createIcon(new ItemStack(Material.GOLDEN_CHESTPLATE), "Changer l'équipement", "setMode equipment");
        if(pe.hasPermission("asm.mode.bodypart.chest"))
            chest = createIcon(new ItemStack(Material.LEATHER_CHESTPLATE), "Piloter le torse", "setMode chest");
        if(pe.hasPermission("asm.mode.bodypart.rightarm"))
            rightArm = createIcon(new ItemStack(Material.STICK), "Piloter le bras droit", "setMode rightArm");
        if(pe.hasPermission("asm.mode.invisibility"))
            invisibility = createIcon(new ItemStack(Material.POTION), "Activer/Desactiver l'invisibilité", "setMode invisibility");
        if(pe.hasPermission("asm.mode.showarm"))
            showArm = createIcon(new ItemStack(Material.STICK), "Activer/Desactiver les bras", "setMode showArm");
        if(pe.hasPermission("asm.mode.bodypart.leftleg"))
            leftLeg = createIcon(new ItemStack(Material.STICK), "Piloter la jambe gauche", "setMode leftLeg");
        if(pe.hasPermission("asm.mode.plate"))
            plate = createIcon(new ItemStack(Material.STONE_PRESSURE_PLATE), "Activer/Desactiver la plaque de stone", "setMode plate");
        if(pe.hasPermission("asm.mode.bodypart.rightleg"))
            rightLeg = createIcon(new ItemStack(Material.STICK), "Piloter la jambe droite", "setMode rightLeg");
        if(pe.hasPermission("asm.command.help"))
            help = createIcon(new ItemStack(Material.NETHER_STAR), "Besoin d'aide ?", "help");
        if(pe.hasPermission("asm.mode.movearmorstand"))
            armorStand = createIcon(new ItemStack(Material.ARMOR_STAND), "Deplacer porte armure", "setMode armorstand");
        if(pe.hasPermission("asm.mode.rotation"))
            rotation = createIcon(new ItemStack(Material.MAGENTA_WOOL), "Tourner le porte armure", "setMode rotation");
        if(pe.hasPermission("asm.mode.size"))
            size = createIcon(new ItemStack(Material.ARMOR_STAND), "Agrandir/rétrécir", "setMode size");
        if(pe.hasPermission("asm.mode.info"))
            info = createIcon(new ItemStack(Material.SUNFLOWER), "Information", "setMode info");
        if(pe.hasPermission("asm.adjustment.coarse"))
            coarseAdj = createIcon(new ItemStack(Material.REDSTONE_TORCH), "Ajustement grossier", "setAdj coarse");
        if(pe.hasPermission("asm.adjustment.fine"))
            fineAdj = createIcon(new ItemStack(Material.TORCH), "Ajustement fin", "setAdj fine");
        if (pe.hasPermission("asm.mode.showname"))
            showName = createIcon(new ItemStack(Material.MAP), "Afficher/Masquer le nom de l'armor stand", "setMode showName");

        switch (pe.getMode()) {
            case Rename:
                nameTag.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Gravity:
                gravity.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Invulnerable:
                invulnerable.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Equipment:
                equipment.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Invisibility:
                invisibility.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case ShowArm:
                showArm.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Plate:
                plate.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Reset:
                reset.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case BodyPart:
                switch (pe.getBodyPart()) {
                    case LeftArm:
                        leftArm.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        break;
                    case RightArm:
                        rightArm.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        break;
                    case LeftLeg:
                        leftLeg.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        break;
                    case RightLeg:
                        rightLeg.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        break;
                    case Head:
                        head.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        break;
                    case Chest:
                        chest.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        break;
                }
                break;
            case Size:
                size.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case MoveArmorStand:
                armorStand.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Rotation:
                rotation.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Info:
                info.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case ShowName:
                showName.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
        }

        if (pe.getAdjustment() == ArmorStandModifier.getInstance().getConfig().getInt("adjustment.fine"))
            fineAdj.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        else if (pe.getAdjustment() == ArmorStandModifier.getInstance().getConfig().getInt("adjustment.coarse"))
            coarseAdj.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        switch (pe.getAxis()) {
            case X:
                axisX.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Y:
                axisY.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
            case Z:
                axisZ.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                break;
        }

        return new ItemStack[] { axisX, axisY, axisZ, null, null, null, nameTag, showName, gravity,
                null, null, null, null, null, null, info, null, invulnerable,
                null, head, null, null, armorStand, null, reset, null, invisibility,
                leftArm, chest, rightArm, null, rotation, null, showArm, size, equipment,
                leftLeg, plate, rightLeg, null, null, null, null, null, null,
                null, null, null, null, fineAdj, coarseAdj, null, null, help };
    }

    /**
     * Create the content for the equipment menu
     * @return array contents all items for the inventory
     */
    private ItemStack[] createEquipmentMenu() {
        ItemStack blueGlass = createIcon(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), "", "");
        ItemStack grayGlass = createIcon(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), "", "");
        ItemStack yellowGlass = createIcon(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), "", "");
        ItemStack lightGrayGlass = createIcon(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE), "", "");

        ItemStack helmetSlot = createIcon(new ItemStack(Material.LEATHER_HELMET), "Slot du casque", "");
        ItemStack chestplateSlot = createIcon(new ItemStack(Material.LEATHER_CHESTPLATE), "Slot du plastron", "");
        ItemStack leggingsSlot = createIcon(new ItemStack(Material.LEATHER_LEGGINGS), "Slot des jambières", "");
        ItemStack bootsSlot = createIcon(new ItemStack(Material.LEATHER_BOOTS), "Slot des bottes", "");
        ItemStack rightHandSlot = createIcon(new ItemStack(Material.DIAMOND_SWORD), "Slot de la main droite", "");
        ItemStack leftHandSlot = createIcon(new ItemStack(Material.SHIELD), "Slot de la main gauche", "");

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        EntityEquipment asEquipment = pe.getTarget().getEquipment();
        ItemStack helmet = null, chest = null, leggings = null, boots = null, rightHand = null, leftHand = null;
        if (asEquipment != null)
        {
            helmet = asEquipment.getHelmet();
            chest = asEquipment.getChestplate();
            leggings = asEquipment.getLeggings();
            boots = asEquipment.getBoots();
            rightHand = asEquipment.getItemInMainHand();
            leftHand = asEquipment.getItemInOffHand();
        }

        ItemStack lockAddHead = lightGrayGlass, lockChangeHead = lightGrayGlass, lockRemoveHead = lightGrayGlass,
                lockAddChest = lightGrayGlass, lockChangeChest = lightGrayGlass, lockRemoveChest = lightGrayGlass,
                lockAddLegs = lightGrayGlass, lockChangeLegs = lightGrayGlass, lockRemoveLegs = lightGrayGlass,
                lockAddFeet = lightGrayGlass, lockChangeFeet = lightGrayGlass, lockRemoveFeet = lightGrayGlass,
                lockAddHand = lightGrayGlass, lockChangeHand = lightGrayGlass, lockRemoveHand = lightGrayGlass,
                lockAddOffHand = lightGrayGlass, lockChangeOffHand = lightGrayGlass, lockRemoveOffHand = lightGrayGlass;

        ArmorStand as = pe.getTarget();

        if (pe.hasPermission("asm.equipment.lock")) {
            if (as.hasEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING))
                lockAddHead = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter équipement (Interdit)", "");
            else
                lockAddHead = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter équipement (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING_OR_CHANGING))
                lockChangeHead = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter ou changer équipment (Interdit)", "");
            else
                lockChangeHead = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter ou changer équipment (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING))
                lockRemoveHead = createIcon(new ItemStack(Material.RED_WOOL), "Retirer ou changer équipment (Interdit)", "");
            else
                lockRemoveHead = createIcon(new ItemStack(Material.GREEN_WOOL), "Retirer ou changer équipment (Autorisé)", "");

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (as.hasEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING))
                lockAddChest = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter équipement (Interdit)", "");
            else
                lockAddChest = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter équipement (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING_OR_CHANGING))
                lockChangeChest = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter ou changer équipement (Interdit)", "");
            else
                lockChangeChest = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter ou changer équipement (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.REMOVING_OR_CHANGING))
                lockRemoveChest = createIcon(new ItemStack(Material.RED_WOOL), "Retirer ou changer équipement (Interdit)", "");
            else
                lockRemoveChest = createIcon(new ItemStack(Material.GREEN_WOOL), "Retirer ou changer équipement (Autorisé)", "");

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (as.hasEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING))
                lockAddLegs = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter équipement (Interdit)", "");
            else
                lockAddLegs = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter équipement (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING_OR_CHANGING))
                lockChangeLegs = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter ou changer équipement (Interdit)", "");
            else
                lockChangeLegs = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter ou changer équipement (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.REMOVING_OR_CHANGING))
                lockRemoveLegs = createIcon(new ItemStack(Material.RED_WOOL), "Retirer ou changer équipement (Interdit)", "");
            else
                lockRemoveLegs = createIcon(new ItemStack(Material.GREEN_WOOL), "Retirer ou changer équipement (Autorisé)", "");

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (as.hasEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING))
                lockAddFeet = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter équipement (Interdit)", "");
            else
                lockAddFeet = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter équipement (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING_OR_CHANGING))
                lockChangeFeet = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter ou changer équipement (Interdit)", "");
            else
                lockChangeFeet = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter ou changer équipement (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.REMOVING_OR_CHANGING))
                lockRemoveFeet = createIcon(new ItemStack(Material.RED_WOOL), "Retirer ou changer équipement (Interdit)", "");
            else
                lockRemoveFeet = createIcon(new ItemStack(Material.GREEN_WOOL), "Retirer ou changer équipement (Autorisé)", "");

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (as.hasEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING))
                lockAddHand = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter item (Interdit)", "");
            else
                lockAddHand = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter item (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING_OR_CHANGING))
                lockChangeHand = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter ou changer item (Interdit)", "");
            else
                lockChangeHand = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter ou changer item (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING))
                lockRemoveHand = createIcon(new ItemStack(Material.RED_WOOL), "Retirer ou changer item (Interdit)", "");
            else
                lockRemoveHand = createIcon(new ItemStack(Material.GREEN_WOOL), "Retirer ou changer item (Autorisé)", "");

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (as.hasEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING))
                lockAddOffHand = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter item (Interdit)", "");
            else
                lockAddOffHand = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter item (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING_OR_CHANGING))
                lockChangeOffHand = createIcon(new ItemStack(Material.RED_WOOL), "Ajouter ou changer item (Interdit)", "");
            else
                lockChangeOffHand = createIcon(new ItemStack(Material.GREEN_WOOL), "Ajouter ou changer item (Autorisé)", "");

            if (as.hasEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.REMOVING_OR_CHANGING))
                lockRemoveOffHand = createIcon(new ItemStack(Material.RED_WOOL), "Retirer ou changer item (Interdit)", "");
            else
                lockRemoveOffHand = createIcon(new ItemStack(Material.GREEN_WOOL), "Retirer ou changer item (Autorisé)", "");
        }

        return new ItemStack[]{ helmetSlot, blueGlass, helmet, grayGlass, grayGlass, yellowGlass, lockAddHead, lockChangeHead, lockRemoveHead,
                chestplateSlot, blueGlass, chest, grayGlass, grayGlass, yellowGlass, lockAddChest, lockChangeChest, lockRemoveChest,
                leggingsSlot, blueGlass, leggings, grayGlass, grayGlass, yellowGlass, lockAddLegs, lockChangeLegs, lockRemoveLegs,
                bootsSlot, blueGlass, boots, grayGlass, grayGlass, yellowGlass, lockAddFeet, lockChangeFeet, lockRemoveFeet,
                rightHandSlot, blueGlass, rightHand, grayGlass, grayGlass, yellowGlass, lockAddHand, lockChangeHand, lockRemoveHand,
                leftHandSlot, blueGlass, leftHand, grayGlass, grayGlass, yellowGlass, lockAddOffHand, lockChangeOffHand, lockRemoveOffHand };
    }

    /**
     * Create the content for the armorstand list
     * @return array contents all items for the inventory
     */
    private ItemStack[] createArmorStandListMenu() {
        ItemStack[] output = { null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null };

        for (int i = 0; i < pe.getArmorStands().size() && i < 53; i++) {
            ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(pe.getArmorStands().get(i));
            if (armorStand != null) {
                ItemStack item = new ItemStack(Material.ARMOR_STAND);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setDisplayName(ChatColor.RESET + pe.getArmorStands().get(i).toString());
                ArrayList<String> loreList = new ArrayList<>();
                loreList.add("");
                loreList.add(ChatColor.GRAY + "X : " + armorStand.getLocation().getX());
                loreList.add(ChatColor.GRAY + "Y : " + armorStand.getLocation().getY());
                loreList.add(ChatColor.GRAY + "Z : " + armorStand.getLocation().getZ());
                DecimalFormat f = new DecimalFormat();
                f.setMaximumFractionDigits(2);
                loreList.add(ChatColor.GRAY + "Distance : " + f.format(pe.getPlayer().getLocation().distance(armorStand.getLocation())));
                meta.setLore(loreList);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);

                output[i] = item;
            } else {
                output[i] = createIcon(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), "", "");
            }
        }

        return output;
    }

    /**
     * Get the Armor Stand Modifier GUI interface title
     * @return ASM GUI title
     */
    public static String getAsmGuiTitle() {
        return asmGuiTitle;
    }

    /**
     * Set the Armor Stand Modifier GUI interface title
     * @param asmGuiTitle new title
     */
    public static void setAsmGuiTitle(String asmGuiTitle) {
        Menu.asmGuiTitle = asmGuiTitle;
    }

    /**
     * Get the Armor Stand Modifier Equipment interface title
     * @return ASM Equipment title
     */
    public static String getAsmEquipmentTitle() {
        return asmEquipmentTitle;
    }

    /**
     * Set the Armor Stand Modifier Equipment title
     * @param asmEquipmentTitle new title
     */
    public static void setAsmEquipmentTitle(String asmEquipmentTitle) {
        Menu.asmEquipmentTitle = asmEquipmentTitle;
    }

    /**
     * Get the Armor Stand List interface title
     * @return Armor Stand List title
     */
    public static String getAsmListTitle() {
        return asmListTitle;
    }

    /**
     * Set the Armor Stand List interface title
     * @param asmListTitle new title
     */
    public static void setAsmListTitle(String asmListTitle) {
        Menu.asmListTitle = asmListTitle;
    }
}
