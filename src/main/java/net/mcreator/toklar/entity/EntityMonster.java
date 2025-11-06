package net.mcreator.toklar.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.init.SoundEvents;
import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.mcreator.toklar.ElementsToklar;
import net.mcreator.toklar.entity.ai.EntityAIHurtByTargetFiltered;
import net.mcreator.toklar.entity.ai.EntityAILeapTowardTarget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import com.tmtravlr.potioncore.PotionCoreAttributes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;

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
        RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
            RenderLiving<EntityCustom> renderer = new RenderLiving<EntityCustom>(
                    renderManager, new ModelPlayer(0.0F, false), 0.5f) {
                @Override
                protected ResourceLocation getEntityTexture(EntityCustom entity) {
                    return new ResourceLocation("toklar", "textures/entity/clanky_skin.png");
                }
            };

            renderer.addLayer(new LayerBipedArmor(renderer));
            renderer.addLayer(new LayerHeldItem(renderer)); 
            return renderer;
        });
    }

    public static class EntityCustom extends EntityMob {
        // Explicit constructor calling super(world)
    	public EntityCustom(World world) {
    	    super(world);
    	    setSize(0.6f, 1.8f);
    	   // experienceValue = 50;
    	    this.isImmuneToFire = false;
    	    this.stepHeight = 1.2F;
    	    setNoAI(false);
    	    enablePersistence();

      	}
    	
//    	private void assignGear(EntityEquipmentSlot slot, String modid, String itemName) {
//    	    Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, itemName));
//    	    if (item != null) {
//    	        this.setItemStackToSlot(slot, new ItemStack(item));
//    	    }
//    	}
    	@Override
    	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
    	    assignEnchantedGear(EntityEquipmentSlot.MAINHAND, "spartanfire", "battleaxe_lightning_dragonsteel", Enchantments.SHARPNESS, 5, Enchantments.UNBREAKING, 3);
    	    assignEnchantedGear(EntityEquipmentSlot.HEAD,     "iceandfire",  "dragonsteel_lightning_helmet",     Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3);
    	    assignEnchantedGear(EntityEquipmentSlot.CHEST,    "iceandfire",  "dragonsteel_lightning_chestplate", Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3);
    	    assignEnchantedGear(EntityEquipmentSlot.LEGS,     "iceandfire",  "dragonsteel_lightning_leggings",   Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3);
    	    assignEnchantedGear(EntityEquipmentSlot.FEET,     "iceandfire",  "dragonsteel_lightning_boots",      Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3);
    	    return super.onInitialSpawn(difficulty, livingdata);
    	}
    	
    	private void assignEnchantedGear(EntityEquipmentSlot slot, String modid, String itemName,
                Enchantment enchant1, int level1,
                Enchantment enchant2, int level2) {
    			Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, itemName));
    			if (item != null) {
    				ItemStack stack = new ItemStack(item);
    				stack.addEnchantment(enchant1, level1);
    				stack.addEnchantment(enchant2, level2);
    				this.setItemStackToSlot(slot, stack);
			}
    	}
    	
        public ITextComponent getDisplayName() {
            return new TextComponentString("Clanky");
        }

        @Override
        protected int getExperiencePoints(EntityPlayer player) {
            return 18487; // Enough raw XP to give 100 levels
        }
        private boolean hasSummonedWraiths = false;
        private boolean initialized = false;

        @Override
        public void onUpdate() {
            super.onUpdate();

           if (!initialized && !this.world.isRemote) {
                    System.out.println("Clanky: assigning gear in onUpdate()");

                    assignEnchantedGear(EntityEquipmentSlot.MAINHAND, "spartanfire", "battleaxe_lightning_dragonsteel", Enchantments.SHARPNESS, 5, Enchantments.UNBREAKING, 3);
                    assignEnchantedGear(EntityEquipmentSlot.HEAD,     "iceandfire",  "dragonsteel_lightning_helmet",     Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3);
                    assignEnchantedGear(EntityEquipmentSlot.CHEST,    "iceandfire",  "dragonsteel_lightning_chestplate", Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3);
                    assignEnchantedGear(EntityEquipmentSlot.LEGS,     "iceandfire",  "dragonsteel_lightning_leggings",   Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3);
                    assignEnchantedGear(EntityEquipmentSlot.FEET,     "iceandfire",  "dragonsteel_lightning_boots",      Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3);

                    this.setDropChance(EntityEquipmentSlot.HEAD, 0.0F);
                    this.setDropChance(EntityEquipmentSlot.CHEST, 0.0F);
                    this.setDropChance(EntityEquipmentSlot.LEGS, 0.0F);
                    this.setDropChance(EntityEquipmentSlot.FEET, 0.0F);
                    this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0F);

                    initialized = true;
                }
                              
            
            if (!this.world.isRemote && !hasSummonedWraiths && this.getHealth() <= this.getMaxHealth() * 0.5F) {
                hasSummonedWraiths = true;

                for (int i = 0; i < 3; i++) {
                    EntityLiving wraith = (EntityLiving) EntityList.createEntityByIDFromName(
                        new ResourceLocation("ebwizardry", "lightning_wraith"), this.world);

                    if (wraith != null) {
                        double offsetX = (this.rand.nextDouble() - 0.5D) * 6.0D;
                        double offsetZ = (this.rand.nextDouble() - 0.5D) * 6.0D;
                        wraith.setPosition(this.posX + offsetX, this.posY, this.posZ + offsetZ);
                        this.world.spawnEntity(wraith);

                        this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, wraith.posX, wraith.posY + 1, wraith.posZ, 0.0D, 0.1D, 0.0D);
                        this.world.playSound(null, wraith.posX, wraith.posY, wraith.posZ,
                            SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F);
                    }
                }
            }
        }  

        
        private int leapOrVortexCooldown = 0;
        private void triggerVortexPull() {
            List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class,
                this.getEntityBoundingBox().grow(15.0D));

            for (EntityPlayer player : players) {
                if (player.isCreative() || player.isSpectator()) continue;

                double dx = this.posX - player.posX;
                double dy = this.posY - player.posY;
                double dz = this.posZ - player.posZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < 0.1) dist = 0.1;

                double strength = 3.2D;
                player.motionX += (dx / dist) * strength;
                player.motionY += (dy / dist) * strength * 0.4;
                player.motionZ += (dz / dist) * strength;
                player.velocityChanged = true;

                if (!player.isPotionActive(MobEffects.SLOWNESS)) {
                    player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));
                }

                for (int i = 0; i < 6; i++) {
                    double px = player.posX + (this.rand.nextDouble() - 0.5D) * 2.0D;
                    double py = player.posY + this.rand.nextDouble() * 2.0D;
                    double pz = player.posZ + (this.rand.nextDouble() - 0.5D) * 2.0D;
                    this.world.spawnParticle(EnumParticleTypes.PORTAL, px, py, pz, 0.0D, 0.1D, 0.0D);
                }

                this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 1.0F, 1.0F);
            }
        }
        
        @Override
        public boolean isPotionApplicable(PotionEffect effect) {
        	if (effect.getPotion() == MobEffects.SLOWNESS ||
        		    effect.getPotion() == MobEffects.WEAKNESS ||
        		    effect.getPotion() == MobEffects.MINING_FATIGUE) {
        		    return false;
        		}
            return super.isPotionApplicable(effect);
        }
        private int targetResetTicker = 0;
        public EntityLivingBase lastExcludedTarget = null;
        private int exclusionTicksRemaining = 0;
        
        @Override
        protected void updateAITasks() {
            super.updateAITasks();

            targetResetTicker++;
            if (targetResetTicker >= 200) {
                lastExcludedTarget = this.getAttackTarget();
                exclusionTicksRemaining = 40;
                this.setAttackTarget(null);
                targetResetTicker = 0;
            }

            if (exclusionTicksRemaining > 0) {
                exclusionTicksRemaining--;
            } else {
                lastExcludedTarget = null;
            }

         // LEAP or VORTEX logic
            if (this.getAttackTarget() != null && this.onGround && leapOrVortexCooldown <= 0) {
                EntityLivingBase target = this.getAttackTarget();
                double distSq = this.getDistanceSq(target);

                if (distSq >= 64.0D) {
                    if (this.rand.nextBoolean()) {
                        double dx = target.posX - this.posX;
                        double dy = (target.posY + target.getEyeHeight()) - (this.posY + this.getEyeHeight());
                        double dz = target.posZ - this.posZ;
                        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (dist < 0.1D) dist = 0.1D;

                        double leapStrength = 2.5D;
                        this.motionX = dx / dist * leapStrength;
                        this.motionY = dy / dist * leapStrength * 0.9; // vertical dampening
                        this.motionZ = dz / dist * leapStrength;

                        this.isAirBorne = true;
                        this.velocityChanged = true;

                        if (!target.isPotionActive(MobEffects.SLOWNESS)) {
                            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1)); // 3s, level 2
                        }
                    } else {
                        triggerVortexPull();
                    }

                    leapOrVortexCooldown = 60;
                }
            }

            if (leapOrVortexCooldown > 0) {
                leapOrVortexCooldown--;
            }
        
        // fallback leap/vortex if stuck or blocked
        if (this.getAttackTarget() != null && this.onGround && leapOrVortexCooldown <= 0) {
            EntityLivingBase target = this.getAttackTarget();
            double distSq = this.getDistanceSq(target);
            boolean stuck = this.getNavigator().noPath() || this.getNavigator().getPath() == null;

            if (distSq < 64.0D && stuck) { // within 8 blocks but stuck
                if (this.rand.nextBoolean()) {
                    double dx = target.posX - this.posX;
                    double dy = (target.posY + target.getEyeHeight()) - (this.posY + this.getEyeHeight());
                    double dz = target.posZ - this.posZ;
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist < 0.1D) dist = 0.1D;

                    double leapStrength = 2.5D;
                    this.motionX = dx / dist * leapStrength;
                    this.motionY = dy / dist * leapStrength * 0.9;
                    this.motionZ = dz / dist * leapStrength;

                    this.isAirBorne = true;
                    this.velocityChanged = true;

                    if (!target.isPotionActive(MobEffects.SLOWNESS)) {
                        target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));
                    }
                } else {
                    triggerVortexPull();
                }

                leapOrVortexCooldown = 60;
            }
        }

        if (leapOrVortexCooldown > 0) {
            leapOrVortexCooldown--;
        }
    }
        @Override
        protected void initEntityAI() {
            this.tasks.addTask(0, new EntityAISwimming(this));
            this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.2D, true));
            this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
            this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
            this.tasks.addTask(5, new EntityAILookIdle(this));

            this.targetTasks.addTask(1, new EntityAIHurtByTargetFiltered(this, false));

            // Prioritize players first, excluding last target
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 16, false, false, target ->
            target != lastExcludedTarget
        ));

            // Then target mobs that attacked Clanky or are helping players, excluding last target
            this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 16, true, false, target -> {
                if (target == lastExcludedTarget) return false;
                EntityLiving living = (EntityLiving) target;
                if (living.getRevengeTarget() == this) return true;
                EntityLivingBase theirTarget = living.getAttackTarget();
                return theirTarget instanceof EntityPlayer;
            }));
        }

        @Override
        protected void applyEntityAttributes() {
            super.applyEntityAttributes();
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3200.0D); 
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D); 
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(30.0D); 
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D); 
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(2.0D);
            this.getEntityAttribute(PotionCoreAttributes.MAGIC_SHIELDING).setBaseValue(0.30D);
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
        public boolean attackEntityFrom(DamageSource source, float amount) {
            Entity attacker = source.getTrueSource();

            if (attacker instanceof EntityLivingBase) {
                double distance = this.getDistance(attacker);

                if (distance > 16.0D) {
                        this.world.playSound(null, this.posX, this.posY, this.posZ,
                        SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 1.0F, 1.0F);

                    return false; // Cancel damage
                }
            }

            return super.attackEntityFrom(source, amount);
        }
        
        @Override
        public EnumCreatureAttribute getCreatureAttribute() {
            return EnumCreatureAttribute.UNDEFINED;
        }

        @Override
        protected SoundEvent getAmbientSound() {
            return null;
        }
        
        @Override
        protected SoundEvent getHurtSound(DamageSource source) {
            return SoundEvents.ENTITY_IRONGOLEM_HURT; 
        }

        @Override
        protected SoundEvent getDeathSound() {
            return SoundEvents.ENTITY_WITHER_DEATH;
        }

        @Override
        protected float getSoundVolume() {
            return 1.0F;
        }
    }

//    public static class Modeltoklar1 extends ModelBase {
//        private final ModelRenderer armorHead;
//        private final ModelRenderer cube_r1;
//        private final ModelRenderer cube_r2;
//        private final ModelRenderer armorBody;
//        private final ModelRenderer armorLeftArm;
//        private final ModelRenderer armorRightArm;
//        private final ModelRenderer armorLeftLeg;
//        private final ModelRenderer armorRightLeg;
//        private final ModelRenderer armorLeftBoot;
//        private final ModelRenderer armorRightBoot;
//
//        public Modeltoklar1() {
//            this.textureWidth = 128;
//            this.textureHeight = 128;
//            this.armorHead = new ModelRenderer(this);
//            this.armorHead.setRotationPoint(0.0F, 0.0F, 0.0F);
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 0, -5.0F, -9.0F, -5.0F, 10, 1, 10, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 12, -4.0F, -10.0F, -4.0F, 8, 1, 8, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 25, 37, -5.0F, -8.0F, -5.0F, 1, 8, 9, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 33, 3, 4.0F, -8.0F, -5.0F, 1, 8, 9, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 31, 0, -4.0F, -8.0F, -5.0F, 8, 1, 1, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 12, -1.0F, -7.0F, -5.0F, 2, 3, 1, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 51, -5.0F, -8.0F, 4.0F, 10, 8, 1, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 47, 61, -2.0F, -4.0F, -5.0F, 4, 1, 1, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 46, 45, 2.0F, -1.0F, -5.0F, 2, 1, 1, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 17, -4.0F, -1.0F, -5.0F, 2, 1, 1, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 52, 23, 3.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 46, 48, -4.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
//            this.armorHead.cubeList.add(new ModelBox(this.armorHead, 0, 0, -1.0F, -13.0F, -1.0F, 2, 3, 2, 0.0F, false));
//            this.cube_r1 = new ModelRenderer(this);
//            this.cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
//            this.armorHead.addChild(this.cube_r1);
//            setRotationAngle(this.cube_r1, -0.2618F, 0.0F, 0.0F);
//            this.cube_r1.cubeList.add(new ModelBox(this.cube_r1, 55, 23, -1.0F, -16.0F, -6.0F, 2, 1, 7, 0.0F, false));
//            this.cube_r1.cubeList.add(new ModelBox(this.cube_r1, 45, 0, -1.0F, -15.0F, -7.0F, 2, 1, 9, 0.0F, false));
//            this.cube_r2 = new ModelRenderer(this);
//            this.cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
//            this.armorHead.addChild(this.cube_r2);
//            setRotationAngle(this.cube_r2, -0.2618F, 0.0F, 0.0F);
//            this.cube_r2.cubeList.add(new ModelBox(this.cube_r2, 0, 22, -1.0F, -14.0F, -8.0F, 2, 2, 11, 0.0F, false));
//            this.armorBody = new ModelRenderer(this);
//            this.armorBody.setRotationPoint(0.0F, 0.0F, 0.0F);
//            this.armorBody.cubeList.add(new ModelBox(this.armorBody, 0, 36, -5.0F, 0.0F, -4.0F, 10, 12, 2, 0.0F, false));
//            this.armorBody.cubeList.add(new ModelBox(this.armorBody, 27, 22, -5.0F, 0.0F, 2.0F, 10, 12, 2, 0.0F, false));
//            this.armorBody.cubeList.add(new ModelBox(this.armorBody, 46, 45, -6.0F, 5.0F, -4.0F, 1, 7, 8, 0.0F, false));
//            this.armorBody.cubeList.add(new ModelBox(this.armorBody, 44, 29, 5.0F, 5.0F, -4.0F, 1, 7, 8, 0.0F, false));
//            this.armorLeftArm = new ModelRenderer(this);
//            this.armorLeftArm.setRotationPoint(-4.0F, 2.0F, 0.0F);
//            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 17, 55, -5.0F, -3.0F, -3.0F, 4, 1, 6, 0.0F, false));
//            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 59, 55, -5.0F, -2.0F, -3.0F, 1, 2, 6, 0.0F, false));
//            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 16, 22, -5.0F, 4.0F, -2.0F, 1, 5, 4, 0.0F, false));
//            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 45, 3, -4.0F, -2.0F, 2.0F, 3, 2, 1, 0.0F, false));
//            this.armorLeftArm.cubeList.add(new ModelBox(this.armorLeftArm, 25, 41, -4.0F, -2.0F, -3.0F, 3, 2, 1, 0.0F, false));
//            this.armorRightArm = new ModelRenderer(this);
//            this.armorRightArm.setRotationPoint(4.0F, 2.0F, 0.0F);
//            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 50, 15, 1.0F, -3.0F, -3.0F, 4, 1, 6, 0.0F, false));
//            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 59, 0, 4.0F, -2.0F, -3.0F, 1, 2, 6, 0.0F, false));
//            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 25, 37, 1.0F, -2.0F, 2.0F, 3, 2, 1, 0.0F, false));
//            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 0, 6, 1.0F, -2.0F, -3.0F, 3, 2, 1, 0.0F, false));
//            this.armorRightArm.cubeList.add(new ModelBox(this.armorRightArm, 0, 22, 4.0F, 4.0F, -2.0F, 1, 5, 4, 0.0F, false));
//            this.armorLeftLeg = new ModelRenderer(this);
//            this.armorLeftLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
//            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 0, 67, -3.0F, 0.0F, 2.0F, 5, 2, 2, 0.0F, false));
//            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 69, 69, -1.0F, 4.0F, 2.0F, 3, 1, 2, 0.0F, false));
//            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 54, 11, -1.0F, 4.0F, -4.0F, 3, 1, 2, 0.0F, false));
//            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 65, 11, -4.0F, 2.0F, 2.0F, 6, 2, 2, 0.0F, false));
//            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 64, 64, -4.0F, 2.0F, -4.0F, 6, 2, 2, 0.0F, false));
//            this.armorLeftLeg.cubeList.add(new ModelBox(this.armorLeftLeg, 66, 66, -3.0F, 6.0F, -4.0F, 5, 5, 8, 0.0F, false));
//            this.armorRightLeg = new ModelRenderer(this);
//            this.armorRightLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
//            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 0, 70, -1.0F, 0.0F, 2.0F, 5, 2, 2, 0.0F, false));
//            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 68, 0, 0.0F, 4.0F, 2.0F, 3, 1, 2, 0.0F, false));
//            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 59, 3, 0.0F, 4.0F, -4.0F, 3, 1, 2, 0.0F, false));
//            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 66, 66, -2.0F, 2.0F, 2.0F, 6, 2, 2, 0.0F, false));
//            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 68, 66, -2.0F, 2.0F, -4.0F, 6, 2, 2, 0.0F, false));
//            this.armorRightLeg.cubeList.add(new ModelBox(this.armorRightLeg, 69, 69, -1.0F, 6.0F, -4.0F, 5, 5, 8, 0.0F, false));
//            this.armorLeftBoot = new ModelRenderer(this);
//            this.armorLeftBoot.setRotationPoint(-2.0F, 12.0F, 0.0F);
//            this.armorLeftBoot.cubeList.add(new ModelBox(this.armorLeftBoot, 24, 53, -3.0F, 10.0F, -4.0F, 5, 2, 10, 0.0F, false));
//            this.armorLeftBoot.cubeList.add(new ModelBox(this.armorLeftBoot, 51, 53, -3.0F, 8.0F, -4.0F, 5, 2, 2, 0.0F, false));
//            this.armorRightBoot = new ModelRenderer(this);
//            this.armorRightBoot.setRotationPoint(2.0F, 12.0F, 0.0F);
//            this.armorRightBoot.cubeList.add(new ModelBox(this.armorRightBoot, 51, 0, -1.0F, 10.0F, -4.0F, 5, 2, 10, 0.0F, false));
//            this.armorRightBoot.cubeList.add(new ModelBox(this.armorRightBoot, 52, 40, -1.0F, 8.0F, -4.0F, 5, 2, 2, 0.0F, false));
//        }
//
//        @Override
//        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
//            armorHead.render(f5);
//            armorBody.render(f5);
//            armorLeftArm.render(f5);
//            armorRightArm.render(f5);
//            armorLeftLeg.render(f5);
//            armorRightLeg.render(f5);
//            armorLeftBoot.render(f5);
//            armorRightBoot.render(f5);
//        }
//
//        public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
//            modelRenderer.rotateAngleX = x;
//            modelRenderer.rotateAngleY = y;
//            modelRenderer.rotateAngleZ = z;
//        }
//
//        @Override
//        public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
//            this.armorRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * f1;
//            this.armorLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
//            this.armorRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
//            this.armorLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * f1;
//            this.armorHead.rotateAngleY = f3 * 0.017453292F; // yaw
//            this.armorHead.rotateAngleX = f4 * 0.017453292F; // pitch
//            this.armorBody.rotateAngleX = MathHelper.sin(f2 * 0.1F) * 0.05F;
//            if (!e.onGround && e.motionY > 0) {
//                this.armorRightArm.rotateAngleX = -1.0F;
//                this.armorLeftArm.rotateAngleX = -1.0F;
//            }
//        }
//    }
}
