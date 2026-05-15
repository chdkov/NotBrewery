package wtf.chdkov.notbrewery.block.cauldron.state.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import static wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlock.NBrewCauldronContent.WATER;

public class EmptyState extends CauldronInteraction implements CauldronState {

    public EmptyState() {
        register(CauldronInteraction::fillLavaInteraction, Items.LAVA_BUCKET);
        register(CauldronInteraction::fillWaterInteraction, Items.WATER_BUCKET);
        register(CauldronInteraction::fillPowderSnowInteraction, Items.POWDER_SNOW_BUCKET);
        register((blockEntity, itemInHand, state, level, pos, player, hand, hitResult)-> {
                PotionContents potion = itemInHand.get(DataComponents.POTION_CONTENTS);
                if (potion != null && potion.is(Potions.WATER)) {
                    if (!level.isClientSide()) {
                        player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, new ItemStack(Items.GLASS_BOTTLE)));
                        player.awardStat(Stats.USE_CAULDRON);
                        player.awardStat(Stats.ITEM_USED.get(itemInHand.getItem()));
                        level.setBlockAndUpdate(pos, state.cycle(LEVEL).setValue(CONTENT, WATER));
                        blockEntity.setCauldronState(CauldronStates.FILLED);
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                    }
                    return InteractionResult.SUCCESS;
                } else {return InteractionResult.TRY_WITH_EMPTY_HAND;}}, Items.POTION);
    }


    @Override
    public InteractionResult onInteract(NBrewCauldronBlockEntity blockEntity, ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.dispatch(blockEntity, itemInHand, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void handlePrecipitation(final NBrewCauldronBlockEntity blockEntity, final BlockState state, final Level level, final BlockPos pos, final Biome.Precipitation precipitation) {
        if (CauldronState.shouldHandlePrecipitation(level, precipitation)) {
            var newState = state.setValue(CONTENT, switch (precipitation) {
                case NONE -> null;
                case RAIN -> WATER;
                case SNOW -> NBrewCauldronBlock.NBrewCauldronContent.POWDER_SNOW;}
            );
            level.setBlockAndUpdate(pos, newState);
            blockEntity.setCauldronState(CauldronStates.FILLED);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        }
    }

    @Override
    public void tick(final NBrewCauldronBlockEntity blockEntity, final BlockState cauldronState, final ServerLevel level, final BlockPos pos){}

    @Override
    public String getName() {
        return "empty";
    }
}
