package ua.lyohha.aae.ae2;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AdvancedPatternHelper implements ICraftingPatternDetails, Comparable<AdvancedPatternHelper> {

    private ItemStack patternItem;
    private IAEItemStack[] condensedInputs = null;
    private IAEItemStack[] condensedOutputs = null;
    private IAEItemStack[] inputs = null;
    private IAEItemStack[] outputs = null;

    public AdvancedPatternHelper(ItemStack stack) {
        patternItem = stack;
    }

    public AdvancedPatternHelper decode() {
        NBTTagCompound encodedValue = patternItem.getTagCompound();
        if (encodedValue == null)
            return null;
        NBTTagList inTag = encodedValue.getTagList("in", 10);
        NBTTagList outTag = encodedValue.getTagList("out", 10);

        List<IAEItemStack> in = new ArrayList<IAEItemStack>();
        List<IAEItemStack> out = new ArrayList<IAEItemStack>();

        for (int i = 0; i < inTag.tagCount(); i++)
            in.add(AEApi.instance().storage().createItemStack(ItemStack.loadItemStackFromNBT(inTag.getCompoundTagAt(i))));

        for (int i = 0; i < outTag.tagCount(); i++)
            out.add(AEApi.instance().storage().createItemStack(ItemStack.loadItemStackFromNBT(outTag.getCompoundTagAt(i))));

        this.inputs = in.toArray(new IAEItemStack[in.size()]);
        this.outputs = out.toArray(new IAEItemStack[out.size()]);

        List<IAEItemStack> inCondensed = new ArrayList<>();
        List<IAEItemStack> outCondensed = new ArrayList<>();

        for (IAEItemStack stack : in) if (stack != null) inCondensed.add(stack.copy());
        for (IAEItemStack stack : out) if (stack != null) outCondensed.add(stack.copy());

        if (inCondensed.size() == 0 || outCondensed.size() == 0)
            return null;

        condensedInputs = inCondensed.toArray(new IAEItemStack[inCondensed.size()]);
        condensedOutputs = outCondensed.toArray(new IAEItemStack[outCondensed.size()]);

        return this;
    }

    @Override
    public ItemStack getPattern() {
        return null;
    }

    @Override
    public boolean isValidItemForSlot(int slotIndex, ItemStack itemStack, World world) {
        return false;
    }

    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public IAEItemStack[] getInputs() {
        return inputs;
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return condensedInputs;
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return condensedOutputs;
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return outputs;
    }

    @Override
    public boolean canSubstitute() {
        return false;
    }

    @Override
    public ItemStack getOutput(InventoryCrafting craftingInv, World world) {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void setPriority(int priority) {

    }

    @Override
    public int compareTo(AdvancedPatternHelper o) {
        return 0;
    }
}
