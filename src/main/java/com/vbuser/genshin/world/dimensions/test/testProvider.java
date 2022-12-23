package com.vbuser.genshin.world.dimensions.test;

import com.vbuser.genshin.init.InitBiome;
import net.minecraft.init.Biomes;
import com.vbuser.genshin.init.InitDimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class testProvider extends WorldProvider {
    public testProvider(){
        this.biomeProvider = new BiomeProviderSingle(InitBiome.BIOME_TEST);
        hasSkyLight = true;
    }

    @Override
    public IChunkGenerator createChunkGenerator(){
        return new testChunkGenerator(world,world.getSeed(),true);
    }

    @Override
    public DimensionType getDimensionType(){
        return InitDimension.TEST_DIMENSION;
    }
    @Override
    public boolean canRespawnHere(){
        return false;
    }
    @Override
    public boolean isSurfaceWorld() {
        return false;
    }


}