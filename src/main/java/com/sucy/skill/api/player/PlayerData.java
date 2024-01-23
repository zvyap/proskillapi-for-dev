package com.sucy.skill.api.player;

import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.enums.PointSource;
import com.sucy.skill.api.event.PlayerExperienceLostEvent;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.cast.PlayerCastBars;
import com.sucy.skill.cast.PlayerTextCastingData;
import com.sucy.skill.data.PlayerEquips;
import com.sucy.skill.dynamic.EffectComponent;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface PlayerData {
    /**
     * Retrieves the Bukkit player object of the owner
     *
     * @return Bukkit player object of the owner or null if offline
     */
    Player getPlayer();

    /**
     * Retrieves the name of the owner
     *
     * @return name of the owner
     */
    String getPlayerName();

    UUID getUUID();

    /**
     * Retrieves the skill bar data for the owner
     *
     * @return skill bar data of the owner
     */
    PlayerSkillBar getSkillBar();

    /**
     * @return cast bars data for the player
     */
    PlayerCastBars getCastBars();

    /**
     * @return cast bars data for the player
     */
    PlayerTextCastingData getTextCastingData();

    /**
     * Returns the data for the player's combos
     *
     * @return combo data for the player
     */
    PlayerCombos getComboData();

    /**
     * @return extra data attached to the player's account
     */
    DataSection getExtraData();

    /**
     * @return equipped item data
     */
    PlayerEquips getEquips();

    /**
     * @return health during last logout
     */
    double getLastHealth();

    /**
     * Used by the API for restoring health - do not use this.
     *
     * @param health health logged off with
     */
    void setLastHealth(double health);

    /**
     * The hunger value here is not representative of the player's total hunger,
     * rather the amount left of the next hunger point. This is manipulated by
     * attributes were if an attribute says a player has twice as much "hunger"
     * as normal, this will go down by decimals to slow the decay of hunger.
     *
     * @return amount of the next hunger point the player has
     */
    double getHungerValue();

    /**
     * @param hungerValue new hunger value
     * @see PlayerData#getHungerValue
     */
    void setHungerValue(double hungerValue);

    int subtractHungerValue(double amount);

    /**
     * Ends the initialization flag for the data. Used by the
     * API to avoid async issues. Do not use this in other
     * plugins.
     */
    void endInit();

    /**
     * Retrieves the name of the active map menu scheme for the player
     *
     * @return map menu scheme name
     */
    String getScheme();

    /**
     * Sets the active scheme name for the player
     *
     * @param name name of the scheme
     */
    void setScheme(String name);

    /**
     * Retrieves a map of all player attribute totals. Modifying
     * the map will not change actual player attributes.
     *
     * @return attribute totals
     */
    HashMap<String, Integer> getAttributes();

    /**
     * Retrieves a map of all attributes the player invested.
     * This doesn't count base attributes from classes or
     * bonus attributes from effects. Modifying the map will
     * not change actual player attributes.
     *
     * @return attribute totals
     */
    HashMap<String, Integer> getInvestedAttributes();

    /**
     * Retrieves a map of all attributes upgrade stages.
     * This doesn't count base attributes from classes or
     * bonus attributes from effects. Modifying the map will
     * not change actual player attributes.
     *
     * @return attribute upgrade stages
     */
    HashMap<String, Integer> getInvestedAttributesStages();

    /**
     * Gets the number of attribute points the player has
     * from invested and bonus sources.
     *
     * @param key attribute key
     * @return number of total points
     */
    int getAttribute(String key);

    /**
     * Gets the number of attribute points invested in the
     * given attribute
     *
     * @param key attribute key
     * @return number of invested points
     */
    int getInvestedAttribute(String key);

    /**
     * Gets the upgrade stage of the
     * given attribute
     *
     * @param key attribute key
     * @return the stage of the attribute
     */
    int getInvestedAttributeStage(String key);

    /**
     * Checks whether the player has any
     * points invested in a given attribute
     *
     * @param key attribute key
     * @return true if any points are invested, false otherwise
     */
    boolean hasAttribute(String key);

    /**
     * Invests a point in the attribute if the player
     * has enough remaining attribute points. If the player
     * has no remaining points, this will do nothing.
     *
     * @param key attribute key
     * @return whether it was successfully upgraded
     */
    boolean upAttribute(String key);

    /**
     * Calculating cost using the formula:
     * costBase + (int) Math.floor(attrStage*costModifier).
     *
     * @param key attribute key
     * @return calculated cost of single attribute upgrade
     */
    int getAttributeUpCost(String key);

    /**
     * Calculating cost using the formula:
     * costBase + (int) Math.floor(attrStage*costModifier).
     * <p>
     * OVERLOAD to check cost of certain stage:
     * mod = -1 is e.g. of previous stage,
     * mod = 0 is e.g. of current stage,
     * mod = 1 is e.g. default cost of next stage.
     *
     * @param key      attribute key
     * @param modifier stage number modifier
     * @return calculated cost of single attribute upgrade
     */
    int getAttributeUpCost(String key, Integer modifier);

    /**
     * Calculating cost using the formula:
     * costBase + (int) Math.floor(attrStage*costModifier).
     * <p>
     * OVERLOAD to check total cost of upgrading
     * [from] stage --> [to] stage
     * where [from] is starting stage
     * and where [to] is target stage
     *
     * @param key  attribute key
     * @param from starting stage
     * @param to   target stage
     * @return calculated cost of single attribute upgrade
     */
    int getAttributeUpCost(String key, Integer from, Integer to);

    /**
     * Upgrades the player attribute stage without costing
     * attribute points.
     *
     * @param key    attribute to give points for
     * @param amount amount to give
     */
    void giveAttribute(String key, int amount);

    /**
     * Adds stat modifier to the player.
     * These bypass min/max invest amount and cannot be refunded.
     *
     * @param key      stat key
     * @param modifier The player stat modifier
     * @param update   calculate player stat immediately and apply to him
     */
    void addStatModifier(String key, PlayerStatModifier modifier, boolean update);

    /**
     * Get all stat modifier from the player.
     *
     * @param key stat key
     * @return stat modifier list of the attribute given
     */
    List<PlayerStatModifier> getStatModifiers(String key);

    /**
     * Adds attribute modifier to the player.
     * These bypass min/max invest amount and cannot be refunded.
     *
     * @param key      attribute key
     * @param modifier The player attribute modifier
     * @param update   calculate player stat immediately and apply to him
     */
    void addAttributeModifier(String key, PlayerAttributeModifier modifier, boolean update);

    /**
     * Get all attribute modifier from the player.
     *
     * @param key attribute key
     * @return attribute modifier list of the attribute given
     */
    List<PlayerAttributeModifier> getAttributeModifiers(String key);

    /**
     * Refunds an attribute point from the given attribute
     * if there are any points invested in it. If there are
     * none, this will do nothing.
     *
     * @param key attribute key
     */
    boolean refundAttribute(String key);

    /**
     * Refunds all spent attribute points for a specific attribute
     *
     * @param key attribute key
     */
    void refundAttributes(String key);

    /**
     * Refunds all spent attribute points
     */
    void refundAttributes();

    /**
     * Retrieves the current number of attribute points the player has
     *
     * @return attribute point total
     */
    int getAttributePoints();

    /**
     * Gives the player attribute points
     *
     * @param amount amount of attribute points
     */
    void giveAttribPoints(int amount);

    /**
     * Sets the current amount of attribute points
     *
     * @param amount amount of points to have
     */
    void setAttribPoints(int amount);

    /**
     * Scales a stat value using the player's attributes
     *
     * @param stat      stat key
     * @param baseValue the default value come with vanilla Minecraft, <strong>Only needed for custom stats and Speed</strong>
     * @return modified value
     */
    double scaleStat(String stat, double baseValue);

    /**
     * Scales a stat value using the player's attributes
     *
     * @param stat         stat key
     * @param defaultValue the default value come with vanilla Minecraft, <strong>Only needed for custom stats and Speed</strong>
     * @param min          min value
     * @param max          max value
     * @return modified value
     */
    double scaleStat(String stat, double defaultValue, double min, double max);

    /**
     * Scales a dynamic skill's value using global modifiers
     *
     * @param component component holding the value
     * @param key       key of the value
     * @param value     unmodified value
     * @return the modified value
     */
    double scaleDynamic(EffectComponent component, String key, double value);

    /**
     * Opens the attribute menu for the player
     *
     * @return true if successfully opened, false if conditions weren't met
     */
    boolean openAttributeMenu();

    /**
     * Retrieves the player's attribute data regarding total amounts spent.
     * Modifying this will modify the player's
     * actual data.
     *
     * @return the player's attribute data
     */
    Map<String, Integer> getAttributeData();

    /**
     * Retrieves the player's attribute data regarding current stages.
     * Modifying this will modify the player's
     * actual data.
     *
     * @return the player's attribute data
     */
    Map<String, Integer> getAttributeStageData();

    /**
     * Checks if the owner has a skill by name. This is not case-sensitive
     * and does not check to see if the skill is unlocked. It only checks if
     * the skill is available to upgrade/use.
     *
     * @param name name of the skill
     * @return true if has the skill, false otherwise
     */
    boolean hasSkill(String name);

    /**
     * Retrieves a skill of the owner by name. This is not case-sensitive.
     *
     * @param name name of the skill
     * @return data for the skill or null if the player doesn't have the skill
     */
    PlayerSkill getSkill(String name);

    int getInvestedSkillPoints();

    /**
     * Retrieves all of the skill data the player has. Modifying this
     * collection will not modify the player's owned skills but modifying
     * one of the elements will change that element's data for the player.
     *
     * @return collection of skill data for the owner
     */
    Collection<PlayerSkill> getSkills();

    Set<PlayerDataImpl.ExternallyAddedSkill> getExternallyAddedSkills();

    /**
     * Retrieves the level of a skill for the owner. This is not case-sensitive.
     *
     * @param name name of the skill
     * @return level of the skill or 0 if not found
     */
    int getSkillLevel(String name);

    /**
     * Gives the player a skill outside of the normal class skills.
     * This skill will not show up in a skill tree.
     *
     * @param skill skill to give the player
     */
    void giveSkill(Skill skill);

    /**
     * Gives the player a skill using the class data as a parent. This
     * skill will not show up in a skill tree.
     *
     * @param skill  skill to give the player
     * @param parent parent class data
     */
    void giveSkill(Skill skill, PlayerClass parent);

    void addSkill(Skill skill, PlayerClass parent);

    void addSkillExternally(Skill skill, PlayerClass parent, NamespacedKey namespacedKey, int level);

    void removeSkillExternally(Skill skill, NamespacedKey namespacedKey);

    /**
     * Attempts to auto-level any skills that are able to do so
     */
    void autoLevel();

    void autoLevel(Skill skill);

    /**
     * Upgrades a skill owned by the player. The player must own the skill,
     * have enough skill points, meet the level and skill requirements, and
     * not have maxed out the skill already in order to upgrade the skill.
     * This will consume the skill point cost while upgrading the skill.
     *
     * @param skill skill to upgrade
     * @return true if successfully was upgraded, false otherwise
     */
    boolean upgradeSkill(Skill skill);

    /**
     * Forcefully upgrades a skill, not letting other plugins
     * cancel it and ignoring any requirements to do so
     *
     * @param skill skill to forcefully upgrade
     */
    void forceUpSkill(PlayerSkill skill);

    void forceUpSkill(PlayerSkill skill, int amount);

    /**
     * Downgrades a skill owned by the player. The player must own the skill and it must
     * not currently be level 0 for the player to downgrade the skill. This will refund
     * the skill point cost when downgrading the skill.
     *
     * @param skill skill to downgrade
     * @return true if successfully downgraded, false otherwise
     */
    boolean downgradeSkill(Skill skill);

    /**
     * Forcefully downgrades a skill, not letting other plugins
     * stop it and ignoring any skill requirements to do so.
     *
     * @param skill skill to forcefully downgrade
     */
    void forceDownSkill(PlayerSkill skill);

    void forceDownSkill(PlayerSkill skill, int amount);

    /**
     * Refunds a skill for the player, resetting it down
     * to level 0 and giving back any invested skill points.
     *
     * @param skill skill to refund
     */
    void refundSkill(PlayerSkill skill);

    /**
     * Refunds all skills for the player
     */
    void refundSkills();

    /**
     * Shows the skill tree for the player. If the player has multiple trees,
     * this will show the list of skill trees they can view.
     */
    void showSkills();

    /**
     * Shows the class details for the player
     *
     * @param player player to show to
     * @return true if shown, false if nothing to show
     */
    boolean showDetails(Player player);

    /**
     * Shows profession options of the first class group available
     *
     * @param player player to show profession options for
     * @return true if shown profession options, false if none available
     */
    boolean showProfession(Player player);

    /**
     * Shows the skill tree for the player. If the player has multiple trees,
     * this will show the list of skill trees they can view.
     *
     * @param player player to show the skill tree for
     * @return true if able to show the player, false otherwise
     */
    boolean showSkills(Player player);

    /**
     * Shows the skill tree to the player for the given class
     *
     * @param player      player to show
     * @param playerClass class to look for
     * @return true if succeeded, false otherwise
     */
    boolean showSkills(Player player, PlayerClass playerClass);

    /**
     * Retrieves the name of the class shown in the skill tree
     *
     * @return class name
     */
    String getShownClassName();

    /**
     * Checks whether the player has as least one class they have professed as.
     *
     * @return true if professed, false otherwise
     */
    boolean hasClass();

    /**
     * Checks whether a player has a class within the given group
     *
     * @param group class group to check
     * @return true if has a class in the group, false otherwise
     */
    boolean hasClass(String group);

    /**
     * Retrieves the collection of the data for classes the player has professed as.
     *
     * @return collection of the data for professed classes
     */
    Collection<PlayerClass> getClasses();

    /**
     * Retrieves the data of a class the player professed as by group. This is
     * case-sensitive.
     *
     * @param group group to get the profession for
     * @return professed class data or null if not professed for the group
     */
    PlayerClass getClass(String group);

    /**
     * Retrieves the data of the professed class under the main class group. The
     * "main" group is determined by the setting in the config.
     *
     * @return main professed class data or null if not professed for the main group
     */
    @Nullable
    PlayerClass getMainClass();

    /**
     * Sets the professed class for the player for the corresponding group. This
     * will not save any skills, experience, or levels of the previous class if
     * there was any. The new class will start at level 1 with 0 experience.
     *
     * @param rpgClass class to assign to the player
     * @return the player-specific data for the new class
     */
    PlayerClass setClass(RPGClass previous, RPGClass rpgClass, boolean reset);

    /**
     * Checks whether the player is professed as the class
     * without checking child classes.
     *
     * @param rpgClass class to check
     * @return true if professed as the specific class, false otherwise
     */
    boolean isExactClass(RPGClass rpgClass);

    /**
     * Checks whether the player is professed as the class
     * or any of its children.
     *
     * @param rpgClass class to check
     * @return true if professed as the class or one of its children, false otherwise
     */
    boolean isClass(RPGClass rpgClass);

    /**
     * Checks whether the player can profess into the given class. This
     * checks to make sure the player is currently professed as the parent of the
     * given class and is high enough of a level to do so.
     *
     * @param rpgClass class to check
     * @return true if can profess, false otherwise
     */
    boolean canProfess(RPGClass rpgClass);

    /**
     * Resets the class data for the owner under the given group. This will remove
     * the profession entirely, leaving no remaining data until the player professes
     * again to a starting class.
     *
     * @param group      group to reset
     * @param toSubclass - whether we are professing to a subclass of the previous class
     * @return the number of skill points to be refunded
     */
    int reset(String group, boolean toSubclass);

    /**
     * Resets all profession data for the player. This clears all professions the player
     * has, leaving no remaining data until the player professes again to a starting class.
     */
    void resetAll();

    /**
     * Resets attributes for the player
     */
    void resetAttribs();

    /**
     * Professes the player into the class if they are able to. This will
     * reset the class data if the group options are set to reset upon
     * profession. Otherwise, all skills, experience, and levels of the
     * current class under the group will be retained and carried over into
     * the new profession.
     *
     * @param rpgClass class to profess into
     * @return true if successfully professed, false otherwise
     */
    boolean profess(RPGClass rpgClass);

    /**
     * Gives experience to the player from the given source
     *
     * @param amount amount of experience to give
     * @param source source of the experience
     */
    void giveExp(double amount, ExpSource source);

    /**
     * Gives experience to the player from the given source
     *
     * @param amount  amount of experience to give
     * @param source  source of the experience
     * @param message whether to show the configured message if enabled
     */
    void giveExp(double amount, ExpSource source, boolean message);

    /**
     * Causes the player to lose experience
     * This will launch a {@link PlayerExperienceLostEvent} event before taking the experience.
     *
     * @param amount  percent of experience to lose
     * @param percent whether to take the amount as a percentage
     */
    void loseExp(double amount, boolean percent, boolean changeLevel);

    /**
     * Causes the player to lose experience as a penalty (generally for dying)
     */
    void loseExp();

    /**
     * Gives levels to the player for all classes matching the experience source
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     */
    boolean giveLevels(int amount, ExpSource source);

    /**
     * Causes the player to lose levels
     */
    void loseLevels(int amount);

    /**
     * Gives skill points to the player for all classes matching the experience source
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     * @deprecated See {@link PlayerData#givePoints(int, PointSource)} instead
     */
    @Deprecated
    void givePoints(int amount, ExpSource source);

    /**
     * Gives skill points to the player for all classes matching the experience source
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     */
    void givePoints(int amount, PointSource source);

    /**
     * Sets the skill point amount to the player for all classes
     *
     * @param amount amount of levels to set to
     */
    void setPoints(int amount);

    /**
     * Updates all the stats of a player based on their current attributes
     * This method is very heavy, consume resources and notable by player
     * Checkout other method such as {@link #updateWalkSpeed(Player)} for a light refresh
     * <br>
     * This also does not update the player equipment
     * You will need to call {@link PlayerEquips#update(Player)} before this function
     * to update attribute/stats that comes with equipments
     */
    void updatePlayerStat(Player player);

    /**
     * Updates walk speed of a player based on their current attributes and apply
     *
     * @param player the player
     */
    void updateWalkSpeed(Player player);

    double getModifiedMaxHealth(Player player);

    /**
     * Updates health of a player based on their current attributes and apply
     *
     * @param player the player
     */
    void updateHealth(Player player);

    void updateMCAttribute(Player player, Attribute attribute, String attribKey, double min, double max);

    /**
     * Retrieves the amount of mana the player currently has.
     *
     * @return current player mana
     */
    double getMana();

    /**
     * Sets the player's amount of mana without launching events
     *
     * @param amount current mana
     */
    void setMana(double amount);

    /**
     * Checks whether the player has at least the specified amount of mana
     *
     * @param amount required mana amount
     * @return true if has the amount of mana, false otherwise
     */
    boolean hasMana(double amount);

    /**
     * Retrieves the max amount of mana the player can have including bonus mana
     *
     * @return max amount of mana the player can have
     */
    double getMaxMana();

    /**
     * Regenerates mana for the player based on the regen amounts of professed classes
     */
    void regenMana();

    /**
     * Gives mana to the player from an unknown source. This will not
     * cause the player's mana to go above their max amount.
     *
     * @param amount amount of mana to give
     */
    void giveMana(double amount);

    /**
     * Gives mana to the player from the given mana source. This will not
     * cause the player's mana to go above the max amount.
     *
     * @param amount amount of mana to give
     * @param source source of the mana
     */
    void giveMana(double amount, ManaSource source);

    /**
     * Takes mana away from the player for an unknown reason. This will not
     * cause the player to fall below 0 mana.
     *
     * @param amount amount of mana to take away
     */
    void useMana(double amount);

    /**
     * Takes mana away from the player for the specified reason. This will not
     * cause the player to fall below 0 mana.
     *
     * @param amount amount of mana to take away
     * @param cost   source of the mana cost
     */
    void useMana(double amount, ManaCost cost);

    /**
     * Remove stat modifier with the exact uuid
     *
     * @param uuid   The uuid
     * @param update calculate player stat immediately and apply to him
     */
    void removeStatModifier(UUID uuid, boolean update);

    /**
     * Clear all stat modifier which is not persistent
     */
    void clearStatModifier();

    /**
     * Remove attribute modifier with the exact uuid
     *
     * @param uuid   The uuid
     * @param update calculate player stat immediately and apply to him
     */
    void removeAttributeModifier(UUID uuid, boolean update);

    /**
     * Clear all attribute modifier which is not persistent
     */
    void clearAttributeModifiers();

    /**
     * Clear all of the modifiers including stat modifier and attribute modifier
     */
    void clearAllModifiers();

    /**
     * Retrieves a skill the player has bound by material
     *
     * @param mat material to get the bind for
     * @return skill bound to the material or null if none are bound
     */
    @Deprecated
    PlayerSkill getBoundSkill(Material mat);

    /**
     * Retrieves the bound data for the player. Modifying this map will
     * modify the bindings the player has.
     *
     * @return the skill binds data for the player
     */
    @Deprecated
    HashMap<Material, PlayerSkill> getBinds();

    /**
     * Checks whether the material has a skill bound to it
     *
     * @param mat material to check
     * @return true if a skill is bound to it, false otherwise
     */
    @Deprecated
    boolean isBound(Material mat);

    /**
     * Binds a skill to a material for the player. The bind will not work if the skill
     * was already bound to the material.
     *
     * @param mat   material to bind the skill to
     * @param skill skill to bind to the material
     * @return true if was able to bind the skill, false otherwise
     */
    @Deprecated
    boolean bind(Material mat, PlayerSkill skill);

    /**
     * Clears a skill binding on the material. If there is no binding on the
     * material, this will do nothing.
     *
     * @param mat material to clear bindings from
     * @return true if a binding was cleared, false otherwise
     */
    @Deprecated
    boolean clearBind(Material mat);

    /**
     * Decrypt and return the saved values on the account.
     *
     * @param key The key is used to save the value.
     * @return Decrypted value
     */
    Object getPersistentData(String key);

    /**
     * Encrypt and save values to account for long-term storage
     *
     * @param key  The key is used to save the value.
     * @param data The value is stored. Currently supported types are:
     *             Number, String, Player, TempEntity, Entity
     */
    void setPersistentData(String key, Object data);

    /**
     * Remove a value with a specific key
     *
     * @param key The key is used to save the value.
     */
    void removePersistentData(String key);

    /**
     * @return original HashMap used to store persistent data
     */
    Map<String, String> getAllPersistentData();

    /**
     * Clears the skill binding for the given skill. This will remove the bindings
     * on all materials involving the skill.
     *
     * @param skill skill to unbind
     */
    @Deprecated
    void clearBinds(Skill skill);

    /**
     * Clears all binds the player currently has
     */
    @Deprecated
    void clearAllBinds();

    /**
     * Records any data to save with class data
     *
     * @param player player to record for
     */
    void record(Player player);

    /**
     * Updates the scoreboard with the player's current class.
     * This is already done by the API and doesn't need to be
     * done by other plugins.
     */
    void updateScoreboard();

    /**
     * Starts passive abilities for the player if they are online. This is
     * already called by the API and shouldn't be called by other plugins.
     *
     * @param player player to set the passive skills up for
     */
    void startPassives(Player player);

    /**
     * Stops passive abilities for the player if they are online. This is already
     * called by the API and shouldn't be called by other plugins.
     *
     * @param player player to stop the passive skills for
     */
    void stopPassives(Player player);

    /**
     * Casts a skill by name for the player. In order to cast the skill,
     * the player must be online, have the skill unlocked, have enough mana,
     * have the skill off cooldown, and have a proper target if applicable.
     *
     * @param skillName name of the skill ot cast
     * @return true if successfully cast the skill, false otherwise
     */
    boolean cast(String skillName);

    /**
     * Casts a skill for the player. In order to cast the skill,
     * the player must be online, have the skill unlocked, have enough mana,
     * have the skill off cooldown, and have a proper target if applicable.
     *
     * @param skill skill to cast
     * @return true if successfully cast the skill, false otherwise
     */
    boolean cast(PlayerSkill skill);

    boolean applyUse(Player player, PlayerSkill skill, double manaCost);

    /**
     * Checks the cooldown and mana requirements for a skill
     *
     * @param skill    skill to check for
     * @param cooldown whether to check cooldowns
     * @param mana     whether to check mana requirements
     * @return true if can use
     */
    boolean check(PlayerSkill skill, boolean cooldown, boolean mana);

    /**
     * Stops the current preview, if any, and registers
     * the on-stop runnable for a new preview, if any
     *
     * @param onPreviewStop runnable to execute when the new preview stops
     */
    void setOnPreviewStop(@Nullable Runnable onPreviewStop);

    /**
     * Initializes the application of the data for the player
     *
     * @param player player to set up for
     */
    void init(Player player);
}
