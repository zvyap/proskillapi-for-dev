/**
 * SkillAPI
 * com.sucy.skill.data.io.IOManager
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.data.io;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.event.PlayerAccountsLoadEvent;
import com.sucy.skill.api.event.PlayerAccountsSaveEvent;
import com.sucy.skill.api.player.*;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.cast.CastMode;
import com.sucy.skill.listener.MainListener;
import com.sucy.skill.log.Logger;
import com.sucy.skill.manager.ComboManager;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import mc.promcteam.engine.mccore.util.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Base class for managers that handle saving and loading player data
 */
public abstract class IOManager {
    private static final String
            LIMIT          = "limit",
            ACTIVE         = "active",
            ACCOUNTS       = "accounts",
            ACCOUNT_PREFIX = "acc",
            HEALTH         = "health",
            MANA           = "mana",
            CLASSES        = "classes",
            SKILLS         = "skills",
            BINDS          = "binds",
            LEVEL          = "level",
            EXP            = "exp",
            POINTS         = "points",
            SKILL_BAR      = "bar",
            HOVER          = "hover",
            EXTRA          = "extra",
            PERSISTENT     = "persistent",
            INSTANT        = "instant",
            TEXT_LAYOUT    = "text-layout",
            ENABLED        = "enabled",
            SLOTS          = "slots",
            UNASSIGNED     = "e",
            COMBOS         = "combos",
            ATTRIBS        = "attribs",
            ATTRSTAGES        = "attrstages",
            COOLDOWN       = "cd",
            HUNGER         = "hunger",
            ATTRIB_POINTS  = "attrib-points";

    /**
     * API reference
     */
    protected final SkillAPI api;

    /**
     * Initializes a new IO manager
     *
     * @param api SkillAPI reference
     */
    IOManager(SkillAPI api) {
        this.api = api;
    }

    /**
     * Loads player data for all online players
     *
     * @return loaded player data
     */
    public HashMap<String, PlayerAccounts> loadAll() {
        List<OfflinePlayer> playerList = new LinkedList<>();
        HashMap<String, PlayerAccounts> result = new HashMap<>();
        for(Player player : VersionManager.getOnlinePlayers()) {
            PlayerAccountsLoadEvent loadEvent = new PlayerAccountsLoadEvent(player);
            Bukkit.getPluginManager().callEvent(loadEvent);
            if(loadEvent.getAccounts() != null) {
                result.put(player.getUniqueId().toString().toLowerCase(), loadEvent.getAccounts());
                continue;
            }
            playerList.add(player);
        }

        result.putAll(loadAllInternal(playerList));
        return result;
    }

    /**
     * Loads data for the player
     *
     * @param player player to load for
     * @return loaded player data
     */
    public PlayerAccounts loadData(OfflinePlayer player) {
        PlayerAccountsLoadEvent loadEvent = new PlayerAccountsLoadEvent(player);
        Bukkit.getPluginManager().callEvent(loadEvent);
        if(loadEvent.getAccounts() != null) {
            return loadEvent.getAccounts();
        }

        return loadDataInternal(loadEvent.getOfflinePlayer());
    }

    /**
     * Saves the player's data
     *
     * @param data data to save
     */
    public void saveData(PlayerAccounts data) {
        PlayerAccountsSaveEvent saveEvent = new PlayerAccountsSaveEvent(data);
        Bukkit.getPluginManager().callEvent(saveEvent);
        if(!saveEvent.isCancelled()) {
            saveDataInternal(saveEvent.getAccountData());
        }
    }

    protected abstract PlayerAccounts loadDataInternal(OfflinePlayer player);
    protected abstract void saveDataInternal(PlayerAccounts data);

    protected void saveAllInternal(Map<String, PlayerAccounts> accountsMap) {
        for (PlayerAccounts data : accountsMap.values()) {
            if (data.isLoaded() && !MainListener.loadingPlayers.containsKey(data.getOfflinePlayer().getUniqueId())) {
                saveDataInternal(data);
            }
        }
    }

    protected Map<String, PlayerAccounts> loadAllInternal(List<OfflinePlayer> players) {
        Map<String, PlayerAccounts> accountsMap = new HashMap<>();
        for(OfflinePlayer player : players) {
            accountsMap.put(player.getUniqueId().toString().toLowerCase(), loadDataInternal(player));
        }
        return accountsMap;
    }

    /**
     * Saves all player data provided, only call when you know what you are doing
     */
    public void saveAll() {
        Map<String, PlayerAccounts> accountsMap = new HashMap<>();
        for (Map.Entry<String, PlayerAccounts> entry : SkillAPI.getPlayerAccountData().entrySet()) {
            PlayerAccountsSaveEvent saveEvent = new PlayerAccountsSaveEvent(entry.getValue());
            Bukkit.getPluginManager().callEvent(saveEvent);
            if(!saveEvent.isCancelled()) {
                accountsMap.put(entry.getKey(), entry.getValue());
            }
        }
        saveAllInternal(accountsMap);
    }

    /**
     * Loads data from the DataSection for the given player
     *
     * @param player player to load for
     * @param file   DataSection containing the account info
     * @return the loaded player account data
     */
    protected PlayerAccounts load(OfflinePlayer player, DataSection file) {
        PlayerAccounts data     = new PlayerAccounts(player);
        DataSection    accounts = file.getSection(ACCOUNTS);
        if (accounts == null) {
            data.getActiveData().endInit();
            data.isLoaded(true);
            return data;
        }
        for (String accountKey : accounts.keys()) {
            DataSection account = accounts.getSection(accountKey);
            PlayerData  acc     = null;
            try {
                acc = data.getData(Integer.parseInt(accountKey.replace(ACCOUNT_PREFIX, "")), player, true);
            } catch (NumberFormatException e) {
                Logger.bug("Could not parse account key '" + accountKey + "' for player " + player.getUniqueId());
                Logger.bug("This is related to ticket #154. Please paste the player's file and this stack trace.");
                Logger.bug("https://github.com/promcteam/proskillapi/issues/154");
                e.printStackTrace();
            }

            // Load classes
            DataSection classes = account.getSection(CLASSES);
            if (classes != null) {
                for (String classKey : classes.keys()) {
                    RPGClass rpgClass = SkillAPI.getClass(classKey);
                    if (rpgClass != null) {
                        PlayerClass c         = acc.setClass(null, rpgClass, true);
                        DataSection classData = classes.getSection(classKey);
                        int         levels    = classData.getInt(LEVEL);
                        if (levels > 0)
                            c.setLevel(levels);
                        c.setPoints(classData.getInt(POINTS));
                        if (classData.has("total-exp"))
                            c.setExp(classData.getDouble("total-exp") - c.getTotalExp());
                        else
                            c.setExp(classData.getDouble(EXP));
                    }
                }
            }

            // Load skills
            DataSection skills = account.getSection(SKILLS);
            if (skills != null) {
                for (String skillKey : skills.keys()) {
                    DataSection skill     = skills.getSection(skillKey);
                    PlayerSkill skillData = acc.getSkill(skillKey);
                    if (skillData != null) {
                        skillData.setLevel(skill.getInt(LEVEL));
                        skillData.addCooldown(skill.getInt(COOLDOWN, 0));
                    }
                }
            }

            // Load skill bar
            if (SkillAPI.getSettings().isSkillBarEnabled() || SkillAPI.getSettings()
                    .getCastMode()
                    .equals(CastMode.COMBAT)) {
                final DataSection    skillBar = account.getSection(SKILL_BAR);
                final PlayerSkillBar bar      = acc.getSkillBar();
                if (skillBar != null && bar != null) {
                    boolean enabled = skillBar.getBoolean(ENABLED, true);
                    for (final String key : skillBar.keys()) {
                        final boolean[] locked = SkillAPI.getSettings().getLockedSlots();
                        if (key.equals(SLOTS)) {
                            for (int i = 0; i < 9; i++)
                                if (!bar.isWeaponSlot(i) && !locked[i])
                                    bar.getData().remove(i + 1);

                            final List<String> slots = skillBar.getList(SLOTS);
                            for (final String slot : slots) {
                                int i = Integer.parseInt(slot);
                                if (!locked[i - 1])
                                    bar.getData().put(i, UNASSIGNED);
                            }
                        } else if (SkillAPI.getSkill(key) != null)
                            bar.getData().put(skillBar.getInt(key), key);
                    }

                    bar.applySettings();
                }
            }

            // Load combos
            if (SkillAPI.getSettings().isCustomCombosAllowed()) {
                DataSection  combos    = account.getSection(COMBOS);
                PlayerCombos comboData = acc.getComboData();
                ComboManager cm        = SkillAPI.getComboManager();
                if (combos != null && comboData != null) {
                    for (String key : combos.keys()) {
                        Skill skill = SkillAPI.getSkill(key);
                        if (acc.hasSkill(key) && skill != null && skill.canCast()) {
                            int combo = cm.parseCombo(combos.getString(key));
                            if (combo == -1) Logger.invalid("Invalid skill combo: " + combos.getString(key));
                            else comboData.setSkill(skill, combo);
                        }
                    }
                }
            }

            // Load attributes
            if (SkillAPI.getSettings().isAttributesEnabled()) {
                acc.setAttribPoints(account.getInt(ATTRIB_POINTS, 0));
                DataSection attribs = account.getSection(ATTRIBS);
                DataSection attrstages = account.getSection(ATTRSTAGES);
                if (attribs != null) {
                    for (String key : attribs.keys()) {
                        acc.getAttributeData().put(key, attribs.getInt(key));
                    }
                }
                // iomatix: load attrUpStages
                if (attrstages != null) {
                    for (String key : attrstages.keys()) {
                        acc.getAttributeStageData().put(key, attrstages.getInt(key));
                    }
                }
            }

            // Load cast bars
            if (SkillAPI.getSettings().isCastEnabled()) {
                switch (SkillAPI.getSettings().getCastMode()) {
                    case BARS -> {
                        acc.getCastBars().reset();
                        acc.getCastBars().load(account.getSection(HOVER), true);
                        acc.getCastBars().load(account.getSection(INSTANT), false);
                    }
                    case ACTION_BAR, TITLE, SUBTITLE, CHAT ->
                            acc.getTextCastingData().load(account.getSection(TEXT_LAYOUT));
                }
            }

            acc.setHungerValue(account.getDouble(HUNGER, 1));

            // Extra data
            if (account.has(EXTRA) && account.getSection(EXTRA) != null)
                acc.getExtraData().applyDefaults(account.getSection(EXTRA));

            acc.endInit();

            // Load persistent data
            DataSection persistent = account.getSection(PERSISTENT);
            if (persistent != null){
                for (String key : persistent.keys()) {
                    acc.getAllPersistentData().put(key, persistent.getString(key));
                }
            }

        }
        data.setAccount(file.getInt(ACTIVE, data.getActiveId()), false);
        data.getActiveData().setLastHealth(file.getDouble(HEALTH));
        data.getActiveData().setMana(file.getDouble(MANA, data.getActiveData().getMana()));
        data.isLoaded(true);

        return data;
    }

    protected DataSection save(PlayerAccounts data) {
        try {
            DataSection file = new DataSection();
            file.set(LIMIT, data.getAccountLimit());
            file.set(ACTIVE, data.getActiveId());
            file.set(HEALTH, data.getActiveData().getLastHealth());
            file.set(MANA, data.getActiveData().getMana());
            DataSection accounts = file.createSection(ACCOUNTS);
            for (Map.Entry<Integer, PlayerData> entry : data.getAllData().entrySet()) {
                DataSection account = accounts.createSection(ACCOUNT_PREFIX + entry.getKey());
                PlayerData  acc     = entry.getValue();

                // Save classes
                DataSection classes = account.createSection(CLASSES);
                for (PlayerClass c : acc.getClasses()) {
                    DataSection classSection = classes.createSection(c.getData().getName());
                    classSection.set(LEVEL, c.getLevel());
                    classSection.set(POINTS, c.getPoints());
                    classSection.set(EXP, c.getExp());
                }

                // Save skills
                DataSection skills = account.createSection(SKILLS);
                for (PlayerSkill skill : acc.getSkills()) {
                    if (skill.isExternal()) {
                        continue;
                    }
                    DataSection skillSection = skills.createSection(skill.getData().getName());
                    skillSection.set(LEVEL, skill.getLevel());
                    if (skill.isOnCooldown())
                        skillSection.set(COOLDOWN, skill.getCooldown());
                }

                // Save binds
                DataSection binds = account.createSection(BINDS);
                for (Map.Entry<Material, PlayerSkill> bind : acc.getBinds().entrySet()) {
                    if (bind.getKey() == null || bind.getValue() == null) continue;
                    binds.set(bind.getKey().name(), bind.getValue().getData().getName());
                }

                // Save skill bar
                if ((SkillAPI.getSettings().isSkillBarEnabled() || SkillAPI.getSettings()
                        .getCastMode()
                        .equals(CastMode.COMBAT))
                        && acc.getSkillBar() != null) {
                    DataSection    skillBar = account.createSection(SKILL_BAR);
                    PlayerSkillBar bar      = acc.getSkillBar();
                    skillBar.set(ENABLED, bar.isEnabled());
                    skillBar.set(SLOTS, new ArrayList<>(bar.getData().keySet()));
                    for (Map.Entry<Integer, String> slotEntry : bar.getData().entrySet()) {
                        if (slotEntry.getValue().equals(UNASSIGNED)) {
                            continue;
                        }
                        skillBar.set(slotEntry.getValue(), slotEntry.getKey());
                    }
                }

                // Save combos
                if (SkillAPI.getSettings().isCustomCombosAllowed()) {
                    DataSection  combos    = account.createSection(COMBOS);
                    PlayerCombos comboData = acc.getComboData();
                    ComboManager cm        = SkillAPI.getComboManager();
                    if (combos != null && comboData != null) {
                        HashMap<Integer, String> comboMap = comboData.getSkillMap();
                        for (Map.Entry<Integer, String> combo : comboMap.entrySet()) {
                            combos.set(combo.getValue(), cm.getSaveString(combo.getKey()));
                        }
                    }
                }

                // Save attributes
                if (SkillAPI.getSettings().isAttributesEnabled()) {
                    account.set(ATTRIB_POINTS, acc.getAttributePoints());
                    DataSection attribs = account.createSection(ATTRIBS);
                    DataSection attrstages = account.createSection(ATTRSTAGES);
                    for (String key : acc.getAttributeData().keySet()) {
                        attribs.set(key, acc.getAttributeData().get(key));
                    }
                    // iomatix Save attrUpStages
                    for (String key : acc.getAttributeStageData().keySet()){
                        attrstages.set(key, acc.getAttributeStageData().get(key));
                    }
                }

                // Save cast bars
                if (SkillAPI.getSettings().isCastEnabled()) {
                    switch (SkillAPI.getSettings().getCastMode()) {
                        case BARS -> {
                            acc.getCastBars().save(account.createSection(HOVER), true);
                            acc.getCastBars().save(account.createSection(INSTANT), false);
                        }
                        case ACTION_BAR, TITLE, SUBTITLE, CHAT ->
                                acc.getTextCastingData().save(account.createSection(TEXT_LAYOUT));
                    }
                }

                account.set(HUNGER, acc.getHungerValue());

                // Save persistent data
                DataSection persistentData = account.createSection(PERSISTENT);
                acc.getAllPersistentData().forEach(persistentData::set);

                // Extra data
                if (acc.getExtraData().size() > 0) {
                    account.set(EXTRA, acc.getExtraData());
                }
            }
            return file;
        } catch (Exception ex) {
            Logger.bug("Failed to save player data for " + data.getPlayer().getName());
            ex.printStackTrace();
            return null;
        }
    }
}
