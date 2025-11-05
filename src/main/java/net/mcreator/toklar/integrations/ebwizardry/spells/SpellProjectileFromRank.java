package net.mcreator.toklar.integrations.ebwizardry.spells;

import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.data.WizardData;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SpellProjectileFromRank extends Spell {

    private final String projectileName;
    private final SpellRank rank;

    public SpellProjectileFromRank(SpellRank rank) {
        super("toklar", rank.projectileName + "_rank" + rank.level, SpellActions.POINT, false);
        this.projectileName = rank.projectileName;
        this.rank = rank;
        addProperties(new String[] { "damage", "count" });

        MinecraftForge.EVENT_BUS.register(SpellProjectileFromRank.class);
    }

    private static final Map<UUID, SpellState> activeCasts = new HashMap<>();

    private static class SpellState {
        public final World world;
        public final EntityPlayer caster;
        public final Object projectileInfo;
        public final int bonusDamage;
        public int remaining;
        public int tickCounter = 0;

        public SpellState(World world, EntityPlayer caster, Object projectileInfo, int count, int bonusDamage) {
            this.world = world;
            this.caster = caster;
            this.projectileInfo = projectileInfo;
            this.bonusDamage = bonusDamage;
            this.remaining = count;
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Iterator<Map.Entry<UUID, SpellState>> it = activeCasts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, SpellState> entry = it.next();
            SpellState state = entry.getValue();
            state.tickCounter++;

            if (state.tickCounter >= 4 && state.remaining > 0) {
                state.tickCounter = 0;
                try {
                    Class<?> infoClass = Class.forName("com.lycanitesmobs.core.info.projectile.ProjectileInfo");
                    Method createProjectile = infoClass.getMethod("createProjectile", World.class, EntityLivingBase.class);
                    Object projectile = createProjectile.invoke(state.projectileInfo, state.world, state.caster);
                    Method setBonusDamage = projectile.getClass().getMethod("setBonusDamage", int.class);
                    setBonusDamage.invoke(projectile, state.bonusDamage);
                    state.world.spawnEntity((Entity) projectile);
                } catch (Exception e) {
                    System.err.println("[Toklar] Failed to fire delayed projectile: " + state.projectileInfo);
                    e.printStackTrace();
                }
                state.remaining--;
            }

            if (state.remaining <= 0) it.remove();
        }
    }

    @Override
    public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
        if (world.isRemote) return true;

        WizardData data = WizardData.get(caster);
        if (data == null) return false;

        try {
            Class<?> managerClass = Class.forName("com.lycanitesmobs.core.info.projectile.ProjectileManager");
            Object manager = managerClass.getMethod("getInstance").invoke(null);
            Object info = managerClass.getMethod("getProjectile", String.class).invoke(manager, projectileName);
            if (info == null) {
                System.err.println("[Toklar] Unknown projectile: " + projectileName);
                return false;
            }

            int count = Math.max(1, getProperty("count").intValue());
            float baseDamage = getProperty("damage").floatValue();
            float potency = modifiers.get(SpellModifiers.POTENCY);
            int bonusDamage = Math.round(baseDamage * potency);

            activeCasts.put(caster.getUniqueID(), new SpellState(world, caster, info, count, bonusDamage));
            System.out.println("[Toklar] Scheduled " + count + " projectiles for " + projectileName);

        } catch (Exception e) {
            System.err.println("[Toklar] Failed to schedule projectile: " + projectileName);
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == WizardryItems.spell_book || item == WizardryItems.scroll;
    }
}