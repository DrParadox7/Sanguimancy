package tombenpotter.sanguimancy.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderSoulNetworkDimension extends WorldProvider {

    @Override
    public void registerWorldChunkManager() {
        WorldChunkManager manager = new WorldChunkManagerHell(BiomeGenBase.plains, BiomeGenBase.deepOcean.rainfall);
        this.worldChunkMgr = manager;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new ChunkProviderSNDimension(worldObj, worldObj.getSeed(), false, "SoulNetworkDimension");
    }

    @Override
    public String getDimensionName() {
        return "Soul Network Dimension";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean getWorldHasVoidParticles() {
        return false;
    }

    public boolean canRespawnHere() {
        return false;
    }


    public float calculateCelestialAngle(final long par1, final float par3) {
        return 0.55f;
    }

    protected void generateLightBrightnessTable() {
        final float f = 1.0f;
        for (int i = 0; i <= 15; ++i) {
            final float f2 = 1.0f - i / 15.0f;
            this.lightBrightnessTable[i] = (1.0f - f2) / (f2 * 3.0f + 1.0f) * (1.0f - f) + f;
        }
    }
}
