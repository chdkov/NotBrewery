package wtf.chdkov.notbrewery.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;
import wtf.chdkov.notbrewery.NBrewItems;

public class NutrientMixItem extends Item implements PolymerItem {

    public NutrientMixItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult useOn(final UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        ItemStack nutrientMixStack = context.getItemInHand();
        var state = level.getBlockState(pos);
        if (state.is(Blocks.BROWN_MUSHROOM) && !level.isClientSide() && context.getPlayer() instanceof ServerPlayer player) {
            if (level.getRandom().nextFloat() > 0.25f) {
                ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        5, 0.2, 0.2, 0.2, 0.05);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.BLOCKS, 0.4f, 1.5f);

                var amount = level.getRandom().nextInt(2) + 1;
                Block.popResource(level, pos, new ItemStack(NBrewItems.YEAST, amount));
            } else {
                level.destroyBlock(pos, false);
                level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 0.5f);
            }
            nutrientMixStack.consume(1, player);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {return Items.TRIAL_KEY;}

}
