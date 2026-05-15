package wtf.chdkov.notbrewery.block.cauldron;

import com.mojang.serialization.MapCodec;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import wtf.chdkov.notbrewery.Notbrewery;

import static net.minecraft.world.level.block.LayeredCauldronBlock.FILLED_SHAPES;
import static wtf.chdkov.notbrewery.Notbrewery.LOG;
import static wtf.chdkov.notbrewery.Notbrewery.id;

public class NBrewCauldronBlock extends BaseEntityBlock implements PolymerTexturedBlock, FactoryBlock {
    private static final MapCodec<NBrewCauldronBlock> CODEC = simpleCodec(NBrewCauldronBlock::new);
    public static final EnumProperty<NBrewCauldronContent> CONTENT = EnumProperty.create("content", NBrewCauldronContent.class);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
    public static final TagKey<Item> START_COOKING_ITEM = TagKey.create(Registries.ITEM, Notbrewery.id("start_cooking_item"));
    private static final Vector3f Level3 = new Vector3f(0, 0.9375f, 0);
    private static final Vector3f Level2 = new Vector3f(0, 0.75f, 0);
    private static final Vector3f Level1 = new Vector3f(0, 0.5625f, 0);
    private static final Vector3f Level0 = new Vector3f(0, 0.2f, 0);

    public NBrewCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CONTENT, NBrewCauldronContent.EMPTY).setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTENT, LEVEL);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return Items.CAULDRON.getDefaultInstance();
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext packetContext) {
        return Blocks.CAULDRON.defaultBlockState();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
        return new NBrewCauldronBlockEntity(worldPosition, blockState);
    }

    @Override
    protected @NonNull VoxelShape getEntityInsideCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final Entity entity) {
        int s = state.getValue(LEVEL);
        return FILLED_SHAPES[s != 0 ? s - 1 : 0];
    }

    @Override
    protected void entityInside(
            final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise
    ) {
        if (level.getBlockEntity(pos) instanceof NBrewCauldronBlockEntity blockEntity) {
            blockEntity.getCaulsronState().onEntityInside(blockEntity, state, level, pos, entity, effectApplier);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof NBrewCauldronBlockEntity blockEntity) {
            return blockEntity.getCaulsronState().onInteract(blockEntity, itemStack, state, level, pos, player, hand, hitResult);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        LOG.info("useWithoutItem");
        return InteractionResult.PASS;
    }

    @Override
    protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
        return state.getValue(LEVEL);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return state.getValue(CONTENT) != NBrewCauldronContent.EMPTY;
    }

    public boolean isFull(final BlockState state) {
        return state.getValue(LEVEL) == 3;
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (level.getBlockEntity(pos) instanceof NBrewCauldronBlockEntity blockEntity) {
            blockEntity.getCaulsronState().handlePrecipitation(blockEntity, state, level, pos, precipitation);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return NBrewCauldronBlockEntity::ticker;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    public static final class Model extends BlockModel {
        private static final ItemStack lava = createModelStack("cauldron_lava");
        private static final ItemStack powder_snow = createModelStack("cauldron_powder_snow");
        private static final ItemStack water = createModelStack("cauldron_liquid");
        private final ItemStack liquid = createModelStack("cauldron_liquid");
        private final ItemDisplayElement element;

        private static ItemStack createModelStack(String model) {
            var i = new ItemStack(Items.TRIAL_KEY);
            i.set(DataComponents.ITEM_MODEL, id(model));
            return i;
        }

        public Model(BlockState state) {
            this.element = ItemDisplayElementUtil.createSimple(ItemStack.EMPTY);
            this.syncVisuals(state);
            this.addElement(this.element);
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                this.syncVisuals(this.blockState());
            }
        }

        private NBrewCauldronBlockEntity getBlockEntity() {
            var attachment = this.getAttachment();
            if (attachment == null) return null;
            return attachment.getWorld().getBlockEntity(this.blockPos()) instanceof NBrewCauldronBlockEntity be ? be : null;
        }

        // Single Source of Truth
        // fix проблему анигиляции модельки нахер при перезаходе
        private void syncVisuals(BlockState state) {
            this.element.setItem(switch (state.getValue(CONTENT)) {
                case EMPTY -> ItemStack.EMPTY;
                case LAVA -> lava;
                case POWDER_SNOW -> powder_snow;
                case WATER -> water;
            });
            this.element.setTranslation(switch (state.getValue(LEVEL)) {
                case 0 -> Level0;
                case 1 -> Level1;
                case 2 -> Level2;
                case 3 -> Level3;
                default -> throw new IllegalStateException("Unexpected level value: " + state.getValue(LEVEL));
            });
            this.tick();
        }
    }

    public enum NBrewCauldronContent implements StringRepresentable {
        EMPTY("empty", Items.BUCKET, SoundEvents.BUCKET_FILL),
        WATER("water", Items.WATER_BUCKET, SoundEvents.BUCKET_FILL),
        LAVA("lava", Items.LAVA_BUCKET, SoundEvents.BUCKET_FILL_LAVA),
        POWDER_SNOW("powder_snow", Items.POWDER_SNOW_BUCKET, SoundEvents.BUCKET_FILL_POWDER_SNOW);

        private final String name;
        private final Item fillBucketItem;
        private final SoundEvent fillSound;

        NBrewCauldronContent(String name, Item fillBucketItem, SoundEvent fillSound) {
            this.name = name;
            this.fillBucketItem = fillBucketItem;
            this.fillSound = fillSound;
        }

        public Item getFillBucketItem() { return fillBucketItem;}
        public SoundEvent getFillSound() { return fillSound;}

        @Override
        public @NonNull String getSerializedName() {
            return this.name;
        }
    }
}