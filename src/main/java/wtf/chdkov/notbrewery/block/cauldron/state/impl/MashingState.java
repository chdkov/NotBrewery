package wtf.chdkov.notbrewery.block.cauldron.state.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import wtf.chdkov.notbrewery.Notbrewery;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlockEntity;

public class MashingState implements CauldronState {

    @Override
    public void tick(final NBrewCauldronBlockEntity blockEntity, final BlockState cauldronState, final ServerLevel level, final BlockPos pos) {}

    @Override
    public String getName() {
        return "mashing";
    }

    @Override
    public void onEntityInside(NBrewCauldronBlockEntity blockEntity, BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
        if (entity instanceof ServerPlayer player && player.onGround() && player.fallDistance > 0.5F) {
            Notbrewery.LOG.info("Mash!");
            blockEntity.mash();
        }
    }
}
