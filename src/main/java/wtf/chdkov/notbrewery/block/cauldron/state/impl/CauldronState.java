package wtf.chdkov.notbrewery.block.cauldron.state.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlockEntity;

public interface CauldronState {
    default InteractionResult onInteract(final NBrewCauldronBlockEntity blockEntity, ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {return InteractionResult.PASS;};

    void tick(final NBrewCauldronBlockEntity blockEntity, final BlockState cauldronState, final ServerLevel level, final BlockPos pos);

    default void onEntityInside(final NBrewCauldronBlockEntity blockEntity, final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier) {}

    default void handlePrecipitation(final NBrewCauldronBlockEntity blockEntity, final BlockState state, final Level level, final BlockPos pos, final Biome.Precipitation precipitation) {}

    String getName();

    static boolean shouldHandlePrecipitation(final Level level, final Biome.Precipitation precipitation) {
        return switch (precipitation) {
            case RAIN -> level.getRandom().nextFloat() < 0.05F;
            case SNOW -> level.getRandom().nextFloat() < 0.1F;
            default -> false;
        };
    }
}
