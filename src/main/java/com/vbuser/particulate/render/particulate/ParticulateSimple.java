package com.vbuser.particulate.render.particulate;

import com.vbuser.particulate.math.ExprNode;
import com.vbuser.particulate.math.ExpressionParser;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ParticulateSimple extends Particle {

    private static final String[][] PRESET={
            {"sin(t)","0","cos(t)","1","1","1","1","0.05"},
            {"t","0","0","1","0","0","1","0.06"},
            {"0","sin(t)","0","0","1","0","1","0.04"}
    };
    private static final Random R=new Random();

    private final ExprNode astX, astY, astZ, astR, astG, astB, astA, astS;
    private final double ox,oy,oz;
    private final Map<String, Double> variables = new HashMap<>();

    public ParticulateSimple(World w,double x,double y,double z,
                             String exX,String exY,String exZ,
                             String exR,String exG,String exB,String exA,
                             String exS,int life){
        super(w,x,y,z);
        this.ox=x;this.oy=y;this.oz=z;

        ExpressionParser parser = new ExpressionParser();
        this.astX = parser.compile(exX);
        this.astY = parser.compile(exY);
        this.astZ = parser.compile(exZ);
        this.astR = parser.compile(exR);
        this.astG = parser.compile(exG);
        this.astB = parser.compile(exB);
        this.astA = parser.compile(exA);
        this.astS = parser.compile(exS);

        this.particleMaxAge=life;
        this.canCollide=false;
        this.setParticleTextureIndex(0);
    }

    public ParticulateSimple(World w,double x,double y,double z){
        this(w,x,y,z,preset()[0],preset()[1],preset()[2],
                preset()[3],preset()[4],preset()[5],preset()[6],
                preset()[7],80);
    }

    private static String[] preset(){return PRESET[R.nextInt(PRESET.length)];}

    @Override
    public void onUpdate(){
        if(particleAge++>=particleMaxAge){setExpired();return;}
        double t=particleAge/20.0;
        variables.put("t", t);

        double dx=astX.evaluate(variables);
        double dy=astY.evaluate(variables);
        double dz=astZ.evaluate(variables);
        double r=c(astR.evaluate(variables));
        double g=c(astG.evaluate(variables));
        double b=c(astB.evaluate(variables));
        double a=c(astA.evaluate(variables));
        double s=astS.evaluate(variables);

        prevPosX=posX;prevPosY=posY;prevPosZ=posZ;
        posX=ox+dx;posY=oy+dy;posZ=oz+dz;
        particleRed=(float)r;particleGreen=(float)g;particleBlue=(float)b;particleAlpha=(float)a;
        particleScale=(float)s;
    }

    private static double c(double v){return v<0?0:v>1?1:v;}

    @Override
    public void renderParticle(BufferBuilder b,Entity cam,float pt,
                               float rx,float rz,float ryz,float rxy,float rxz){
        double cx=cam.lastTickPosX+(cam.posX-cam.lastTickPosX)*pt;
        double cy=cam.lastTickPosY+(cam.posY-cam.lastTickPosY)*pt;
        double cz=cam.lastTickPosZ+(cam.posZ-cam.lastTickPosZ)*pt;
        float x=(float)(posX-cx);
        float y=(float)(posY-cy);
        float z=(float)(posZ-cz);
        float s=particleScale;
        float u0=0f,u1=1f/16f,v0=0f,v1=1f/16f;
        int br=0xF000F0;
        b.pos(x-rx*s-rxy*s,y-rz*s,z-ryz*s-rxz*s).tex(u0,v0).color(particleRed,particleGreen,particleBlue,particleAlpha).lightmap(br>>16&65535,br&65535).endVertex();
        b.pos(x-rx*s+rxy*s,y+rz*s,z-ryz*s+rxz*s).tex(u0,v1).color(particleRed,particleGreen,particleBlue,particleAlpha).lightmap(br>>16&65535,br&65535).endVertex();
        b.pos(x+rx*s+rxy*s,y+rz*s,z+ryz*s+rxz*s).tex(u1,v1).color(particleRed,particleGreen,particleBlue,particleAlpha).lightmap(br>>16&65535,br&65535).endVertex();
        b.pos(x+rx*s-rxy*s,y-rz*s,z+ryz*s-rxz*s).tex(u1,v0).color(particleRed,particleGreen,particleBlue,particleAlpha).lightmap(br>>16&65535,br&65535).endVertex();
    }

    public static class Factory implements IParticleFactory{
        @Override
        public Particle createParticle(int id,World w,double x,double y,double z,double xs,double ys,double zs,int... a){
            return new ParticulateSimple(w,x,y,z);
        }
    }
}