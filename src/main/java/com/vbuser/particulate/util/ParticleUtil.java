package com.vbuser.particulate.util;

import com.vbuser.particulate.render.particulate.ParticulateLeave;
import com.vbuser.particulate.render.particulate.ParticulateSimple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.EnumHelper;

public class ParticleUtil {
    public static EnumParticleTypes registerParticleSystem(String enumName, String name, int id, boolean ignoreRange, int argumentCount, IParticleFactory factory) {
        for (EnumParticleTypes existingParticle : EnumParticleTypes.values()) {
            if (existingParticle.getParticleID() == id) {
                throw new RuntimeException("Something attempted to register a particle with the same integer ID as " + existingParticle.getParticleName() + " (" + existingParticle + ").");
            }
        }

        EnumParticleTypes particle = EnumHelper.addEnum(EnumParticleTypes.class, enumName,
                new Class[]{String.class, int.class, boolean.class, int.class},
                name, id, ignoreRange, argumentCount);
        Minecraft.getMinecraft().effectRenderer.registerParticle(id, factory);

        return particle;
    }

    public static EnumParticleTypes LEAVE;
    public static EnumParticleTypes SIMPLE;

    public static void init() {
        LEAVE = registerParticleSystem("PARTICULATE_LEAVE", "leave", 49, true, 0, new ParticulateLeave.Factory());
        SIMPLE = registerParticleSystem("PARTICULATE_SIMPLE","simple",50,true,0,new ParticulateSimple.Factory());
    }
}