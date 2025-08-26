package com.vbuser.particulate.render.particulate;

import com.vbuser.particulate.math.ExprNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

import static com.vbuser.particulate.math.ExpressionList.get_leave_pos_ast;

/**
 * ParticulateLeave 类实现了树叶飘落的粒子效果<br>
 * 使用数学表达式控制树叶的运动轨迹，模拟真实的树叶飘落效果
 */
public class ParticulateLeave extends Particle {
    private final long startTime;           // 粒子创建的时间戳
    private final double initialX, initialY, initialZ;  // 粒子的初始位置
    private final Map<String, Double> variables = new HashMap<>();  // 表达式变量映射表
    private final ExprNode[] leavePosAst;   // 树叶位置表达式的抽象语法树数组
    ResourceLocation rl = new ResourceLocation("particulate", "particle/leave.png");  // 树叶粒子纹理

    /**
     * 构造函数
     * @param worldIn 世界对象
     * @param xCoordIn 初始x坐标
     * @param yCoordIn 初始y坐标
     * @param zCoordIn 初始z坐标
     */
    public ParticulateLeave(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.startTime = System.currentTimeMillis();
        this.initialX = xCoordIn;
        this.initialY = yCoordIn;
        this.initialZ = zCoordIn;
        this.particleScale *= (float) (0.3 * Math.random() + 1);  // 随机缩放粒子尺寸
        this.leavePosAst = get_leave_pos_ast();  // 获取树叶位置表达式的抽象语法树
        bindValue();  // 初始化表达式变量值
    }

    /**
     * 初始化表达式变量值<br>
     * 为树叶运动表达式中的各个参数设置随机值
     */
    public void bindValue() {
        variables.put("A", Math.random() * 4 - 2);        // x轴振幅
        variables.put("w_x", Math.random() * 0.4 - 0.2);  // x轴角频率
        variables.put("v_wx", Math.random() * 4 - 2);     // x轴线性速度
        variables.put("g", Math.random() * 4 + 3);        // 重力加速度
        variables.put("B", Math.random() * 4 - 2);        // z轴振幅
        variables.put("w_z", Math.random() * 0.4 - 0.2);  // z轴角频率
        variables.put("v_wz", Math.random() * 4 - 2);     // z轴线性速度
        variables.put("phi_z", 0.0);                      // z轴初相位
        variables.put("phi_x", 0.0);                      // x轴初相位
    }

    /**
     * 渲染粒子<br>
     * 使用自定义纹理渲染树叶粒子
     * @param buffer 缓冲区构建器
     * @param entityIn 摄像机实体
     * @param partialTicks 部分帧时间，用于平滑插值
     * @param rotationX 旋转矩阵的X分量
     * @param rotationZ 旋转矩阵的Z分量
     * @param rotationYZ 旋转矩阵的YZ分量
     * @param rotationXY 旋转矩阵的XY分量
     * @param rotationXZ 旋转矩阵的XZ分量
     */
    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(rl);  // 绑定树叶纹理

        // 纹理坐标 (使用整个纹理)
        float minU = 0.0F;
        float maxU = 1.0F;
        float minV = 0.0F;
        float maxV = 1.0F;

        // 计算粒子尺寸和位置
        float scale = 0.1F * this.particleScale;
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - Particle.interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - Particle.interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - Particle.interpPosZ);

        // 获取亮度值
        int brightness = this.getBrightnessForRender(partialTicks);
        int l = brightness >> 16 & 65535;
        int i1 = brightness & 65535;

        // 构建粒子的四个顶点 (四边形)
        buffer.pos(x - rotationX * scale - rotationXY * scale, y - rotationZ * scale, z - rotationYZ * scale - rotationXZ * scale)
                .tex(maxU, maxV)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(l, i1)
                .endVertex();
        buffer.pos(x - rotationX * scale + rotationXY * scale, y + rotationZ * scale, z - rotationYZ * scale + rotationXZ * scale)
                .tex(maxU, minV)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(l, i1)
                .endVertex();
        buffer.pos(x + rotationX * scale + rotationXY * scale, y + rotationZ * scale, z + rotationYZ * scale + rotationXZ * scale)
                .tex(minU, minV)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(l, i1)
                .endVertex();
        buffer.pos(x + rotationX * scale - rotationXY * scale, y - rotationZ * scale, z + rotationYZ * scale - rotationXZ * scale)
                .tex(minU, maxV)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(l, i1)
                .endVertex();
    }

    /**
     * 更新粒子状态<br>
     * 根据数学表达式计算树叶的位置
     */
    @Override
    public void onUpdate() {
        // 检查是否落到非空气方块上
        if (world.getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)).getBlock() != Blocks.AIR) {
            this.setExpired();  // 如果落到非空气方块上，则消失
            return;
        }

        // 计算经过的时间
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double time = elapsedTime / 1000.0;

        // 更新时间变量
        variables.put("t", time);

        // 计算树叶的位置偏移
        double dx = leavePosAst[0].evaluate(variables);
        double dy = leavePosAst[1].evaluate(variables);
        double dz = leavePosAst[2].evaluate(variables);

        // 保存上一帧的位置
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        // 计算新的位置
        this.posX = initialX + dx;
        this.posY = initialY + dy;
        this.posZ = initialZ + dz;

        // 更新粒子位置
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    /**
     * 粒子工厂类<br>
     * 用于创建ParticulateLeave粒子实例
     */
    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new ParticulateLeave(worldIn, xCoordIn, yCoordIn, zCoordIn);
        }
    }
}