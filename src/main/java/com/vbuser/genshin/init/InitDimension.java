package com.vbuser.genshin.init;

import com.vbuser.genshin.world.dimensions.test.testProvider;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class InitDimension {
    public static final DimensionType TEST_DIMENSION = DimensionType.register("test_dimension", "_dim", 118, testProvider.class, false);

    public static void registerDimensions()
    {
        DimensionManager.registerDimension(118, TEST_DIMENSION);
    }
}