package com.sucy.skill.data.io;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoidIO extends IOManager{

    private static final DataSection EMPTY_DATA_SECTION = new DataSection();

    /**
     * Initializes a new IO manager
     *
     * @param api SkillAPI reference
     */
    public VoidIO(SkillAPI api) {
        super(api);
    }

    @Override
    protected PlayerAccounts loadDataInternal(OfflinePlayer player) {
        return load(player, EMPTY_DATA_SECTION);
    }

    @Override
    protected void saveDataInternal(PlayerAccounts data) {

    }
}
