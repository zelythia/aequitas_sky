package net.zelythia.aequitas_sky.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.zelythia.aequitas_sky.AequitasSky;

public class SpawnPlatformFeature extends Feature<SpawnPlatformFeatureConfiguration> {
    public SpawnPlatformFeature(Codec<SpawnPlatformFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<SpawnPlatformFeatureConfiguration> context) {
        SpawnPlatformFeatureConfiguration config = context.getConfig();
        // Always absolute with Y
        BlockPos origin = config.spawnRelative() ? context.getOrigin().withY(0) : BlockPos.ORIGIN;

        StructureWorldAccess level = context.getWorld();
        MinecraftServer server = level.getServer();
        if (server == null) {
            return false;
        }
        StructureTemplate structure = server.getStructureTemplateManager().getTemplate(config.structure()).orElse(null);
        if (structure == null) {
            AequitasSky.LOGGER.warn("Missing structure " + config.structure());
            return false;
        }

        return structure.place(level, origin.add(config.pos()), null, new StructurePlacementData(), context.getRandom(), Block.NOTIFY_ALL);
    }

}
