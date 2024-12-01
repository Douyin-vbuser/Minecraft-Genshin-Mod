package com.vbuser.genshin.world.test;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeVoidDecorator;

public class TestBiome extends Biome {
    public TestBiome() {
        super(new BiomeProperties("biome_test"));
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        decorator = new BiomeVoidDecorator();
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
