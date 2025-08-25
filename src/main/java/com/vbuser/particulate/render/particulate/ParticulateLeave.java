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

public class ParticulateLeave extends Particle {
    private final long startTime;
    private final double initialX, initialY, initialZ;
    private final Map<String, Double> variables = new HashMap<>();
    private final ExprNode[] leavePosAst;
    ResourceLocation rl = new ResourceLocation("particulate", "particle/leave.png");

    public ParticulateLeave(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.startTime = System.currentTimeMillis();
        this.initialX = xCoordIn;
        this.initialY = yCoordIn;
        this.initialZ = zCoordIn;
        this.particleScale *= (float) (0.3 * Math.random() + 1);
        this.leavePosAst = get_leave_pos_ast();
        bindValue();
    }

    public void bindValue() {
        variables.put("A", Math.random() * 4 - 2);
        variables.put("w_x", Math.random() * 0.4 - 0.2);
        variables.put("v_wx", Math.random() * 4 - 2);
        variables.put("g", Math.random() * 4 + 3);
        variables.put("B", Math.random() * 4 - 2);
        variables.put("w_z", Math.random() * 0.4 - 0.2);
        variables.put("v_wz", Math.random() * 4 - 2);
        variables.put("phi_z", 0.0);
        variables.put("phi_x", 0.0);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(rl);

        float minU = 0.0F;
        float maxU = 1.0F;
        float minV = 0.0F;
        float maxV = 1.0F;

        float scale = 0.1F * this.particleScale;
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - Particle.interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - Particle.interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int l = brightness >> 16 & 65535;
        int i1 = brightness & 65535;

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

    @Override
    public void onUpdate() {
        if (world.getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)).getBlock() != Blocks.AIR) {
            this.setExpired();
            return;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double time = elapsedTime / 1000.0;

        variables.put("t", time);

        double dx = leavePosAst[0].evaluate(variables);
        double dy = leavePosAst[1].evaluate(variables);
        double dz = leavePosAst[2].evaluate(variables);

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.posX = initialX + dx;
        this.posY = initialY + dy;
        this.posZ = initialZ + dz;

        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new ParticulateLeave(worldIn, xCoordIn, yCoordIn, zCoordIn);
        }
    }
}