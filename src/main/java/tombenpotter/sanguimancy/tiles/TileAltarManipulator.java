package tombenpotter.sanguimancy.tiles;

import WayofTime.bloodmagic.api.registry.AltarRecipeRegistry;
import WayofTime.bloodmagic.tile.TileAltar;
import WayofTime.bloodmagic.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import tombenpotter.sanguimancy.api.tiles.TileBaseSidedInventory;

public class TileAltarManipulator extends TileBaseSidedInventory implements ITickable {

    public int sideToOutput;

    public TileAltarManipulator() {
        super(3);
        customNBTTag = new NBTTagCompound();
        sideToOutput = 0;
    }

    @Override
    public void update() {
        BlockPos down = pos.down();
        if (worldObj.getTileEntity(down) != null && worldObj.getTileEntity(down) instanceof TileAltar) {
            TileAltar tile = (TileAltar) worldObj.getTileEntity(down);
            if (getInventory(null).getStackInSlot(1) != null)
                outputToAdjacentInventory();
            if (tile.getStackInSlot(0) != null)
                moveItemsFromAltar(tile, worldObj.getBlockState(pos));
            if (getInventory(null).getStackInSlot(0) != null)
                insertItemInAltar(tile, worldObj.getBlockState(pos));
        }
    }

    public void insertItemInAltar(TileAltar tile, IBlockState state) {
        ItemStack stack = getInventory(null).getStackInSlot(0).copy();
        if (AltarRecipeRegistry.getRecipeForInput(stack) != null && AltarRecipeRegistry.getRecipeForInput(stack).doesRequiredItemMatch(stack, tile.getTier())) {
            AltarRecipeRegistry.AltarRecipe recipe = AltarRecipeRegistry.getRecipeForInput(stack);
            int maxAmount = tile.getCurrentBlood() / recipe.getSyphon();
            int amount = getInventory(null).getStackInSlot(0).stackSize;

            if (tile.getStackInSlot(0) == null && amount > 0 && amount <= maxAmount) {
                stack.stackSize = amount;
                tile.setInventorySlotContents(0, stack);
                getInventory(null).extractItem(0, amount, false);

                tile.startCycle();
            } else if (tile.getStackInSlot(0) != null) {
                ItemStack altarItem = tile.getStackInSlot(0).copy();
                if (altarItem.isItemEqual(stack) && altarItem.stackSize < altarItem.getMaxStackSize()) {
                    tile.getStackInSlot(0).stackSize += 1;
                    getInventory(null).extractItem(0, 1, false);

                    worldObj.notifyBlockUpdate(pos, state, state, 3);
                    IBlockState altar = worldObj.getBlockState(tile.getPos());
                    worldObj.notifyBlockUpdate(tile.getPos(), altar, altar, 3);

                    cooldown = 10;
                }
            }
        }
    }

    public void moveItemsFromAltar(TileAltar tile, IBlockState state) {
        if (tile.getProgress() <= 0) {
            ItemStack result = getInventory(null).insertItem(1, tile.getStackInSlot(0).copy(), false);
            tile.setInventorySlotContents(0, result);

            worldObj.notifyBlockUpdate(pos, state, state, 3);
            IBlockState altar = worldObj.getBlockState(tile.getPos());
            worldObj.notifyBlockUpdate(tile.getPos(), altar, altar, 3);
        }
    }

    public void outputToAdjacentInventory() {
        ItemStack stack = getInventory(null).getStackInSlot(1);
        EnumFacing dir = EnumFacing.getFront(sideToOutput);
        if (dir != EnumFacing.DOWN) {
            TileEntity tile = worldObj.getTileEntity(pos.add(dir.getDirectionVec()));
            if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
                Utils.insertStackIntoTile(stack.copy(), tile, dir.getOpposite());
                getInventory(null).extractItem(1, stack.stackSize, false);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        sideToOutput = tagCompound.getInteger("sideToOutput");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("sideToOutput", sideToOutput);

        return tagCompound;
    }
}
