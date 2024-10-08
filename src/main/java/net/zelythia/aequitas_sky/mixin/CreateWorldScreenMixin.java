package net.zelythia.aequitas_sky.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.WorldPreset;
import net.zelythia.aequitas_sky.AequitasSky;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(value = EnvType.CLIENT)
@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Redirect(method = "create(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/gen/WorldPresets;DEFAULT:Lnet/minecraft/registry/RegistryKey;"))
    private static RegistryKey<WorldPreset> setDefaultSelectedWorldPreset() {
        return AequitasSky.SKYBLOCK_WORLD_PRESET;
    }


    @Redirect(
            method = "method_45686",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/WorldPresets;createDemoOptions(Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/world/dimension/DimensionOptionsRegistryHolder;"
            ))
    private static DimensionOptionsRegistryHolder setDefaultWorldGenSettings(DynamicRegistryManager dynamicRegistryManager) {
        return dynamicRegistryManager.get(RegistryKeys.WORLD_PRESET)
                .getOrThrow(AequitasSky.SKYBLOCK_WORLD_PRESET)
                .createDimensionsRegistryHolder();
    }

}
