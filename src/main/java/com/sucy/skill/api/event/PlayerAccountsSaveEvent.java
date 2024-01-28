package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerAccounts;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAccountsSaveEvent extends Event implements Cancellable {
    private static final HandlerList handlers  = new HandlerList();
    private PlayerAccounts accounts;
    private              boolean        cancelled = false;

    /**
     * Constructor
     *
     * @param accounts player accounts data
     */
    public PlayerAccountsSaveEvent(PlayerAccounts accounts) {
        super(true);
        this.accounts = accounts;
    }

    /**
     * @return player's account data
     */
    public PlayerAccounts getAccountData() {
        return accounts;
    }

    /**
     * Checks whether the event is cancelled
     *
     * @return true if cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether the switch should be cancelled
     *
     * @param cancelled cancelled state of the event
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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