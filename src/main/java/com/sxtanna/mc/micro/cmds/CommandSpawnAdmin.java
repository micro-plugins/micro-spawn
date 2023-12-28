package com.sxtanna.mc.micro.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import com.sxtanna.mc.micro.MicroSpawnPlugin;
import com.sxtanna.mc.micro.lang.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;

import static java.util.Map.of;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@CommandAlias("uSpawn")
@CommandPermission("micro.spawn.admin")
public class CommandSpawnAdmin extends BaseCommand {

    @Default
    @HelpCommand
    public void help(@NotNull final CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("set|setspawn")
    @CommandPermission("micro.spawn.admin.set")
    public void set_spawn(@NotNull final Player sender) {
        final var location = sender.getLocation();

        MicroSpawnPlugin.getInstance()
                        .setSpawnLocation(location);

        Messages.LOCATION_UPDATED.send(sender,
                                       of("location", formatLocation(sender.locale(), location)));
    }

    @Subcommand("del|delspawn")
    @CommandPermission("micro.spawn.admin.del")
    public void del_spawn(@NotNull final Player sender) {
        final var plugin = MicroSpawnPlugin.getInstance();

        plugin.getSpawnLocation()
              .ifPresentOrElse(previous -> {
                                   plugin.setSpawnLocation(null);

                                   Messages.LOCATION_REMOVED
                                           .send(sender,
                                                 of("location", formatLocation(sender.locale(), previous)));
                               },
                               () -> Messages.LOCATION_NOT_SET.send(sender));
    }


    @Subcommand("force_spawn")
    @CommandPermission("micro.spawn.admin.force")
    public void toggle_force_spawn(@NotNull final CommandSender sender) {
        final var plugin = MicroSpawnPlugin.getInstance();

        plugin.setForcingSpawnOnJoin(!plugin.isForcingSpawnOnJoin());

        if (plugin.isForcingSpawnOnJoin()) {
            Messages.FORCE_SPAWN_ON_JOIN_ENABLED.send(sender);
        } else {
            Messages.FORCE_SPAWN_ON_JOIN_DISABLED.send(sender);
        }
    }

    @Subcommand("reload")
    @CommandPermission("micro.spawn.admin.reload")
    public void reload(@NotNull final CommandSender sender) {
        final var plugin = MicroSpawnPlugin.getInstance();

        plugin.reloadConfig();

        Messages.CONFIG_RELOADED.send(sender);
    }


    private static @NotNull Component formatLocation(@NotNull final Locale locale, @NotNull final Location location) {
        final var format = NumberFormat.getNumberInstance(locale);
        format.setGroupingUsed(true);
        format.setMaximumFractionDigits(2);

        return text(location.getWorld().getName())
                .color(AQUA)
                .append(space())
                .append(text("[")
                                .color(DARK_GRAY))
                .append(text("x: ")
                                .color(GRAY))
                .append(text(format.format(location.x()))
                                .color(GREEN))

                .append(text(", ")
                                .color(DARK_GRAY))

                .append(text("y: ")
                                .color(GRAY))
                .append(text(format.format(location.y()))
                                .color(GREEN))

                .append(text(", ")
                                .color(DARK_GRAY))

                .append(text("z: ")
                                .color(GRAY))
                .append(text(format.format(location.z()))
                                .color(GREEN))

                .append(text("]")
                                .color(DARK_GRAY));
    }

}
