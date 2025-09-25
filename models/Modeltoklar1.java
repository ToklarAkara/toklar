public static class Modeltoklar1 extends ModelBase {
	private final ModelRenderer armorHead;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer armorBody;
	private final ModelRenderer armorLeftArm;
	private final ModelRenderer armorRightArm;
	private final ModelRenderer armorLeftLeg;
	private final ModelRenderer armorRightLeg;
	private final ModelRenderer armorLeftBoot;
	private final ModelRenderer armorRightBoot;

	public Modeltoklar1() {
		textureWidth = 128;
		textureHeight = 128;

		armorHead = new ModelRenderer(this);
		armorHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		armorHead.cubeList.add(new ModelBox(armorHead, 0, 0, -5.0F, -9.0F, -5.0F, 10, 1, 10, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 0, 12, -4.0F, -10.0F, -4.0F, 8, 1, 8, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 25, 37, -5.0F, -8.0F, -5.0F, 1, 8, 9, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 33, 3, 4.0F, -8.0F, -5.0F, 1, 8, 9, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 31, 0, -4.0F, -8.0F, -5.0F, 8, 1, 1, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 0, 12, -1.0F, -7.0F, -5.0F, 2, 3, 1, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 0, 51, -5.0F, -8.0F, 4.0F, 10, 8, 1, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 47, 61, -2.0F, -4.0F, -5.0F, 4, 1, 1, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 46, 45, 2.0F, -1.0F, -5.0F, 2, 1, 1, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 0, 17, -4.0F, -1.0F, -5.0F, 2, 1, 1, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 52, 23, 3.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 46, 48, -4.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
		armorHead.cubeList.add(new ModelBox(armorHead, 0, 0, -1.0F, -13.0F, -1.0F, 2, 3, 2, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		armorHead.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.2618F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 55, 23, -1.0F, -16.0F, -6.0F, 2, 1, 7, 0.0F, false));
		cube_r1.cubeList.add(new ModelBox(cube_r1, 45, 0, -1.0F, -15.0F, -7.0F, 2, 1, 9, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
		armorHead.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.2618F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 22, -1.0F, -14.0F, -8.0F, 2, 2, 11, 0.0F, false));

		armorBody = new ModelRenderer(this);
		armorBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		armorBody.cubeList.add(new ModelBox(armorBody, 0, 36, -5.0F, 0.0F, -4.0F, 10, 12, 2, 0.0F, false));
		armorBody.cubeList.add(new ModelBox(armorBody, 27, 22, -5.0F, 0.0F, 2.0F, 10, 12, 2, 0.0F, false));
		armorBody.cubeList.add(new ModelBox(armorBody, 46, 45, -6.0F, 5.0F, -4.0F, 1, 7, 8, 0.0F, false));
		armorBody.cubeList.add(new ModelBox(armorBody, 44, 29, 5.0F, 5.0F, -4.0F, 1, 7, 8, 0.0F, false));

		armorLeftArm = new ModelRenderer(this);
		armorLeftArm.setRotationPoint(-4.0F, 2.0F, 0.0F);
		armorLeftArm.cubeList.add(new ModelBox(armorLeftArm, 17, 55, -5.0F, -3.0F, -3.0F, 4, 1, 6, 0.0F, false));
		armorLeftArm.cubeList.add(new ModelBox(armorLeftArm, 59, 55, -5.0F, -2.0F, -3.0F, 1, 2, 6, 0.0F, false));
		armorLeftArm.cubeList.add(new ModelBox(armorLeftArm, 16, 22, -5.0F, 4.0F, -2.0F, 1, 5, 4, 0.0F, false));
		armorLeftArm.cubeList.add(new ModelBox(armorLeftArm, 45, 3, -4.0F, -2.0F, 2.0F, 3, 2, 1, 0.0F, false));
		armorLeftArm.cubeList.add(new ModelBox(armorLeftArm, 25, 41, -4.0F, -2.0F, -3.0F, 3, 2, 1, 0.0F, false));

		armorRightArm = new ModelRenderer(this);
		armorRightArm.setRotationPoint(4.0F, 2.0F, 0.0F);
		armorRightArm.cubeList.add(new ModelBox(armorRightArm, 50, 15, 1.0F, -3.0F, -3.0F, 4, 1, 6, 0.0F, false));
		armorRightArm.cubeList.add(new ModelBox(armorRightArm, 59, 0, 4.0F, -2.0F, -3.0F, 1, 2, 6, 0.0F, false));
		armorRightArm.cubeList.add(new ModelBox(armorRightArm, 25, 37, 1.0F, -2.0F, 2.0F, 3, 2, 1, 0.0F, false));
		armorRightArm.cubeList.add(new ModelBox(armorRightArm, 0, 6, 1.0F, -2.0F, -3.0F, 3, 2, 1, 0.0F, false));
		armorRightArm.cubeList.add(new ModelBox(armorRightArm, 0, 22, 4.0F, 4.0F, -2.0F, 1, 5, 4, 0.0F, false));

		armorLeftLeg = new ModelRenderer(this);
		armorLeftLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		armorLeftLeg.cubeList.add(new ModelBox(armorLeftLeg, 0, 67, -3.0F, 0.0F, 2.0F, 5, 2, 2, 0.0F, false));
		armorLeftLeg.cubeList.add(new ModelBox(armorLeftLeg, 69, 69, -1.0F, 4.0F, 2.0F, 3, 1, 2, 0.0F, false));
		armorLeftLeg.cubeList.add(new ModelBox(armorLeftLeg, 54, 11, -1.0F, 4.0F, -4.0F, 3, 1, 2, 0.0F, false));
		armorLeftLeg.cubeList.add(new ModelBox(armorLeftLeg, 65, 11, -4.0F, 2.0F, 2.0F, 6, 2, 2, 0.0F, false));
		armorLeftLeg.cubeList.add(new ModelBox(armorLeftLeg, 64, 64, -4.0F, 2.0F, -4.0F, 6, 2, 2, 0.0F, false));
		armorLeftLeg.cubeList.add(new ModelBox(armorLeftLeg, 66, 37, -3.0F, 0.0F, -4.0F, 5, 2, 2, 0.0F, false));
		armorLeftLeg.cubeList.add(new ModelBox(armorLeftLeg, 0, 72, -3.0F, 0.0F, -2.0F, 1, 2, 4, 0.0F, false));
		armorLeftLeg.cubeList.add(new ModelBox(armorLeftLeg, 62, 69, -4.0F, 2.0F, -2.0F, 1, 2, 4, 0.0F, false));

		armorRightLeg = new ModelRenderer(this);
		armorRightLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
		armorRightLeg.cubeList.add(new ModelBox(armorRightLeg, 65, 50, -2.0F, 0.0F, 2.0F, 5, 2, 2, 0.0F, false));
		armorRightLeg.cubeList.add(new ModelBox(armorRightLeg, 65, 16, -2.0F, 0.0F, -4.0F, 5, 2, 2, 0.0F, false));
		armorRightLeg.cubeList.add(new ModelBox(armorRightLeg, 47, 64, -2.0F, 2.0F, 2.0F, 6, 2, 2, 0.0F, false));
		armorRightLeg.cubeList.add(new ModelBox(armorRightLeg, 55, 32, -2.0F, 2.0F, -4.0F, 6, 2, 2, 0.0F, false));
		armorRightLeg.cubeList.add(new ModelBox(armorRightLeg, 51, 69, 2.0F, 0.0F, -2.0F, 1, 2, 4, 0.0F, false));
		armorRightLeg.cubeList.add(new ModelBox(armorRightLeg, 40, 68, 3.0F, 2.0F, -2.0F, 1, 2, 4, 0.0F, false));
		armorRightLeg.cubeList.add(new ModelBox(armorRightLeg, 68, 55, -2.0F, 4.0F, 2.0F, 3, 1, 2, 0.0F, false));
		armorRightLeg.cubeList.add(new ModelBox(armorRightLeg, 68, 0, -2.0F, 4.0F, -4.0F, 3, 1, 2, 0.0F, false));

		armorLeftBoot = new ModelRenderer(this);
		armorLeftBoot.setRotationPoint(-2.0F, 12.0F, 0.0F);
		armorLeftBoot.cubeList.add(new ModelBox(armorLeftBoot, 57, 39, -3.0F, 9.0F, -3.0F, 1, 4, 6, 0.0F, false));
		armorLeftBoot.cubeList.add(new ModelBox(armorLeftBoot, 37, 41, -3.0F, 8.0F, -3.0F, 1, 1, 2, 0.0F, false));
		armorLeftBoot.cubeList.add(new ModelBox(armorLeftBoot, 37, 37, -3.0F, 8.0F, 1.0F, 1, 1, 2, 0.0F, false));
		armorLeftBoot.cubeList.add(new ModelBox(armorLeftBoot, 15, 69, -2.0F, 7.0F, 2.0F, 4, 6, 1, 0.0F, false));
		armorLeftBoot.cubeList.add(new ModelBox(armorLeftBoot, 0, 61, -2.0F, 12.0F, -2.0F, 4, 1, 4, 0.0F, false));
		armorLeftBoot.cubeList.add(new ModelBox(armorLeftBoot, 67, 23, -2.0F, 8.0F, -3.0F, 4, 5, 1, 0.0F, false));

		armorRightBoot = new ModelRenderer(this);
		armorRightBoot.setRotationPoint(2.0F, 12.0F, 0.0F);
		armorRightBoot.cubeList.add(new ModelBox(armorRightBoot, 31, 3, -2.0F, 8.0F, -3.0F, 4, 5, 1, 0.0F, false));
		armorRightBoot.cubeList.add(new ModelBox(armorRightBoot, 29, 68, -2.0F, 7.0F, 2.0F, 4, 6, 1, 0.0F, false));
		armorRightBoot.cubeList.add(new ModelBox(armorRightBoot, 32, 57, 2.0F, 9.0F, -3.0F, 1, 4, 6, 0.0F, false));
		armorRightBoot.cubeList.add(new ModelBox(armorRightBoot, 25, 16, 2.0F, 8.0F, 1.0F, 1, 1, 2, 0.0F, false));
		armorRightBoot.cubeList.add(new ModelBox(armorRightBoot, 25, 12, 2.0F, 8.0F, -3.0F, 1, 1, 2, 0.0F, false));
		armorRightBoot.cubeList.add(new ModelBox(armorRightBoot, 13, 63, -2.0F, 12.0F, -2.0F, 4, 1, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		armorHead.render(f5);
		armorBody.render(f5);
		armorLeftArm.render(f5);
		armorRightArm.render(f5);
		armorLeftLeg.render(f5);
		armorRightLeg.render(f5);
		armorLeftBoot.render(f5);
		armorRightBoot.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}