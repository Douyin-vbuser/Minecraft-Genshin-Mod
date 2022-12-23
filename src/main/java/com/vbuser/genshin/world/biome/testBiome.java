package com.vbuser.genshin.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeVoidDecorator;

public class testBiome extends Biome {
    public testBiome(){
        super(new BiomeProperties("biome_test"));
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        decorator = new BiomeVoidDecorator();
    }
}
