package com.lefoxxy.lightmatters.command;

import com.lefoxxy.lightmatters.LightMattersMod;
import com.lefoxxy.lightmatters.gameplay.DarknessProfile;
import com.lefoxxy.lightmatters.gameplay.DarknessSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = LightMattersMod.MODID)
public final class LightMattersCommands {
    private LightMattersCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("lightmatters")
                .then(Commands.literal("debug")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(LightMattersCommands::runDebug)));
    }

    private static int runDebug(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        DarknessProfile profile = DarknessProfile.sample(player);
        int exposureTicks = DarknessSystem.getPitchBlackExposureTicks(player);
        double exposureSeconds = exposureTicks / 20.0D;

        context.getSource().sendSuccess(() -> Component.literal(
                "Light Matters debug | stage=" + profile.stage().name()
                        + ", effective=" + profile.effectiveLight()
                        + ", block=" + profile.blockLight()
                        + ", rawSky=" + profile.rawSkyLight()
                        + ", sky=" + profile.skyLight()
                        + ", outdoorPenalty=" + profile.outdoorPenalty()
                        + ", personal=" + profile.personalLight()
                        + ", canSeeSky=" + profile.canSeeSky()
                        + ", tier=" + profile.ambientTier().name()
                        + ", pitchBlackExposureTicks=" + exposureTicks
                        + ", pitchBlackExposureSeconds=" + String.format("%.1f", exposureSeconds)),
                false);
        return 1;
    }
}
