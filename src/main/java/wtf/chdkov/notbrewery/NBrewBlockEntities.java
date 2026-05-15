package wtf.chdkov.notbrewery;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import wtf.chdkov.notbrewery.block.cauldron.NBrewCauldronBlockEntity;

import static wtf.chdkov.notbrewery.Notbrewery.id;

public class NBrewBlockEntities {
    public static BlockEntityType<NBrewCauldronBlockEntity> CAULDRON;

    public static void init() {

        CAULDRON = register("cauldron", FabricBlockEntityTypeBuilder.create(NBrewCauldronBlockEntity::new, NBrewBlocks.CAULDRON).build());

    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, BlockEntityType<T> block) {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id(path), block);
        PolymerBlockUtils.registerBlockEntity(block);
        return block;
    }
}
