package wtf.chdkov.notbrewery;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static wtf.chdkov.notbrewery.Notbrewery.id;

public class NBrewTags {

    private static TagKey<Item> bind(final String path) {
        return TagKey.create(Registries.ITEM, id(path));
    }
}
