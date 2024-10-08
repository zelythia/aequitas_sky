package net.zelythia.aequitas_sky.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {

    @Accessor
    Supplier<List<PlacedFeatureIndexer.IndexedFeatures>> getIndexedFeaturesListSupplier();

    @Accessor
    Function<RegistryEntry<Biome>, GenerationSettings> getGenerationSettingsGetter();

    @Invoker("getBlockBoxForChunk")
    static BlockBox getBlockBoxForChunk(Chunk chunk) {
        throw new AssertionError();
    }
}
