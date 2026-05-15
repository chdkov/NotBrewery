package wtf.chdkov.notbrewery.block.cauldron.state.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlockEntity;

public class CookingState implements CauldronState {

    @Override
    public void tick(final NBrewCauldronBlockEntity blockEntity, final BlockState cauldronState, final ServerLevel level, final BlockPos pos) {}


    @Override
    public String getName() {
        return "cooking";
    }
}
