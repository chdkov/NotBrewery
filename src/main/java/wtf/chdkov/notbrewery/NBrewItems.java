package wtf.chdkov.notbrewery;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import wtf.chdkov.notbrewery.item.NutrientMixItem;

import java.util.function.Function;

import static wtf.chdkov.notbrewery.Notbrewery.id;

public class NBrewItems {
    public static void init() {}

    public static final Item WHEAT_MALT = registerItem("wheat_malt", SimplePolymerItem::new, new Item.Properties()
            .component(DataComponents.ITEM_MODEL, id("wheat_malt"))
            .component(DataComponents.ITEM_NAME, Component.translatable("item.notbrewery.wheat_malt")));
    public static final Item YEAST = registerItem("yeast", SimplePolymerItem::new, new Item.Properties()
            .component(DataComponents.ITEM_MODEL, id("yeast"))
            .component(DataComponents.ITEM_NAME, Component.translatable("item.notbrewery.yeast")));
    public static final Item NUTRIENT_MIX = registerItem("nutrient_mix", NutrientMixItem::new, new Item.Properties()
            .component(DataComponents.ITEM_MODEL, id("nutrient_mix"))
            .component(DataComponents.ITEM_NAME, Component.translatable("item.notbrewery.nutrient_mix"))
            .stacksTo(32));


    private static ResourceKey<Item> itemId(final String name) {
        return ResourceKey.create(Registries.ITEM, id(name));
    }

    private static Item registerItem(final String name, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        return registerItem(itemId(name), itemFactory, properties);
    }

    private static Item registerItem(final ResourceKey<Item> key, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        Item item = (Item) itemFactory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
}
