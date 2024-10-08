package net.zelythia.aequitas_sky;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.zelythia.aequitas_sky.gen.SkyblockChunkGenerator;
import net.zelythia.aequitas_sky.gen.feature.SpawnPlatformFeature;
import net.zelythia.aequitas_sky.gen.feature.SpawnPlatformFeatureConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AequitasSky implements ModInitializer {
    public static final String MOD_ID = "aequitas_sky";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();


    public static final RegistryKey<WorldPreset> SKYBLOCK_WORLD_PRESET = RegistryKey.of(RegistryKeys.WORLD_PRESET, new Identifier(AequitasSky.MOD_ID, "skyblock"));

    public static final RegistryKey<ConfiguredFeature<?, ?>> SPAWN_PLATFORM_FEATURE = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(AequitasSky.MOD_ID, "spawn_platform"));

    public static final Feature<SpawnPlatformFeatureConfiguration> SPAWN_PLATFORM = new SpawnPlatformFeature(SpawnPlatformFeatureConfiguration.CODEC);



    public static final Block CRUSHED_PRIMAL_ESSENCE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "crushed_primal_essence_block"), new Block(AbstractBlock.Settings.copy(Blocks.SAND)));
    public static final Item CRUSHED_PRIMAL_ESSENCE_BLOCK_ITEM = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "crushed_primal_essence_block"), new BlockItem(CRUSHED_PRIMAL_ESSENCE_BLOCK, new Item.Settings()));

    public static final Item PETRIFIED_ESSENCE_PEBBLE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "petrified_essence_pebble"), new Item(new Item.Settings()));

    @Override
    public void onInitialize() {
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(MOD_ID, "skyblock"), SkyblockChunkGenerator.CODEC);
        Registry.register(Registries.FEATURE, new Identifier(MOD_ID, "spawn_platform"), SPAWN_PLATFORM);

        //not needed anymore
//        if(!ResourceManagerHelper.registerBuiltinResourcePack(
//                new Identifier(MOD_ID, "skyblock"),
//                MOD_CONTAINER,
//				Text.literal("Skyblock"),
//                ResourcePackActivationType.DEFAULT_ENABLED)){
//            LOGGER.warn("Could not register built-in datapack \"skyblock\".");
//        }
    }
}