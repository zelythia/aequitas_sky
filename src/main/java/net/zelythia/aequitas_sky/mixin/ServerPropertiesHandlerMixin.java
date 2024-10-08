package net.zelythia.aequitas_sky.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.world.gen.WorldPreset;
import net.zelythia.aequitas_sky.AequitasSky;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPropertiesHandler.class)
public class ServerPropertiesHandlerMixin {
    @Redirect(method = "<init>", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/gen/WorldPresets;DEFAULT:Lnet/minecraft/registry/RegistryKey;"))
    private RegistryKey<WorldPreset> setDefaultSelectedWorldPreset() {
        return AequitasSky.SKYBLOCK_WORLD_PRESET;
    }
}
