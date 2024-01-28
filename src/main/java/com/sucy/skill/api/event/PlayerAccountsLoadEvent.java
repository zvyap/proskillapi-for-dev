package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerAccounts;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerAccountsLoadEvent extends Event {
    private static final HandlerList handlers  = new HandlerList();
    private OfflinePlayer player;
    private PlayerAccounts accounts;
    private              boolean        cancelled = false;

    /**
     * Constructor
     *
     * @param player to retrieve accounts data
     */
    public PlayerAccountsLoadEvent(@NotNull OfflinePlayer player) {
        super(true);
        this.player = player;
    }

    /**
     * @return player's account data
     */
    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    /**
     * Set the target offline player
     *
     * @param player to retrieve accounts data
     */
    public void setOfflinePlayer(@NotNull OfflinePlayer player) {
        Objects.requireNonNull(player);
        this.player = player;
    }

    /**
     * Override the player data
     */
    public void setAccounts(PlayerAccounts accounts) {
        this.accounts = accounts;
    }

    /**
     * @return player's account data provided by other source,
     *         if the account is exist, the plugin will not retrieves data from storage
     */
    public PlayerAccounts getAccounts() {
        return accounts;
    }

    /**
     * @return gets the handlers for the event
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}