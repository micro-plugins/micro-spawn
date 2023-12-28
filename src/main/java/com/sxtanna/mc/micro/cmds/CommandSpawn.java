package com.sxtanna.mc.micro.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.sxtanna.mc.micro.MicroSpawnPlugin;
import com.sxtanna.mc.micro.lang.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("spawn")
@CommandPermission("micro.spawn.spawn")
public final class CommandSpawn extends BaseCommand {

    @Default
    public void spawn(@NotNull final Player sender) {
        final var plugin = MicroSpawnPlugin.getInstance();

        plugin.getSpawnLocation()
              .ifPresentOrElse(spawn -> sender.teleportAsync(spawn).whenComplete((teleported, error) -> {
                                   if (teleported) {
                                       Messages.TELEPORT_SUCCESS.send(sender);
                                   } else {
                                       Messages.TELEPORT_FAILURE.send(sender);

                                       if (error != null) {
                                           MicroSpawnPlugin.logger()
                                                           .error("failed to teleport [{}]", sender.getName(), error);
                                       }
                                   }
                               }),
                               () -> Messages.LOCATION_NOT_SET.send(sender));
    }

}
