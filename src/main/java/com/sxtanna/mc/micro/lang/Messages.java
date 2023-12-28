package com.sxtanna.mc.micro.lang;

import com.sxtanna.mc.micro.MicroSpawnPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public enum Messages {


    TELEPORT_SUCCESS("teleport.success"),
    TELEPORT_FAILURE("teleport.failure"),

    LOCATION_UPDATED("location.updated"),
    LOCATION_REMOVED("location.removed"),
    LOCATION_NOT_SET("location.not_set"),

    FORCE_SPAWN_ON_JOIN_ENABLED("force_spawn_on_join.enabled"),
    FORCE_SPAWN_ON_JOIN_DISABLED("force_spawn_on_join.disabled"),

    CONFIG_RELOADED("config_reloaded");


    @NotNull
    private final String key;


    Messages(@NotNull final String key) {
        this.key = key;
    }


    public final @NotNull String getMessage() {
        return MicroSpawnPlugin.getInstance().getConfig().getString("messages." + this.key, "");
    }

    public final @NotNull Component getMessageComponent() {
        return getMessageComponent(Collections.emptyMap());
    }


    public final @NotNull Component getMessageComponent(@NotNull final Map<String, Object> replacements) {
        if (replacements.isEmpty()) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(getMessage());
        }

        final var parts = new ArrayList<Object>(Arrays.asList(getMessage().split(" ")));

        for (final var entry : replacements.entrySet()) {
            final var k = entry.getKey();
            final var v = entry.getValue();

            final var placeholder = "%" + k + "%";
            final var replacement = v instanceof Component component ?
                                    component :
                                    LegacyComponentSerializer.legacyAmpersand().deserialize(v.toString());


            for (int i = 0; i < parts.size(); i++) {
                if (!(parts.get(i) instanceof String text) || !text.contains(placeholder)) {
                    continue;
                }

                parts.set(i, replacement);
            }
        }

        final var builder = new ArrayList<String>();
        final var message = new ArrayList<Component>();

        for (final var part : parts) {
            if (part instanceof String text) {
                builder.add(text);
            } else if (part instanceof Component component) {
                message.add(LegacyComponentSerializer.legacyAmpersand().deserialize(String.join(" ", builder)));
                builder.clear();
                message.add(component);
            }
        }

        if (!builder.isEmpty()) {
            message.add(LegacyComponentSerializer.legacyAmpersand().deserialize(String.join(" ", builder)));
            builder.clear();
        }

        return Component.join(JoinConfiguration.separator(Component.space()), message);
    }


    public final void send(@NotNull final Audience audience) {
        audience.sendMessage(getMessageComponent());
    }

    public final void send(@NotNull final Audience audience, @NotNull final Map<String, Object> replacements) {
        audience.sendMessage(getMessageComponent(replacements));
    }

}
