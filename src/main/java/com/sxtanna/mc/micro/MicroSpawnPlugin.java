package com.sxtanna.mc.micro;

import co.aikar.commands.PaperCommandManager;
import com.sxtanna.mc.micro.cmds.CommandSpawn;
import com.sxtanna.mc.micro.cmds.CommandSpawnAdmin;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.Objects;
import java.util.Optional;

public final class MicroSpawnPlugin extends JavaPlugin implements Listener {

    private static MicroSpawnPlugin instance;

    public static @NotNull MicroSpawnPlugin getInstance() {
        return Objects.requireNonNull(instance, "plugin is not initialized");
    }


    public static @NotNull Logger logger() {
        return getInstance().getSLF4JLogger();
    }


    private PaperCommandManager commandManager;


    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.enableUnstableAPI("help");
        this.commandManager.enableUnstableAPI("brigadier");

        this.commandManager.registerCommand(new CommandSpawn());
        this.commandManager.registerCommand(new CommandSpawnAdmin());

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        this.commandManager.unregisterCommands();
        this.commandManager = null;

        HandlerList.unregisterAll(((Plugin) this));

        instance = null;
    }


    public @NotNull Optional<Location> getSpawnLocation() {
        return Optional.of(getConfig())
                       .map(config -> config.getLocation("spawn", null))
                       .filter(Location::isWorldLoaded);
    }

    public void setSpawnLocation(@Nullable final Location location) {
        final var config = getConfig();

        config.set("spawn", location);

        saveConfig();
    }


    public boolean isForcingSpawnOnJoin() {
        return getConfig().getBoolean("settings.force_spawn_on_join", false);
    }

    public void setForcingSpawnOnJoin(final boolean state) {
        final var config = getConfig();

        config.set("settings.force_spawn_on_join", state);

        saveConfig();
    }


    @EventHandler
    private void onPlayerJoin(@NotNull final PlayerSpawnLocationEvent event) {
        if (!isForcingSpawnOnJoin() || event.getPlayer().hasPermission("micro.spawn.force_bypass")) {
            return;
        }

        getSpawnLocation().ifPresent(event::setSpawnLocation);
    }

}
