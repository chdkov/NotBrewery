package wtf.chdkov.notbrewery.block.cauldron.state.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlock;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlockEntity;
import wtf.chdkov.notbrewery.block.cauldron.state.CauldronInteraction;
import wtf.chdkov.notbrewery.block.cauldron.state.CauldronStates;

import static wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlock.CONTENT;
import static wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlock.LEVEL;


public class FilledState extends CauldronInteraction implements CauldronState {

    public FilledState() {
        register(CauldronInteraction::fillLavaInteraction, Items.LAVA_BUCKET);
        register(CauldronInteraction::fillWaterInteraction, Items.WATER_BUCKET);
        register(CauldronInteraction::fillPowderSnowInteraction, Items.POWDER_SNOW_BUCKET);
//      scoop interactions
        register((blockEntity, itemInHand, state, level, pos, player, hand, hitResult)->
                fillBucket(
                        blockEntity, CauldronStates.EMPTY, level, pos, player, hand, itemInHand, state,
                        state.setValue(CONTENT, NBrewCauldronBlock.NBrewCauldronContent.EMPTY).setValue(LEVEL, 0),
                        s -> s.getValue(LEVEL) == 3), Items.BUCKET);
        register((blockEntity, itemInHand, state, level, pos, player, hand, hitResult)-> {
            if (!level.isClientSide()) {
                Item usedItem = itemInHand.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(usedItem));
                lowerFillLevel(state, level, pos, blockEntity);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResult.SUCCESS;}, Items.GLASS_BOTTLE);
        register((blockEntity, itemInHand, state, level, pos, player, hand, hitResult)-> {
            if (state.getValue(LEVEL) == 3) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            } else {
                PotionContents potion = itemInHand.get(DataComponents.POTION_CONTENTS);
                if (potion != null && potion.is(Potions.WATER)) {
                    if (!level.isClientSide()) {
                        player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, new ItemStack(Items.GLASS_BOTTLE)));
                        player.awardStat(Stats.USE_CAULDRON);
                        player.awardStat(Stats.ITEM_USED.get(itemInHand.getItem()));
                        level.setBlockAndUpdate(pos, state.cycle(LEVEL));
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                    }
                    return InteractionResult.SUCCESS;
                } else {return InteractionResult.TRY_WITH_EMPTY_HAND;}}}, Items.POTION);
        register(ItemTags.CAULDRON_CAN_REMOVE_DYE, CauldronInteraction::dyedItemIteration);
        register(CauldronInteraction::bannerInteraction,
                Items.WHITE_BANNER, Items.BLACK_BANNER, Items.BLUE_BANNER,
                Items.BROWN_BANNER, Items.CYAN_BANNER, Items.GRAY_BANNER, Items.GREEN_BANNER,
                Items.LIME_BANNER, Items.LIGHT_BLUE_BANNER, Items.MAGENTA_BANNER, Items.ORANGE_BANNER,
                Items.RED_BANNER, Items.YELLOW_BANNER, Items.LIGHT_GRAY_BANNER,
                Items.PINK_BANNER, Items.PURPLE_BANNER
        );
        register(CauldronInteraction::shulkerBoxInteraction,
                Items.WHITE_SHULKER_BOX, Items.BLACK_SHULKER_BOX,
                Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX,
                Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.LIME_SHULKER_BOX,
                Items.LIGHT_BLUE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX,
                Items.RED_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX,
                Items.PINK_SHULKER_BOX, Items.PURPLE_SHULKER_BOX
        );
    }

    @Override
    public InteractionResult onInteract(NBrewCauldronBlockEntity blockEntity, ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.dispatch(blockEntity, itemInHand, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void handlePrecipitation(final NBrewCauldronBlockEntity blockEntity, final BlockState state, final Level level, final BlockPos pos, final Biome.Precipitation precipitation) {
        if (CauldronState.shouldHandlePrecipitation(level, precipitation) && state.getValue(LEVEL) != 3 && checkPrecipitation(state, precipitation)) {
            var newState = state.cycle(LEVEL);
            level.setBlockAndUpdate(pos, newState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        }
    }

    private boolean checkPrecipitation(final BlockState state, final Biome.Precipitation precipitation) {
        return switch (precipitation) {
            case RAIN -> state.getValue(CONTENT) == NBrewCauldronBlock.NBrewCauldronContent.WATER;
            case SNOW -> state.getValue(CONTENT) == NBrewCauldronBlock.NBrewCauldronContent.POWDER_SNOW;
            case NONE -> false;
        };
    }

    @Override
    public void onEntityInside(NBrewCauldronBlockEntity blockEntity, BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
        var content = state.getValue(CONTENT);
        switch (content) {
            case LAVA -> {
                effectApplier.apply(InsideBlockEffectType.CLEAR_FREEZE);
                effectApplier.apply(InsideBlockEffectType.LAVA_IGNITE);
                effectApplier.runAfter(InsideBlockEffectType.LAVA_IGNITE, Entity::lavaHurt);
                return;
            }
            case POWDER_SNOW -> {
                if (!entity.isOnFire()) {
                    effectApplier.apply(InsideBlockEffectType.FREEZE);
                    return;
                } else {
                    handleExtinguishSnow(blockEntity, state, level, pos, effectApplier);
                }
            }
            default -> handleExtinguish(blockEntity, state, level, pos, effectApplier);
        }
        effectApplier.apply(InsideBlockEffectType.EXTINGUISH);
    }

    private void handleExtinguishSnow(final NBrewCauldronBlockEntity blockEntity, final BlockState state, final Level level, final BlockPos blockPos, final InsideBlockEffectApplier effectApplier) {
        if (level instanceof ServerLevel serverLevel) {
            effectApplier.runBefore(InsideBlockEffectType.EXTINGUISH, e -> {
                if (e.isOnFire() && e.mayInteract(serverLevel, blockEntity.getBlockPos())) {
                    lowerFillLevel(state.setValue(CONTENT, NBrewCauldronBlock.NBrewCauldronContent.WATER), level, blockEntity.getBlockPos(), blockEntity);;
                }
            });
        }
    }

    private void handleExtinguish(NBrewCauldronBlockEntity blockEntity, final BlockState state, final Level level, final BlockPos blockPos, final InsideBlockEffectApplier effectApplier) {
        if (level instanceof ServerLevel serverLevel) {
            effectApplier.runBefore(InsideBlockEffectType.EXTINGUISH, e -> {
                if (e.isOnFire() && e.mayInteract(serverLevel, blockEntity.getBlockPos())) {
                    lowerFillLevel(state, level, blockEntity.getBlockPos(), blockEntity);
                }
            });
        }
    }

    public static void lowerFillLevel(final BlockState state, final Level level, final BlockPos pos, final NBrewCauldronBlockEntity blockEntity) {
        var newLevel = state.getValue(LEVEL) - 1;
        var newState = newLevel == 0
                ? state.setValue(CONTENT, NBrewCauldronBlock.NBrewCauldronContent.EMPTY).setValue(LEVEL, 0)
                : state.setValue(LEVEL, newLevel);
        level.setBlockAndUpdate(pos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        if (newLevel == 0) {blockEntity.setCauldronState(CauldronStates.EMPTY);}
    }

    @Override
    public void tick(final NBrewCauldronBlockEntity blockEntity, final BlockState state, final ServerLevel level, final BlockPos pos) {}

    @Override
    public String getName() {
        return "filled";
    }
}
