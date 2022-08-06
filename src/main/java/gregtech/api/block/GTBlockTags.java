package gregtech.api.block;

import gregtech.api.GTValues;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class GTBlockTags {
    public static Tag.Identified<Block> WRENCH_MINEABLE;

    private static Tag.Identified<Block> register(String name) {
        return TagFactory.BLOCK.create(new Identifier(GTValues.COMMON_TAG_NAMESPACE, name));
    }

    static {
        WRENCH_MINEABLE = register("mineable/wrench");
    }
}
