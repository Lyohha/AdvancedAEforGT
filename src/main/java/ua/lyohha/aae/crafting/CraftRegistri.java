package ua.lyohha.aae.crafting;

import appeng.api.AEApi;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;

public class CraftRegistri {
    public static void Registri() {
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(AdvancedAEBlocks.advancedStorageMEInterface, 1), new Object[]{"XYX", "TZT", "TYT", ('X'), "chestWood", ('Y'), "circuitAdvanced", ('Z'), "craftingPiston", ('T'), "ingotIron"}));
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(AdvancedAEBlocks.advancedPatternTerminal, 1), new Object[]{" X ", "ZYZ", "ZZZ", ('X'), "craftingWorkBench", ('Y'), "chestWood", ('Z'), "ingotIron"}));
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(AdvancedAEBlocks.multiblockMEInterfaceHatch, 1), new Object[]{"   ", "TZT", "TTT", ('Z'), "craftingPiston", ('T'), "ingotIron"}));
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(AdvancedAEBlocks.multiblockMEInterfaceController, 1), new Object[]{"X X", "YZY", "X X", ('Z'), "chestWood", ('Y'), "circuitAdvanced", ('X'), "ingotIron"}));

    }
}
