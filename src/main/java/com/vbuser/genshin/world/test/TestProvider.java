package com.vbuser.genshin.world.test;

import com.vbuser.genshin.init.InitBiome;
import com.vbuser.genshin.init.InitDimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class TestProvider extends WorldProvider {
    public TestProvider(){
        this.biomeProvider = new BiomeProviderSingle(InitBiome.BIOME_TEST);
        hasSkyLight = true;
    }

    @Override
    public IChunkGenerator createChunkGenerator(){
        return new TestChunkGenerator(world,world.getSeed(),true);
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