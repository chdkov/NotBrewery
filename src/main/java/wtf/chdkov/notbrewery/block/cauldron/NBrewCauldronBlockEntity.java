package wtf.chdkov.notbrewery.block.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import wtf.chdkov.notbrewery.NBrewBlockEntities;
import wtf.chdkov.notbrewery.block.cauldron.state.CauldronStates;
import wtf.chdkov.notbrewery.block.cauldron.state.impl.CauldronState;

public class NBrewCauldronBlockEntity extends BlockEntity {
    private CauldronState currentState = CauldronStates.EMPTY;
    private int mashProgress = 0;

    public NBrewCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(NBrewBlockEntities.CAULDRON, pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("MashProgress", this.mashProgress);
        output.putString("CurrentState", this.currentState.getName());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.currentState = CauldronStates.fromName(input.getStringOr("CurrentState", "empty"));
        this.mashProgress = input.getIntOr("MashProgress", 0);
    }

    public static <T extends BlockEntity> void ticker(Level level, BlockPos pos, BlockState state, T t) {
        if (t instanceof NBrewCauldronBlockEntity blockEntity && level instanceof ServerLevel serverLevel) {
            blockEntity.currentState.tick(blockEntity, state, serverLevel, pos);
        }
    }

    public CauldronState getCaulsronState() {
        return this.currentState;
    }

    public void setCauldronState(CauldronState newState) {
        if (this.currentState == newState) {return;}
        this.currentState = newState;
        this.setChanged();
    }

    public void mash() {
        this.mashProgress++;
    }

    public int getMashProgress() {
        return this.mashProgress;
    }
}
