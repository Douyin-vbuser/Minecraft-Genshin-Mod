package com.vbuser.genshin.entity.element.feng;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public abstract class Feng extends EntityLiving {
    public Feng(World worldIn){super(worldIn);}     //用于实现所有风元素伤害实体
}
