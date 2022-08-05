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

    public static ItemGroup MATERIALS;
    public static ItemGroup TOOLS;
    public static ItemGroup ORES;


    public static void init() {
    }

}
