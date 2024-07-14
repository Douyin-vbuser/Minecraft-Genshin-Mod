package com.vbuser.genshin.world.test;

import com.vbuser.genshin.init.InitBiome;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;


@SuppressWarnings("all")
public class TestMapGen extends MapGenStructure {

    public static List<Biome> villageSpawnBiomes = Arrays.<Biome>asList(new Biome[] {InitBiome.BIOME_TEST});
    /** World terrain type, 0 for normal, 1 for flat map */
    private int terrainType;
    private int field_82665_g;
    private int field_82666_h;

    public TestMapGen()
    {
        field_82665_g = 32;
        field_82666_h = 8;
    }

    public TestMapGen(Map<String, String> p_i2093_1_)
    {
        this();

        for (Map.Entry<String, String> entry : p_i2093_1_.entrySet())
            if (entry.getKey().equals("size"))
                terrainType = MathHelper.getInt(entry.getValue(), terrainType, 0);
            else if (entry.getKey().equals("distance"))
                field_82665_g = MathHelper.getInt(entry.getValue(), field_82665_g, field_82666_h + 1);
    }

    @Override
    public String getStructureName()
    {
        return null;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        if(chunkX == 0 && chunkZ == 0) return true;

        return false;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new Start(world, rand, chunkX, chunkZ, terrainType);
    }

    public static class Start extends StructureStart
    {
        /** well ... thats what it does */
        private boolean hasMoreThanTwoComponents;

        public Start()
        {
        }


        public Start(World worldIn, Random random, int chunkX, int chunkZ, int p_i2092_5_)
        {
            super(chunkX, chunkZ);
            StructureStrongholdPieces.prepareStructurePieces();
            StructureStrongholdPieces.Stairs2 structurestrongholdpieces$stairs2 = new StructureStrongholdPieces.Stairs2(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.components.add(structurestrongholdpieces$stairs2);
            structurestrongholdpieces$stairs2.buildComponent(structurestrongholdpieces$stairs2, this.components, random);
            List<StructureComponent> list = structurestrongholdpieces$stairs2.pendingChildren;

            while (!list.isEmpty())
            {
                int i = random.nextInt(list.size());
                StructureComponent structurecomponent = list.remove(i);
                structurecomponent.buildComponent(structurestrongholdpieces$stairs2, this.components, random);
            }

            this.updateBoundingBox();
            this.markAvailableHeight(worldIn, random, 10);
        }
        /**
         * currently only defined for Villages, returns true if Village has more than 2 non-road components
         */
        @Override
        public boolean isSizeableStructure()
        {
            return hasMoreThanTwoComponents;
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound)
        {
            super.writeToNBT(tagCompound);
            tagCompound.setBoolean("Valid", hasMoreThanTwoComponents);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound)
        {
            super.readFromNBT(tagCompound);
            hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
        }
    }

    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {

        return null;
    }
}