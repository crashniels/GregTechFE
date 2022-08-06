package gregtech;

import gregtech.api.enchants.GTEnchantments;
import gregtech.api.items.GTItemGroups;
import gregtech.api.items.material.MaterialItemRegistry;
import gregtech.api.unification.Materials;
import gregtech.api.unification.element.Elements;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.flags.MaterialFlags;
import gregtech.api.unification.ore.OreVariants;
import gregtech.api.worldgen.populator.OreVeinPopulators;
import gregtech.common.items.GTItems;
import net.fabricmc.api.ModInitializer;

public class GregTechFE implements ModInitializer {

    @Override
    public void onInitialize() {
        /* API */
        GTItemGroups.init();
        GTEnchantments.init();
        MaterialFlags.init();
        MaterialItemRegistry.INSTANCE.registerMaterialItems();
        Elements.init();
        OreVariants.init();
        OreVeinPopulators.ensureInitialized();

        /* Common */
        GTItems.init();
    }

}
