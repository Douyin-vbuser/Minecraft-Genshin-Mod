package com.vbuser.genshin.init;

import com.vbuser.genshin.world.test.TestBiome;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class InitBiome {

    public static final Biome BIOME_TEST = new TestBiome();     //biome_id:40

    public static void registerBiomes()
    {
        initBiome(BIOME_TEST, "biome_test", BiomeManager.BiomeType.WARM, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.DENSE);
    }

    public static void initBiome(Biome biome, String name, BiomeManager.BiomeType biomeType, BiomeDictionary.Type... type) {
        biome.setRegistryName(name);
        ForgeRegistries.BIOMES.register(biome);
        BiomeDictionary.addTypes(biome, type);
        BiomeManager.addBiome(biomeType, new BiomeManager.BiomeEntry(biome, 10));
        BiomeManager.addSpawnBiome(biome);
    }
}