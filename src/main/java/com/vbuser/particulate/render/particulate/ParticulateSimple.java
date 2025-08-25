package com.vbuser.particulate.render.particulate;

import com.vbuser.particulate.math.ExpressionParser;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.Random;

public class ParticulateSimple extends Particle {

    private static final String[][] PRESET={
            {"sin(t)","0","cos(t)","1","1","1","1","0.05"},
            {"t","0","0","1","0","0","1","0.06"},
            {"0","sin(t)","0","0","1","0","1","0.04"}
    };
    private static final Random R=new Random();

    private final String exX,exY,exZ,exR,exG,exB,exA,exS;
    private final double ox,oy,oz;
    private final ExpressionParser p=new ExpressionParser();

    public ParticulateSimple(World w,double x,double y,double z,
                             String exX,String exY,String exZ,
                             String exR,String exG,String exB,String exA,
                             String exS,int life){
        super(w,x,y,z);
        this.ox=x;this.oy=y;this.oz=z;
        this.exX=exX;this.exY=exY;this.exZ=exZ;
        this.exR=exR;this.exG=exG;this.exB=exB;this.exA=exA;
        this.exS=exS;
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
        p.setVariable("t",t);
        double dx=p.parse(exX);
        double dy=p.parse(exY);
        double dz=p.parse(exZ);
        double r=c(p.parse(exR));
        double g=c(p.parse(exG));
        double b=c(p.parse(exB));
        double a=c(p.parse(exA));
        double s=p.parse(exS);
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