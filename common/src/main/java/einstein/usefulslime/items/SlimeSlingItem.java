package einstein.usefulslime.items;

import einstein.usefulslime.init.ModItems;
import einstein.usefulslime.util.BounceHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import javax.annotation.Nullable;
import java.util.Map;

@SuppressWarnings({"depercation"})
public class SlimeSlingItem extends SwordItem {

    public SlimeSlingItem(Properties properties) {
        super(Tiers.WOOD, 0, 0, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }

        int timeUsed = getUseDuration(stack) - timeLeft;
        float i = timeUsed / 24F;
        i = (i * i + i * 2) * 1.33F;

        if (i > 4) {
            i = 4;
        }

        i = i * 0.5F;
        //player.displayClientMessage(Component.translatable("i: " + i), false);

        LivingEntity targetEntity = null;
        boolean invertDirection = false;

        Entity nearestEntity = getEntityLookedAt(level, player, 7);
        if (nearestEntity != null) {
            if ((nearestEntity instanceof LivingEntity hitEntity)) {
                targetEntity = hitEntity;
            }
        } else {
            HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
            if (hitResult != null) {
                targetEntity = player;
                invertDirection = true;
            }
        }

        if (targetEntity != null) {
            if (invertDirection) {
                i = -i;
            }

            int knockBackLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, stack);
            if (knockBackLevel > 0) {
                i = i * (1F + (knockBackLevel * knockBackLevel) / 1.75F);
            }

            Vec3 vec3 = player.getLookAngle().normalize();

            if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.SLIME_CHESTPLATE.get()) && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.SLIME_HELMET.get())) {
                i += 2;
            }

            if (player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.SLIME_BOOTS.get()) && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.SLIME_LEGGINGS.get())) {
                i += 2;
            }

            if (targetEntity.isInWaterOrBubble()) {
                i /= 2;
            }

            targetEntity.push(vec3.x * i, vec3.y * i, vec3.z * i);
            BounceHandler.addBounceHandler(targetEntity);
            EquipmentSlot slot = stack.equals(player.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;

            int damageModifier = 1 + ((1 + knockBackLevel) * (1 + knockBackLevel));
            int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
            if (unbreakingLevel >= damageModifier) {
                damageModifier = 1;
            } else {
                damageModifier = damageModifier - unbreakingLevel;
            }

            stack.hurtAndBreak(damageModifier, player, it -> it.broadcastBreakEvent(slot));

            if (i > 0.5F && i < 2F) {
                player.playSound(SoundEvents.SLIME_JUMP_SMALL, 1, 1);
            } else if (i >= 2F) {
                player.playSound(SoundEvents.SLIME_JUMP, 1, 1);
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredientStack) {
        return ingredientStack.is(Items.SLIME_BALL);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Nullable
    private Entity getEntityLookedAt(Level level, Player player, double range) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookDirection = player.getLookAngle().normalize();
        Vec3 reachPosition = eyePosition.add(lookDirection.scale(range));
        AABB boundingBox = new AABB(eyePosition, reachPosition).inflate(0.1D);
        TargetingConditions targetingConditions = TargetingConditions.forNonCombat().range(range);
        Entity nearestEntity = level.getNearestEntity(level.getEntitiesOfClass(LivingEntity.class, boundingBox, e -> e != player),
                targetingConditions,
                player,
                eyePosition.x,
                eyePosition.y,
                eyePosition.z );
        Entity nearestPlayer = level.getNearestEntity(level.getEntitiesOfClass(Player.class, boundingBox, e -> e != player),
                targetingConditions,
                player,
                eyePosition.x,
                eyePosition.y,
                eyePosition.z );
        if (nearestPlayer != null)
            return nearestPlayer;
        else
            return nearestEntity;
    }
}
