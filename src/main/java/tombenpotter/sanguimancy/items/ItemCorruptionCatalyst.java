package tombenpotter.sanguimancy.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tombenpotter.sanguimancy.Sanguimancy;
import tombenpotter.sanguimancy.recipes.CorruptedInfusionRecipe;
import tombenpotter.sanguimancy.util.RandomUtils;
import tombenpotter.sanguimancy.util.SoulCorruptionHelper;

import java.util.List;

public class ItemCorruptionCatalyst extends Item {

    public ItemCorruptionCatalyst() {
        setCreativeTab(Sanguimancy.tabSanguimancy);
        setUnlocalizedName(Sanguimancy.modid + ".corruptionCatalist");
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ri) {
        this.itemIcon = ri.registerIcon(Sanguimancy.texturePath + ":CorruptionCatalyst");
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        RandomUtils.checkAndSetCompound(stack);
        if (!stack.stackTagCompound.hasKey("activated")) {
            stack.stackTagCompound.setBoolean("activated", false);
        } else if (stack.stackTagCompound.getBoolean("activated")) {
            if (entity != null && entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                NBTTagCompound tag = SoulCorruptionHelper.getModTag(player, Sanguimancy.modid);
                if (player.getHeldItem() != null && CorruptedInfusionRecipe.isRecipeValid(new ItemStack[]{player.getHeldItem()}, SoulCorruptionHelper.getCorruptionLevel(tag))) {
                    ItemStack[] input = new ItemStack[]{player.getHeldItem().copy()};
                    ItemStack output = CorruptedInfusionRecipe.getPossibleRecipes(input, SoulCorruptionHelper.getCorruptionLevel(tag)).get(0).fOutput.copy();
                    for (ItemStack inputStack : CorruptedInfusionRecipe.getPossibleRecipes(input, SoulCorruptionHelper.getCorruptionLevel(tag)).get(0).fInput) {
                        if (world.getWorldTime() % CorruptedInfusionRecipe.getPossibleRecipes(input, SoulCorruptionHelper.getCorruptionLevel(tag)).get(0).fTime == 0) {
                            for (int i = 0; i < inputStack.stackSize; i++) {
                                player.inventory.consumeInventoryItem(inputStack.getItem());
                            }
                            if (!player.inventory.addItemStackToInventory(output)) {
                                RandomUtils.dropItemStackInWorld(world, player.posX, player.posY, player.posZ, output);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer playzr) {
        RandomUtils.checkAndSetCompound(stack);
        if (!stack.stackTagCompound.hasKey("activated")) {
            stack.stackTagCompound.setBoolean("activated", false);
        }
        stack.stackTagCompound.setBoolean("activated", !stack.stackTagCompound.getBoolean("activated"));
        return stack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("activated")) {
            return stack.stackTagCompound.getBoolean("activated");
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (!GuiScreen.isShiftKeyDown()) {
            list.add(StatCollector.translateToLocal("info.Sanguimancy.tooltip.shift.info"));
        }
        if (GuiScreen.isShiftKeyDown()) {
            if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("activated")) {
                list.add(StatCollector.translateToLocal("info.Sanguimancy.tooltip.activated") + ": " + stack.stackTagCompound.getBoolean("activated"));
            }
            list.add(StatCollector.translateToLocal("info.Sanguimancy.tooltip.corrupted.infusion.1"));
            list.add(StatCollector.translateToLocal("info.Sanguimancy.tooltip.corrupted.infusion.2"));
        }
    }
}
