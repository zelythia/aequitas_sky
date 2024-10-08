package net.zelythia.aequitas_sky.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.NetherFortressStructure;
import net.minecraft.world.gen.structure.Structure;
import net.zelythia.aequitas_sky.mixin.ChunkGeneratorAccessor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SkyblockChunkGenerator extends NoiseChunkGenerator {
    public SkyblockChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    public static final Codec<SkyblockChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(SkyblockChunkGenerator::getBiomeSource),
                            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(SkyblockChunkGenerator::getSettings))
                    .apply(instance, instance.stable(SkyblockChunkGenerator::new)));

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig random, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {

    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        ChunkPos chunkPos = chunk.getPos();
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, world.getBottomSectionCoord());
        BlockPos minChunkPos = chunkSectionPos.getMinPos();

        Registry<Structure> structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
        Map<Integer, List<Structure>> structuresPerStep = structureRegistry.stream().collect(Collectors.groupingBy(structureType -> structureType.getFeatureGenerationStep().ordinal()));
        List<PlacedFeatureIndexer.IndexedFeatures> featuresPerStep = ((ChunkGeneratorAccessor) this).getIndexedFeaturesListSupplier().get();

        ChunkRandom random = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
        long decorationSeed = random.setPopulationSeed(world.getSeed(), minChunkPos.getX(), minChunkPos.getZ());

        // Get all surrounding biomes for biome-based structures
        Set<RegistryEntry<Biome>> biomeSet = new ObjectArraySet<>();
        ChunkPos.stream(chunkSectionPos.toChunkPos(), 1).forEach(curChunkPos -> {
            Chunk curChunk = world.getChunk(curChunkPos.x, curChunkPos.z);
            for (ChunkSection chunkSection : curChunk.getSectionArray()) {
                chunkSection.getBiomeContainer().forEachValue(biomeSet::add);
            }
        });
        biomeSet.retainAll(biomeSource.getBiomes());

        int numFeatures = featuresPerStep.size();
        try {
            Registry<PlacedFeature> placedFeatures = world.getRegistryManager().get(RegistryKeys.PLACED_FEATURE);
            int numSteps = Math.max(GenerationStep.Feature.values().length, numFeatures);
            for (int genStep = 0; genStep < numSteps; ++genStep) {
                int structureInStep = 0;
                if (structureAccessor.shouldGenerateStructures()) {
                    List<Structure> structuresForStep = structuresPerStep.getOrDefault(genStep, Collections.emptyList());

                    for (Structure structure : structuresForStep) {
                        random.setDecoratorSeed(decorationSeed, structureInStep, genStep);
                        Supplier<String> structureNameSupplier = () -> structureRegistry.getKey(structure).map(Object::toString).orElseGet(structure::toString);
                        try {

                            if (structure instanceof NetherFortressStructure) {
                                world.setCurrentlyGeneratingStructureName(structureNameSupplier);

                                structureAccessor.getStructureStarts(chunkSectionPos, structure).forEach(start -> {
                                    start.place(world, structureAccessor, this, random, ChunkGeneratorAccessor.getBlockBoxForChunk(chunk), chunkPos);
                                });
                            }

                        } catch (Exception e) {
                            CrashReport crashReport = CrashReport.create(e, "Feature placement");
                            crashReport.addElement("Feature").add("Description", structureNameSupplier::get);
                            throw new CrashException(crashReport);
                        }
                        ++structureInStep;
                    }
                }
                if (genStep >= numFeatures) continue;
                IntSet intSet = new IntArraySet();
                for (RegistryEntry<Biome> biome : biomeSet) {
                    List<RegistryEntryList<PlacedFeature>> biomeFeatureStepList = ((ChunkGeneratorAccessor) this).getGenerationSettingsGetter().apply(biome).getFeatures();
                    if (genStep < biomeFeatureStepList.size()) {
                        RegistryEntryList<PlacedFeature> biomeFeaturesForStep = biomeFeatureStepList.get(genStep);
                        PlacedFeatureIndexer.IndexedFeatures indexedFeature = featuresPerStep.get(genStep);
                        biomeFeaturesForStep.stream().map(RegistryEntry::value).forEach(placedFeature -> intSet.add(indexedFeature.indexMapping().applyAsInt(placedFeature)));
                    }
                }
                int n = intSet.size();
                int[] is = intSet.toIntArray();
                Arrays.sort(is);
                PlacedFeatureIndexer.IndexedFeatures indexedFeature = featuresPerStep.get(genStep);
                for (int o = 0; o < n; ++o) {
                    int p = is[o];
                    PlacedFeature placedFeature = indexedFeature.features().get(p);
                    Supplier<String> placedFeatureNameSupplier = () -> placedFeatures.getKey(placedFeature).map(Object::toString).orElseGet(placedFeature::toString);
                    random.setDecoratorSeed(decorationSeed, p, genStep);
                    try {
                        // Random End Gateways
//                        if (SkyAdditionsSettings.generateRandomEndGateways && placedFeature.feature().is(new ResourceLocation("end_gateway_return"))) {
//                            level.setCurrentlyGenerating(placedFeatureNameSupplier);
//                            placedFeature.placeWithBiomeCheck(level, this, random, minChunkPos);
//                        }
                    } catch (Exception e) {
                        CrashReport crashReport = CrashReport.create(e, "Feature placement");
                        crashReport.addElement("Feature").add("Description", placedFeatureNameSupplier::get);
                        throw new CrashException(crashReport);
                    }
                }
            }
            world.setCurrentlyGeneratingStructureName(null);
        } catch (Exception e) {
            CrashReport crashReport = CrashReport.create(e, "Biome decoration");
            crashReport.addElement("Generation").add("CenterX", chunkPos.x).add("CenterZ", chunkPos.z).add("Seed", decorationSeed);
            throw new CrashException(crashReport);
        }
    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

//    public int getBaseHeightInEquivalentNoiseWorld(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
//
//        NoiseConfig randomState = NoiseConfig.create(
//                generatorSettings().value(),
//                level.registryAccess().registryOrThrow(Registries.NOISE).asLookup(),
//                level.getSeed());
//        return super.getHeight(x, z, heightmap, world, randomState);
//    }
}
