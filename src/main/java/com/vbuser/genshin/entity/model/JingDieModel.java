package com.vbuser.genshin.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class JingDieModel extends ModelBase {
	private final ModelRenderer jing_die;
	private final ModelRenderer rightwing;
	private final ModelRenderer leftwing;
	private final ModelRenderer body;

	public JingDieModel() {
		textureWidth = 16;
		textureHeight = 16;

		jing_die = new ModelRenderer(this);
		jing_die.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(jing_die, 0.4363F, 0.0F, 0.0F);
		

		rightwing = new ModelRenderer(this);
		rightwing.setRotationPoint(-6.0F, 0.0F, 0.0F);
		jing_die.addChild(rightwing);
		rightwing.cubeList.add(new ModelBox(rightwing, 6, 0, 7.0F, -1.0F, -2.0F, 2, 1, 3, 0.0F, false));
		rightwing.cubeList.add(new ModelBox(rightwing, 0, 9, 9.0F, -1.0F, -1.0F, 1, 1, 2, 0.0F, false));
		rightwing.cubeList.add(new ModelBox(rightwing, 0, 2, 10.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
		rightwing.cubeList.add(new ModelBox(rightwing, 4, 10, 6.0F, -1.0F, 0.0F, 1, 1, 2, 0.0F, false));

		leftwing = new ModelRenderer(this);
		leftwing.setRotationPoint(0.0F, 0.0F, 0.0F);
		jing_die.addChild(leftwing);
		leftwing.cubeList.add(new ModelBox(leftwing, 0, 0, -6.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
		leftwing.cubeList.add(new ModelBox(leftwing, 0, 5, -4.0F, -1.0F, -2.0F, 2, 1, 3, 0.0F, false));
		leftwing.cubeList.add(new ModelBox(leftwing, 7, 5, -2.0F, -1.0F, 0.0F, 1, 1, 2, 0.0F, false));
		leftwing.cubeList.add(new ModelBox(leftwing, 8, 8, -5.0F, -1.0F, -1.0F, 1, 1, 2, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		jing_die.addChild(body);
		body.cubeList.add(new ModelBox(body, 0, 0, -1.0F, -1.0F, -3.0F, 1, 1, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		jing_die.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}