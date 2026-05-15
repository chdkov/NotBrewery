package wtf.chdkov.notbrewery.block.cauldron.state;

import wtf.chdkov.notbrewery.block.cauldron.state.impl.*;

public class CauldronStates {
    public static final CauldronState EMPTY = new EmptyState();
    public static final CauldronState MASHING = new MashingState();
    public static final CauldronState COOKING = new CookingState();
    public static final CauldronState FILLED = new FilledState();

    public static CauldronState fromName(String name) {
        return switch (name) {
            case "cooking" -> COOKING;
            case "mashing" -> MASHING;
            case "filled" -> FILLED;
            default -> EMPTY;
        };
    }
}
