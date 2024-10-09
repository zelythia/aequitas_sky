package net.zelythia.aequitas_sky.compat;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiStack;
import dev.ftb.mods.ftbxmodcompat.ftbquests.recipemod_common.BaseRecipeHelper;
import net.minecraft.item.ItemStack;

public class FTBQuestsEMIRecipeHelper extends BaseRecipeHelper {
    @Override
    public void showRecipes(ItemStack itemStack) {
        EmiApi.displayRecipes(EmiStack.of(itemStack));
    }

    @Override
    public String getHelperName() {
        return "EMI";
    }

    protected void refreshQuests() {

    }

    protected void refreshLootcrates() {

    }
}
