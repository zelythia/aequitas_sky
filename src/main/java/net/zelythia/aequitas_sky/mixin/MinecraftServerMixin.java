package net.zelythia.aequitas_sky.mixin;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.level.ServerWorldProperties;
import net.zelythia.aequitas_sky.AequitasSky;
import net.zelythia.aequitas_sky.gen.SkyblockChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "setupSpawn", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerWorldProperties;setSpawnPos(Lnet/minecraft/util/math/BlockPos;F)V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    private static void generateSpawnPlatform(ServerWorld world, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld, CallbackInfo ci, ServerChunkManager serverChunkManager, ChunkPos chunkPos, int i) {
        ServerChunkManager chunkManager = world.getChunkManager();
        ChunkGenerator chunkGenerator = chunkManager.getChunkGenerator();
        if (!(chunkGenerator instanceof SkyblockChunkGenerator)) return;
        BlockPos worldSpawn = chunkPos.getCenterAtY(i);

        ConfiguredFeature<?, ?> spawnPlatformFeature = world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).getOrThrow(AequitasSky.SPAWN_PLATFORM_FEATURE);
        if (!spawnPlatformFeature.generate(world, chunkGenerator, world.random, worldSpawn)) {
            AequitasSky.LOGGER.error("Couldn't generate spawn platform");
        }

        ci.cancel();
    }
}
