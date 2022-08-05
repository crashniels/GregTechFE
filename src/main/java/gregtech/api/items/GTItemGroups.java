package gregtech.api.items;

import gregtech.common.items.GTItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class GTItemGroups {
    public static final ItemGroup MAIN = FabricItemGroupBuilder.build(
            new Identifier("gregtech", "main"),
            Items.GRANITE::getDefaultStack);

    public static final ItemGroup MATERIALS = FabricItemGroupBuilder.build(
            new Identifier("gregtech", "materials"),
            Items.GRANITE::getDefaultStack);

    public static final ItemGroup TOOLS = FabricItemGroupBuilder.build(
            new Identifier("gregtech", "tools"),
            Items.GRANITE::getDefaultStack);

    public static final ItemGroup ORES = FabricItemGroupBuilder.build(
            new Identifier("gregtech", "ores"),
            Items.GRANITE::getDefaultStack);


    public static void init() {
    }

}
