package wtf.chdkov.notbrewery.block.cauldron.state;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlock;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlockEntity;
import wtf.chdkov.notbrewery.block.cauldron.state.impl.CauldronState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static wtf.chdkov.notbrewery.Notbrewery.LOG;
import static wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlock.CONTENT;
import static wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlock.LEVEL;
import static wtf.chdkov.notbrewery.block.cauldron.state.impl.FilledState.lowerFillLevel;

public abstract class CauldronInteraction {

    @FunctionalInterface
    public interface InteractionAction {
        InteractionResult apply(NBrewCauldronBlockEntity blockEntity, ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult);
    }

    private final Map<Item, InteractionAction> interactionMap = new HashMap<>();
    private final Map<TagKey<Item>, InteractionAction> tagsInteractionMap = new HashMap<>();


    protected void register(InteractionAction action, Item... items) {
        for (Item item : items) {
            interactionMap.put(item, action);
        }
    }

    protected void register(TagKey<Item> tag, InteractionAction action) {
        tagsInteractionMap.put(tag, action);
    }

    public InteractionResult dispatch(NBrewCauldronBlockEntity blockEntity, ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var action = interactionMap.get(itemInHand.getItem());
        if (action != null) {
            return action.apply(blockEntity, itemInHand, state, level, pos, player, hand, hitResult);
        }

        for (Map.Entry<TagKey<Item>, InteractionAction> entry : tagsInteractionMap.entrySet()) {
            if (itemInHand.is(entry.getKey())) {
                return entry.getValue().apply(blockEntity, itemInHand, state, level, pos, player, hand, hitResult);
            }
        }
        LOG.info("no interactions for: {}", itemInHand.getItemName());
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

//  Helpers & base Interactions

    public static InteractionResult fillLavaInteraction(
            final NBrewCauldronBlockEntity blockEntity, final ItemStack itemInHand, BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final HitResult hitResult
            ) {
        return isUnderWater(level, pos)
                ? InteractionResult.CONSUME
                : emptyBucket( blockEntity, level, pos, player, hand, itemInHand, state,
                state.setValue(CONTENT, NBrewCauldronBlock.NBrewCauldronContent.LAVA).setValue(LEVEL, 3),
                CauldronStates.FILLED, SoundEvents.BUCKET_EMPTY_LAVA,
                s -> s.getValue(LEVEL) != 3 && (s.getValue(CONTENT) == NBrewCauldronBlock.NBrewCauldronContent.LAVA || s.getValue(CONTENT) == NBrewCauldronBlock.NBrewCauldronContent.EMPTY));
    }

    public static InteractionResult fillWaterInteraction(
            final NBrewCauldronBlockEntity blockEntity, final ItemStack itemInHand, BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final HitResult hitResult
    ) {
        return emptyBucket(
                blockEntity, level, pos, player, hand, itemInHand, state,
                state.setValue(CONTENT, NBrewCauldronBlock.NBrewCauldronContent.WATER).setValue(LEVEL, 3),
                CauldronStates.FILLED, SoundEvents.BUCKET_EMPTY,
                s -> s.getValue(LEVEL) != 3 && (s.getValue(CONTENT) == NBrewCauldronBlock.NBrewCauldronContent.WATER || s.getValue(CONTENT) == NBrewCauldronBlock.NBrewCauldronContent.EMPTY));
    }

    public static InteractionResult fillPowderSnowInteraction(
            final NBrewCauldronBlockEntity blockEntity, final ItemStack itemInHand, BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final HitResult hitResult
    ) {
        return isUnderWater(level, pos)
                ? InteractionResult.CONSUME
                : emptyBucket(blockEntity, level, pos, player, hand, itemInHand, state,
                state.setValue(CONTENT, NBrewCauldronBlock.NBrewCauldronContent.POWDER_SNOW).setValue(LEVEL, 3),
                CauldronStates.FILLED, SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
                s -> s.getValue(LEVEL) != 3 && (s.getValue(CONTENT) == NBrewCauldronBlock.NBrewCauldronContent.POWDER_SNOW || s.getValue(CONTENT) == NBrewCauldronBlock.NBrewCauldronContent.EMPTY));
    }

    protected static InteractionResult emptyBucket(
            final NBrewCauldronBlockEntity blockEntity,
            final Level level,
            final BlockPos pos,
            final Player player,
            final InteractionHand hand,
            final ItemStack itemInHand,
            final BlockState state,
            final BlockState newState,
            final CauldronState newCauldronState,
            final SoundEvent soundEvent,
            final Predicate<BlockState> canFill
    ) {
        if (!canFill.test(state)) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide()) {
                Item itemUsed = itemInHand.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, new ItemStack(Items.BUCKET)));
                player.awardStat(Stats.FILL_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(itemUsed));
                level.setBlockAndUpdate(pos, newState);
                blockEntity.setCauldronState(newCauldronState);
                level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }

    protected static InteractionResult fillBucket(
            final NBrewCauldronBlockEntity blockEntity,
            final CauldronState newCauldronState,
            final Level level,
            final BlockPos pos,
            final Player player,
            final InteractionHand hand,
            final ItemStack itemInHand,
            final BlockState state,
            final BlockState newState,
            final Predicate<BlockState> canFill
    ) {
        if (!canFill.test(state)) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide()) {
                var content = state.getValue(CONTENT);
                var itemUsed = itemInHand.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, new ItemStack(content.getFillBucketItem())));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(itemUsed));
                level.setBlockAndUpdate(pos, newState);
                blockEntity.setCauldronState(newCauldronState);
                level.playSound(null, pos, content.getFillSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }

    protected static boolean isUnderWater(final Level level, final BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos.above());
        return fluidState.is(FluidTags.WATER);
    }

    protected static InteractionResult dyedItemIteration(
            final NBrewCauldronBlockEntity blockEntity, final ItemStack itemInHand, BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final HitResult hitResult
    ) {
        if (!itemInHand.has(DataComponents.DYED_COLOR) || state.getValue(CONTENT) != NBrewCauldronBlock.NBrewCauldronContent.WATER) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide()) {
                itemInHand.remove(DataComponents.DYED_COLOR);
                player.awardStat(Stats.CLEAN_ARMOR);
                lowerFillLevel(state, level, pos, blockEntity);
            }

            return InteractionResult.SUCCESS;
        }
    }

    protected static InteractionResult shulkerBoxInteraction(
            final NBrewCauldronBlockEntity blockEntity, final ItemStack itemInHand, BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final HitResult hitResult
    ) {
        Block block = Block.byItem(itemInHand.getItem());
        if (!(block instanceof ShulkerBoxBlock) || state.getValue(CONTENT) != NBrewCauldronBlock.NBrewCauldronContent.WATER) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide()) {
                ItemStack cleanedShulkerBox = itemInHand.transmuteCopy(Blocks.SHULKER_BOX, 1);
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, cleanedShulkerBox, false));
                player.awardStat(Stats.CLEAN_SHULKER_BOX);
                lowerFillLevel(state, level, pos, blockEntity);
            }

            return InteractionResult.SUCCESS;
        }
    }

    protected static InteractionResult bannerInteraction(
            final NBrewCauldronBlockEntity blockEntity, final ItemStack itemInHand, BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final HitResult hitResult
    ) {
        BannerPatternLayers patterns = itemInHand.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        if (patterns.layers().isEmpty()  || state.getValue(CONTENT) != NBrewCauldronBlock.NBrewCauldronContent.WATER) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide()) {
                ItemStack cleanedBanner = itemInHand.copyWithCount(1);
                cleanedBanner.set(DataComponents.BANNER_PATTERNS, patterns.removeLast());
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, cleanedBanner, false));
                player.awardStat(Stats.CLEAN_BANNER);
                lowerFillLevel(state, level, pos, blockEntity);
            }

            return InteractionResult.SUCCESS;
        }
    }
}