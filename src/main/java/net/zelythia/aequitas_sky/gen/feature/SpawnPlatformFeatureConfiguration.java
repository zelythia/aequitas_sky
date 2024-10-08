package net.zelythia.aequitas_sky.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeatureConfig;

public record SpawnPlatformFeatureConfiguration(Identifier structure, BlockPos pos, boolean spawnRelative) implements FeatureConfig {
    public static final Codec<SpawnPlatformFeatureConfiguration> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                            Identifier.CODEC.fieldOf("structure").forGetter(config -> config.structure),
                            BlockPos.CODEC.fieldOf("pos").forGetter(config -> config.pos),
                            Codec.BOOL.fieldOf("spawn_relative").forGetter(config -> config.spawnRelative))
                    .apply(instance, SpawnPlatformFeatureConfiguration::new));
}
