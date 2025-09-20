package net.mcreator.toklar.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.init.SoundEvents;
import java.util.List;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.mcreator.toklar.ElementsToklar;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@ElementsToklar.ModElement.Tag
public class EntityMonster extends ElementsToklar.ModElement {
    public static final int ENTITYID = 3;

    public EntityMonster(ElementsToklar instance) {
        super(instance, 4);
    }

    @Override
    public void initElements() {
        elements.entities.add(() -> EntityEntryBuilder.create()
                .entity(EntityCustom.class)
                .id(new ResourceLocation("toklar", "monster"), ENTITYID)
                .name("monster")
                .tracker(64, 3, true)
                .egg(-1, -1)
                .build());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderLiving<EntityCustom>(
                renderManager, new Modeltoklar1(), 0.5f) {
            @Override
            protected ResourceLocation getEntityTexture(EntityCustom entity) {
                return new ResourceLocation("toklar:textures/toklar1.png");
            }
        });
    }

    public static class EntityCustom extends EntityMob {
        // Explicit constructor calling super(world)
        public EntityCustom(World world) {
            super(world);
            setSize(0.9f, 1.9f); // standard size
            experienceValue = 50;
            this.isImmuneToFire = false;
            setNoAI(false);
            enablePersistence();
        }

        public ITextComponent getDisplayName() {
            return new TextComponentString("Clanky");
        }

        @Override
        protected int getExperiencePoints(EntityPlayer player) {
            return 18487; // Enough raw XP to give 100 levels
        }

        @Override
        public void onUpdate() {
            super.onUpdate();

            // Vortex trigger: check if mid-air and has a target
            if (!this.onGround && this.getAttackTarget() != null && this.motionY > 0) {
                // Pull nearby players toward the boss
                List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class,
                        this.getEntityBoundingBox().grow(15.0D)); // 15-block radius

                for (EntityPlayer player : players) {
                    if (player.isCreative() || player.isSpectator()) continue;

                    double dx = this.posX - player.posX;
                    double dy = this.posY - player.posY;
                    double dz = this.posZ - player.posZ;

                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist < 0.1) dist = 0.1; // prevent divide-by-zero

                    double strength = 1.0D; // pull strength
                    player.motionX += (dx / dist) * strength;
                    player.motionY += (dy / dist) * strength * 0.5;
                    player.motionZ += (dz / dist) * strength;

                    player.velocityChanged = true;
                }
            }
        }

        @Override
        protected void initEntityAI() {
            this.tasks.addTask(0, new EntityAISwimming(this));
            this.tasks.addTask(1, new EntityAILeapAtTarget(this, 0.4F));
            this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.2D, true));
            this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
            this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
            this.tasks.addTask(5, new EntityAILookIdle(this));
            this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        }

        @Override
        protected void applyEntityAttributes() {
            super.applyEntityAttributes();
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6666.0D);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(15.0D); // Stronger than Wither
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(6.0D); // Like full diamond
            this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        }

        @Override
        protected boolean canDespawn() {
            return false;
        }

        // New loot drop from loot table
        @Override
        protected ResourceLocation getLootTable() {
            return new ResourceLocation("toklar", "entities/clanky");
        }


        @Override
        public EnumCreatureAttribute getCreatureAttribute() {
            return EnumCreatureAttribute.UNDEFINED;
        }

        @Override
        protected SoundEvent getAmbientSound() {
            return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
        }

        @Override
        protected SoundEvent getHurtSound(DamageSource source) {
            return SoundEvents.ENTITY_GENERIC_HURT;
        }

        @Override
        protected SoundEvent getDeathSound() {
            return SoundEvents.ENTITY_GENERIC_DEATH;
        }

        @Override
        protected float getSoundVolume() {
            return 1.0F;
        }
    }

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
            this.textureWidth = 128;
            this.textureHeight = 128;
            this.armorHead = new ModelRenderer(this);
            this.armorHead.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 0, -5.0F, -9.0F, -5.0F, 10, 1, 10, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 12, -4.0F, -10.0F, -4.0F, 8, 1, 8, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 25, 37, -5.0F, -8.0F, -5.0F, 1, 8, 9, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 33, 3, 4.0F, -8.0F, -5.0F, 1, 8, 9, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 31, 0, -4.0F, -8.0F, -5.0F, 8, 1, 1, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 12, -1.0F, -7.0F, -5.0F, 2, 3, 1, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 51, -5.0F, -8.0F, 4.0F, 10, 8, 1, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 47, 61, -2.0F, -4.0F, -5.0F, 4, 1, 1, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 46, 45, 2.0F, -1.0F, -5.0F, 2, 1, 1, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 17, -4.0F, -1.0F, -5.0F, 2, 1, 1, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 52, 23, 3.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 46, 48, -4.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 0, -1.0F, -13.0F, -1.0F, 2, 3, 2, 0.0F, false));
            this.cube_r1 = new ModelRenderer(this);
            this.cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.armorHead.addChild(this.cube_r1);
            setRotationAngle(this.cube_r1, -0.2618F, 0.0F, 0.0F);
            this.cube_r1.cubeList.add(new ModelBox(this.cube_r1, 55, 23, -1.0F, -16.0F, -6.0F, 2, 1, 7, 0.0F, false));
            this.cube_r1.cubeList.add(new ModelBox(this.cube_r1, 45, 0, -1.0F, -15.0F, -7.0F, 2, 1, 9, 0.0F, false));
            this.cube_r2 = new ModelRenderer(this);
            this.cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.armorHead.addChild(this.cube_r2);
            setRotationAngle(this.cube_r2, -0.2618F, 0.0F, 0.0F);
            this.cube_r2.cubeList.add(new ModelBox(this.cube_r2, 0, 22, -1.0F, -14.0F, -8.0F, 2, 2, 11, 0.0F, false));
            this.armorBody = new ModelRenderer(this);
            this.armorBody.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.armorBody.cubeList.add(new ModelBox(this.armorBody, 0, 36, -5.0F, 0.0F, -4.0F, 10, 12, 2, 0.0F, false));
            this.armorBody.cubeList.add(new ModelBox(this.armorBody, 27, 22, -5.0F, 0.0F, 2.0F, 10, 12, 2, 0.0F, false));
            this.armorBody.cubeList.add(new ModelBox(this.armorBody, 46, 45, -6.0F, 5.0F, -4.0F, 1, 7, 8, 0.0F, false));
            this.armorBody.cubeList.add(new ModelBox(this.armorBody, 44, 29, 5.0F, 5.0F, -4.0F, 1, 7, 8, 0.0F, false));
            this.armorLeftArm = new ModelRenderer(this);
            this.armorLeftArm.setRotationPoint(-4.0F, 2.0F, 0.0F);
            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 17, 55, -5.0F, -3.0F, -3.0F, 4, 1, 6, 0.0F, false));
            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 59, 55, -5.0F, -2.0F, -3.0F, 1, 2, 6, 0.0F, false));
            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 16, 22, -5.0F, 4.0F, -2.0F, 1, 5, 4, 0.0F, false));
            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 45, 3, -4.0F, -2.0F, 2.0F, 3, 2, 1, 0.0F, false));
            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 25, 41, -4.0F, -2.0F, -3.0F, 3, 2, 1, 0.0F, false));
            this.armorRightArm = new ModelRenderer(this);
            this.armorRightArm.setRotationPoint(4.0F, 2.0F, 0.0F);
            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 50, 15, 1.0F, -3.0F, -3.0F, 4, 1, 6, 0.0F, false));
            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 59, 0, 4.0F, -2.0F, -3.0F, 1, 2, 6, 0.0F, false));
            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 25, 37, 1.0F, -2.0F, 2.0F, 3, 2, 1, 0.0F, false));
            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 0, 6, 1.0F, -2.0F, -3.0F, 3, 2, 1, 0.0F, false));
            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 0, 22, 4.0F, 4.0F, -2.0F, 1, 5, 4, 0.0F, false));
            this.armorLeftLeg = new ModelRenderer(this);
            this.armorLeftLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 0, 67, -3.0F, 0.0F, 2.0F, 5, 2, 2, 0.0F, false));
            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 69, 69, -1.0F, 4.0F, 2.0F, 3, 1, 2, 0.0F, false));
            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 54, 11, -1.0F, 4.0F, -4.0F, 3, 1, 2, 0.0F, false));
            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 65, 11, -4.0F, 2.0F, 2.0F, 6, 2, 2, 0.0F, false));
            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 64, 64, -4.0F, 2.0F, -4.0F, 6, 2, 2, 0.0F, false));
            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 66, 66, -3.0F, 6.0F, -4.0F, 5, 5, 8, 0.0F, false));
            this.armorRightLeg = new ModelRenderer(this);
            this.armorRightLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 0, 70, -1.0F, 0.0F, 2.0F, 5, 2, 2, 0.0F, false));
            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 68, 0, 0.0F, 4.0F, 2.0F, 3, 1, 2, 0.0F, false));
            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 59, 3, 0.0F, 4.0F, -4.0F, 3, 1, 2, 0.0F, false));
            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 66, 66, -2.0F, 2.0F, 2.0F, 6, 2, 2, 0.0F, false));
            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 68, 66, -2.0F, 2.0F, -4.0F, 6, 2, 2, 0.0F, false));
            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 69, 69, -1.0F, 6.0F, -4.0F, 5, 5, 8, 0.0F, false));
            this.armorLeftBoot = new ModelRenderer(this);
            this.armorLeftBoot.setRotationPoint(-2.0F, 12.0F, 0.0F);
            this.armorLeftBoot.cubeList.add(new ModelBox(this.armorLeftBoot, 24, 53, -3.0F, 10.0F, -4.0F, 5, 2, 10, 0.0F, false));
            this.armorLeftBoot.cubeList.add(new ModelBox(this.armorLeftBoot, 51, 53, -3.0F, 8.0F, -4.0F, 5, 2, 2, 0.0F, false));
            this.armorRightBoot = new ModelRenderer(this);
            this.armorRightBoot.setRotationPoint(2.0F, 12.0F, 0.0F);
            this.armorRightBoot.cubeList.add(new ModelBox(this.armorRightBoot, 51, 0, -1.0F, 10.0F, -4.0F, 5, 2, 10, 0.0F, false));
            this.armorRightBoot.cubeList.add(new ModelBox(this.armorRightBoot, 52, 40, -1.0F, 8.0F, -4.0F, 5, 2, 2, 0.0F, false));
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

        public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
            super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
        }
    }
}
