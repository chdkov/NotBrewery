package wtf.chdkov.notbrewery;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlock;

import java.util.function.Function;

import static wtf.chdkov.notbrewery.Notbrewery.id;

public class NBrewBlocks {
    public static void init() {}

    public static final Block CAULDRON = registerBlock("cauldron", BlockBehaviour.Properties.of().strength(2.0F).mapColor(MapColor.STONE).noOcclusion().pushReaction(PushReaction.BLOCK).requiresCorrectToolForDrops().overrideLootTable(Blocks.CAULDRON.getLootTable()), NBrewCauldronBlock::new);

    public static <T extends Block> T registerBlock(String path, BlockBehaviour.Properties settings, Function<BlockBehaviour.Properties, T> function) {
        var id = id(path);
        var item = function.apply(settings.setId(ResourceKey.create(Registries.BLOCK, id)));
        if (item == null) {
            return null;
        }
        return Registry.register(BuiltInRegistries.BLOCK, id, item);
    }
}
