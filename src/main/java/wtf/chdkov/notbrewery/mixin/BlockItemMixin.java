package wtf.chdkov.notbrewery.mixin;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.chdkov.notbrewery.NBrewBlocks;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "placeBlock", at = @At(value = "HEAD"), cancellable = true)
    private void replaceToCauldron(BlockPlaceContext context, BlockState placementState, CallbackInfoReturnable<Boolean> cir) {
        if (placementState.is(Blocks.CAULDRON)) {
            boolean var = context.getLevel().setBlock(context.getClickedPos(), NBrewBlocks.CAULDRON.defaultBlockState(), 11);
            cir.setReturnValue(var);
        }
    }

}
