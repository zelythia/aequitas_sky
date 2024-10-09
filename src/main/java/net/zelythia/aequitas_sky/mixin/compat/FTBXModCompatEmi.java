package net.zelythia.aequitas_sky.mixin.compat;


import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbxmodcompat.FTBXModCompat;
import dev.ftb.mods.ftbxmodcompat.ftbquests.FTBQuestsSetup;
import dev.ftb.mods.ftbxmodcompat.ftbquests.filtering.ItemFilteringSetup;
import net.fabricmc.loader.api.FabricLoader;
import net.zelythia.aequitas_sky.compat.FTBQuestsEMIRecipeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FTBQuestsSetup.class)
public class FTBXModCompatEmi {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true, remap = false)
    private static void EmiIntegration(CallbackInfo ci) {

        if (FabricLoader.getInstance().isModLoaded("emi")) {
            FTBQuests.setRecipeModHelper(new FTBQuestsEMIRecipeHelper());
            FTBXModCompat.LOGGER.info("[FTB Quests] recipe helper provider is [{}]", FTBQuests.getRecipeModHelper().getHelperName());
            ItemFilteringSetup.init();
            ci.cancel();
        }
    }

}
