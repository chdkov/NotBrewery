package wtf.chdkov.notbrewery;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Notbrewery implements ModInitializer {
    public static final String MOD_ID = "notbrewery";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path ) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOG.info("Say Not Brewery!");

        NBrewBlocks.init();
        NBrewBlockEntities.init();
        NBrewItems.init();

/*        UseItemCallback.EVENT.register((player, level, handIn)-> {
            if (handIn == InteractionHand.MAIN_HAND ) {
                var stackInHand = player.getItemInHand(handIn);
                var stackInOtherHand = player.getItemInHand(InteractionHand.OFF_HAND);
                if (stackInHand.is(Items.WHEAT) && stackInOtherHand.is(ItemTags.SWORDS) || stackInOtherHand.is(Items.WHEAT) && stackInHand.is(ItemTags.SWORDS)) {
                    var wheatStack = stackInHand.is(Items.WHEAT) ? stackInHand : stackInOtherHand;
                    var wheatEars = new ItemStack(NBrewItems.WHEAT_EARS, level.getRandom().nextInt(3) + 1);
                    wheatStack.consume(1, player);
                    if (!player.getInventory().add(wheatEars)) {
                        player.drop(wheatEars, false);
                    }
                    var swordStack = stackInHand.is(ItemTags.SWORDS) ? stackInHand : stackInOtherHand;
                    swordStack.hurtAndBreak(1, player, stackInHand.is(ItemTags.SWORDS) ? handIn : InteractionHand.OFF_HAND);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0f, 1.0f);

                    return InteractionResult.SUCCESS_SERVER;
                }
            }
            return InteractionResult.PASS;
        });*/

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }
}
