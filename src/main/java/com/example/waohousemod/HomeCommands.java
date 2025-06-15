package com.example.waohousemod;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommands {
    private static final Map<UUID, SavedHome> HOMES = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setcasa")
            .requires(source -> source.hasPermission(0))
            .executes(ctx -> setHome(ctx.getSource())));

        dispatcher.register(Commands.literal("casa")
            .requires(source -> source.hasPermission(0))
            .executes(ctx -> goHome(ctx.getSource())));
    }

    private static int setHome(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            return 0;
        }
        SavedHome home = new SavedHome(player.level().dimension(), player.blockPosition());
        HOMES.put(player.getUUID(), home);
        player.sendSystemMessage(Component.literal("Casa guardada!"));
        return 1;
    }

    private static int goHome(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            return 0;
        }
        SavedHome home = HOMES.get(player.getUUID());
        if (home == null) {
            player.sendSystemMessage(Component.literal("Tu eres marico? todavia no hay casa"));
            return 1;
        }
        ServerLevel level = player.server.getLevel(home.dimension);
        if (level == null) {
            player.sendSystemMessage(Component.literal("Tu eres marico? todavia no hay casa"));
            return 1;
        }
        player.sendSystemMessage(Component.literal("Teletrasportando..."));
        BlockPos pos = home.pos;
        player.teleportTo(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        return 1;
    }

    private record SavedHome(ResourceKey<Level> dimension, BlockPos pos) {}
}
