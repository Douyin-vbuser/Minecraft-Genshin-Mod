//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:ChatGPT(LMArena)
//AI注释服务提供商:DeepSeek

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

/**
 * ParticulateSimple 类实现了基于数学表达式的通用粒子效果<br>
 * 支持通过表达式控制粒子的位置、颜色和尺寸等属性
 */
public class ParticulateSimple extends Particle {

    // 预设表达式数组，包含三组不同的粒子行为
    private static final String[][] PRESET={
            {"sin(t)","0","cos(t)","1","1","1","1","0.05"},  // 圆形运动，白色粒子
            {"t","0","0","1","0","0","1","0.06"},            // 沿x轴线性运动，红色粒子
            {"0","sin(t)","0","0","1","0","1","0.04"}        // 沿y轴正弦运动，绿色粒子
    };
    private static final Random R=new Random();  // 随机数生成器

    // 表达式抽象语法树
    private final ExprNode astX, astY, astZ, astR, astG, astB, astA, astS;
    private final double ox,oy,oz;  // 粒子初始位置
    private final Map<String, Double> variables = new HashMap<>();  // 表达式变量映射表

    /**
     * 带表达式参数的构造函数
     * @param w 世界对象
     * @param x 初始x坐标
     * @param y 初始y坐标
     * @param z 初始z坐标
     * @param exX x坐标表达式
     * @param exY y坐标表达式
     * @param exZ z坐标表达式
     * @param exR 红色分量表达式
     * @param exG 绿色分量表达式
     * @param exB 蓝色分量表达式
     * @param exA 透明度表达式
     * @param exS 尺寸表达式
     * @param life 粒子生命周期
     */
    public ParticulateSimple(World w,double x,double y,double z,
                             String exX,String exY,String exZ,
                             String exR,String exG,String exB,String exA,
                             String exS,int life){
        super(w,x,y,z);
        this.ox=x;this.oy=y;this.oz=z;

        // 编译所有表达式
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

    /**
     * 简单构造函数，使用预设表达式
     * @param w 世界对象
     * @param x 初始x坐标
     * @param y 初始y坐标
     * @param z 初始z坐标
     */
    public ParticulateSimple(World w,double x,double y,double z){
        this(w,x,y,z,preset()[0],preset()[1],preset()[2],
                preset()[3],preset()[4],preset()[5],preset()[6],
                preset()[7],80);
    }

    /**
     * 随机选择一组预设表达式
     * @return 选中的预设表达式数组
     */
    private static String[] preset(){return PRESET[R.nextInt(PRESET.length)];}

    /**
     * 更新粒子状态<br>
     * 根据表达式计算粒子的位置、颜色和尺寸
     */
    @Override
    public void onUpdate(){
        // 检查粒子生命周期
        if(particleAge++>=particleMaxAge){setExpired();return;}

        // 计算时间变量
        double t=particleAge/20.0;
        variables.put("t", t);

        // 计算粒子属性
        double dx=astX.evaluate(variables);
        double dy=astY.evaluate(variables);
        double dz=astZ.evaluate(variables);
        double r=c(astR.evaluate(variables));  // 限制颜色值在[0,1]范围内
        double g=c(astG.evaluate(variables));
        double b=c(astB.evaluate(variables));
        double a=c(astA.evaluate(variables));
        double s=astS.evaluate(variables);

        // 更新粒子状态
        prevPosX=posX;prevPosY=posY;prevPosZ=posZ;
        posX=ox+dx;posY=oy+dy;posZ=oz+dz;
        particleRed=(float)r;particleGreen=(float)g;particleBlue=(float)b;particleAlpha=(float)a;
        particleScale=(float)s;
    }

    /**
     * 限制数值在[0,1]范围内
     * @param v 输入值
     * @return 限制后的值
     */
    private static double c(double v){return v<0?0:v>1?1:v;}

    /**
     * 渲染粒子
     * @param b 缓冲区构建器
     * @param cam 摄像机实体
     * @param pt 部分帧时间，用于平滑插值
     * @param rx 旋转矩阵的X分量
     * @param rz 旋转矩阵的Z分量
     * @param ryz 旋转矩阵的YZ分量
     * @param rxy 旋转矩阵的XY分量
     * @param rxz 旋转矩阵的XZ分量
     */
    @Override
    public void renderParticle(BufferBuilder b,Entity cam,float pt,
                               float rx,float rz,float ryz,float rxy,float rxz){
        // 计算摄像机插值位置
        double cx=cam.lastTickPosX+(cam.posX-cam.lastTickPosX)*pt;
        double cy=cam.lastTickPosY+(cam.posY-cam.lastTickPosY)*pt;
        double cz=cam.lastTickPosZ+(cam.posZ-cam.lastTickPosZ)*pt;

        // 计算粒子相对于摄像机的位置
        float x=(float)(posX-cx);
        float y=(float)(posY-cy);
        float z=(float)(posZ-cz);

        // 设置粒子尺寸和纹理坐标
        float s=particleScale;
        float u0=0f,u1=1f/16f,v0=0f,v1=1f/16f;

        // 设置光照值 (固定值)
        int br=0xF000F0;

        // 构建粒子的四个顶点 (四边形)
        b.pos(x-rx*s-rxy*s,y-rz*s,z-ryz*s-rxz*s).tex(u0,v0).color(particleRed,particleGreen,particleBlue,particleAlpha).lightmap(br>>16&65535,br&65535).endVertex();
        b.pos(x-rx*s+rxy*s,y+rz*s,z-ryz*s+rxz*s).tex(u0,v1).color(particleRed,particleGreen,particleBlue,particleAlpha).lightmap(br>>16&65535,br&65535).endVertex();
        b.pos(x+rx*s+rxy*s,y+rz*s,z+ryz*s+rxz*s).tex(u1,v1).color(particleRed,particleGreen,particleBlue,particleAlpha).lightmap(br>>16&65535,br&65535).endVertex();
        b.pos(x+rx*s-rxy*s,y-rz*s,z+ryz*s-rxz*s).tex(u1,v0).color(particleRed,particleGreen,particleBlue,particleAlpha).lightmap(br>>16&65535,br&65535).endVertex();
    }

    /**
     * 粒子工厂类<br>
     * 用于创建ParticulateSimple粒子实例
     */
    public static class Factory implements IParticleFactory{
        @Override
        public Particle createParticle(int id,World w,double x,double y,double z,double xs,double ys,double zs,int... a){
            return new ParticulateSimple(w,x,y,z);
        }
    }
}