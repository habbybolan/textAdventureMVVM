package com.habbybolan.textadventure.model.inventory.weapon;

import android.database.Cursor;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.InventoryEntity;
import com.habbybolan.textadventure.repository.database.LootInventory;

import org.json.JSONException;
import org.json.JSONObject;

/*
Sets up a weapon for a CharacterEntity with an Attack and Special Attack
 */

public class Weapon implements Inventory {
    private String description = "Weapon description"; //  weapon description
    private String weaponName = ""; // weapon name
    private int tier; //  the quality of a weapon - higher the tier, the better
    static final private int numberOfWeapons = 1;
    private Attack attack; // the weapon's normal attack
    private SpecialAttack specialAttack; // the weapon's special attack
    static public final String table = "weapons";
    private int weaponID;
    private final String DEFAULT_NAME = "fist";

    private boolean isWizardScaled = false;
    private boolean isWarriorScaled = false;
    private boolean isPaladinScaled = false;
    private boolean isArcherScaled = false;

    private int pictureResource;

    // Constructor where database opened and closed elsewhere
    public Weapon(Cursor cursor, LootInventory lootInventory) {
        setVariables(lootInventory, cursor);
        setPictureResource();
    }

    /**
     * Weapon object to be parse from JSON String
     * @param stringWeapon  JSON String of weapon to parse.
     */
    public Weapon(String stringWeapon) {
        try {
            JSONObject JSONWeapon = new JSONObject(stringWeapon);
            weaponName = JSONWeapon.getString(WEAPON_NAME);
            weaponID = JSONWeapon.getInt(WEAPON_ID);
            attack = new Attack(JSONWeapon.getJSONObject(ATTACK).toString(), this);
            specialAttack = new SpecialAttack(JSONWeapon.getJSONObject(S_ATTACK).toString(), this);
            description = JSONWeapon.getString(DESCRIPTION);
            tier  = JSONWeapon.getInt(TIER);
            pictureResource = JSONWeapon.getInt(IMAGE_RESOURCE);
            isWizardScaled = JSONWeapon.getBoolean(WIZARD_SCALED);
            isWarriorScaled = JSONWeapon.getBoolean(WARRIOR_SCALED);
            isPaladinScaled = JSONWeapon.getBoolean(PALADIN_SCALED);
            isArcherScaled = JSONWeapon.getBoolean(ARCHER_SCALED);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a default weapon object called 'fists'
     */
    public Weapon() {
        weaponName = DEFAULT_NAME;
        attack = new Attack();
        specialAttack = new SpecialAttack();
    }

    /**
     * Find if the weapon object is a default weapon.
     * @return  True if the name of the weapon is DEFAULT_WEAPON.
     */
    public boolean isDefaultWeapon() {
        return weaponName.equals(DEFAULT_NAME);
    }


    // sets the picture resource
    @Override
    public void setPictureResource() {
        // todo: get pic based on tier and type
        pictureResource = R.drawable.sword;
    }

    @Override
    public int getPictureResource() {
        return pictureResource;
    }

    @Override
    public boolean isAbility() {
        return false;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public boolean isWeapon() {
        return true;
    }

    @Override
    public boolean isAttack() {
        return false;
    }

    @Override
    public boolean isSpecialAttack() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        else if (o.getClass() != getClass()) return false;
        else if (o == this) return true;
        else return (checkWeaponEquality((Weapon) o));
    }

    // helper for checking if 2 ability scrolls are equal
    private boolean checkWeaponEquality(Weapon weapon) {
        return weapon.getName().equals(getName());
    }

    // sets all the variables that describe the weapon
    private void setVariables(LootInventory lootInventory, Cursor cursor) {
        int weaponColID = cursor.getColumnIndex(WEAPON_ID);
        weaponID = cursor.getInt(weaponColID);
        // set up the weapon name
        int weaponNameColIndex = cursor.getColumnIndex(WEAPON_NAME);
        setWeaponName(cursor.getString(weaponNameColIndex));
        // set up the attack id
        int attackColID = cursor.getColumnIndex(ATTACK_ID);
        Attack attack = lootInventory.getAttackCursorFromID(cursor.getInt(attackColID), this);
        setAttack(attack);
        // set up the special attack id
        int specialAttackColID = cursor.getColumnIndex(S_ATTACK_ID);
        SpecialAttack specialAttack = lootInventory.getSpecialAttackCursorFromID(cursor.getInt(specialAttackColID), this);
        setSpecialAttack(specialAttack);
        // is scaled values
        int wizardScaledColID = cursor.getColumnIndex(WIZARD_SCALED);
        isWizardScaled = cursor.getInt(wizardScaledColID) == 1;
        int warriorScaledColID = cursor.getColumnIndex(WARRIOR_SCALED);
        isWarriorScaled = cursor.getInt(warriorScaledColID) == 1;
        int paladinScaledColID = cursor.getColumnIndex(PALADIN_SCALED);
        isPaladinScaled = cursor.getInt(paladinScaledColID) == 1;
        int archerScaledColID = cursor.getColumnIndex(ARCHER_SCALED);
        isArcherScaled = cursor.getInt(archerScaledColID) == 1;
        // set up the weapon tier (weapon rarity)
        int tierColID = cursor.getColumnIndex(TIER);
        setTier(cursor.getInt(tierColID));
    }

    // setter methods
    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }
    public void setAttack(Attack attack) {
        this.attack = attack;
    }
    public void setSpecialAttack(SpecialAttack specialAttack) {
        this.specialAttack = specialAttack;
    }
    public void setTier(int tier) {
        this.tier = tier;
    }

    // getter methods
    public String getDescription() {
        return description;
    }
    public Attack getAttack() {
        return attack;
    }
    public SpecialAttack getSpecialAttack() {
        return specialAttack;
    }
    @Override
    public int getTier() {
        return tier;
    }
    public int getWeaponID() {
        return weaponID;
    }


    @Override
    public JSONObject serializeToJSON() throws JSONException {
        JSONObject JSONInventory = new JSONObject();
        JSONInventory.put(TYPE, TYPE_WEAPON);
        JSONInventory.put(WEAPON_NAME, weaponName);
        JSONInventory.put(WEAPON_ID, weaponID);
        JSONInventory.put(INVENTORY_TYPE, TYPE_WEAPON);
        JSONObject JSONAttack = attack.serializeToJSON();
        JSONInventory.put(ATTACK, JSONAttack);
        JSONObject JSONSAttack = specialAttack.serializeToJSON();
        JSONInventory.put(S_ATTACK, JSONSAttack);
        JSONInventory.put(DESCRIPTION, description);
        JSONInventory.put(TIER, tier);
        JSONInventory.put(IMAGE_RESOURCE, pictureResource);
        JSONInventory.put(WIZARD_SCALED, isWizardScaled);
        JSONInventory.put(WARRIOR_SCALED, isWarriorScaled);
        JSONInventory.put(PALADIN_SCALED, isPaladinScaled);
        JSONInventory.put(ARCHER_SCALED, isArcherScaled);
        return JSONInventory;
    }

    public boolean getIsWizardScaled() {
        return isWizardScaled;
    }
    public boolean getIsWarriorScaled() {
        return isWarriorScaled;
    }
    public boolean getIsPaladinScaled() {
        return isPaladinScaled;
    }
    public boolean getIsArcherScaled() {
        return isArcherScaled;
    }

    @Override
    public int getID() {
        return weaponID;
    }
    @Override
    public String getType() {
        return InventoryEntity.TYPE_WEAPON;
    }
    @Override
    public String getName() {
        return weaponName;
    }

    public static final String WEAPON_NAME = "weapon_name";
    public static final String WEAPON_ID = "weapon_id";
    public static final String ATTACK_ID = "attack_id";
    public static final String ATTACK = "attack";
    public static final String S_ATTACK_ID = "s_attack_id";
    public static final String S_ATTACK = "s_attack";
    public static final String DESCRIPTION = "description";
    public static final String TIER = "tier";

    private static final String WIZARD_SCALED = "wizard_scaled";
    private static final String WARRIOR_SCALED = "warrior_scaled";
    private static final String PALADIN_SCALED = "paladin_scaled";
    private static final String ARCHER_SCALED = "archer_scaled";

    // image resource
    public static final String IMAGE_RESOURCE = "image_resource";

}
