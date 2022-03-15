package ua.lyohha.aae.crafting;

import appeng.api.AEApi;
import appeng.api.recipes.IRecipeHandler;
import appeng.api.recipes.IRecipeLoader;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;
import ua.lyohha.aae.items.AdvancedAEItems;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CraftRegistry {
    private class InternalRecipeLoader implements IRecipeLoader {

        @Override
        public BufferedReader getFile(String path) throws Exception {
            InputStream resourceAsStream = getClass().getResourceAsStream("/assets/aae/recipes/" + path);
            InputStreamReader reader = new InputStreamReader(resourceAsStream, "UTF-8");
            return new BufferedReader(reader);
        }
    }

    public void MCStyleRegistry() {
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(AdvancedAEBlocks.advancedStorageMEInterface, 1), new Object[]{"XYX", "TZT", "TYT", ('X'), "chestWood", ('Y'), "circuitAdvanced", ('Z'), "craftingPiston", ('T'), "ingotIron"}));
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(AdvancedAEBlocks.advancedPatternTerminal, 1), new Object[]{" X ", "ZYZ", "ZZZ", ('X'), "craftingWorkBench", ('Y'), "chestWood", ('Z'), "ingotIron"}));
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(AdvancedAEBlocks.multiblockMEInterfaceHatch, 1), new Object[]{"   ", "TZT", "TTT", ('Z'), "craftingPiston", ('T'), "ingotIron"}));
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(AdvancedAEBlocks.multiblockMEInterfaceController, 1), new Object[]{"X X", "YZY", "X X", ('Z'), "chestWood", ('Y'), "circuitAdvanced", ('X'), "ingotIron"}));
        GameRegistry.addShapelessRecipe(new ItemStack(AdvancedAEBlocks.advancedStorageMEInterface), new Object[]{AdvancedAEBlocks.advancedStorageMEInterface});

        FMLControlledNamespacedRegistry<Item> itemRegistry = GameData.getItemRegistry();
        FMLControlledNamespacedRegistry<Block> blockRegistry = GameData.getBlockRegistry();

        Item gregComponents = itemRegistry.getObject("gregtech:gt.metaitem.01");
        Block meIterface = blockRegistry.getObject("appliedenergistics2:tile.BlockInterface");

        if(gregComponents == null)
            AdvancedAE.logger.error("Not Found GregTech Components");

        if(meIterface == null)
            AdvancedAE.logger.error("Not Found AE2 Components");

        if(gregComponents != null && meIterface != null) {
            GameRegistry.addRecipe
            (
                new ShapedOreRecipe(
                    new ItemStack(AdvancedAEItems.partGregTechStorageBus, 1),
                    new Object[]{"EMS", "ICI", "   ",
                            ('M'), new ItemStack(meIterface, 1),
                            ('E'), new ItemStack(gregComponents, 1, 32683),
                            ('S'), new ItemStack(gregComponents, 1, 32693),
                            ('C'), new ItemStack(gregComponents, 1, 32633),
                            ('I'), "circuitData"
                    }
                )
            );

            GameRegistry.addRecipe
            (
                new ShapedOreRecipe(
                    new ItemStack(AdvancedAEItems.partGregTechStorageBus, 1),
                    new Object[]{"EMS", "ICI", "   ",
                            ('M'), new ItemStack(meIterface, 1),
                            ('E'), new ItemStack(gregComponents, 1, 32633),
                            ('S'), new ItemStack(gregComponents, 1, 32693),
                            ('C'), new ItemStack(gregComponents, 1, 32683),
                            ('I'), "circuitData"
                    }
                )
            );

            GameRegistry.addRecipe
            (
                    new ShapedOreRecipe(
                            new ItemStack(AdvancedAEItems.partExportMEInterface, 1),
                            new Object[]{"CMI", "   ", "   ",
                                    ('M'), new ItemStack(meIterface, 1),
                                    ('C'), new ItemStack(gregComponents, 1, 32633),
                                    ('I'), "circuitData"
                            }
                    )
            );
        }
    }
}
