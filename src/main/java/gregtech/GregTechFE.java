package gregtech;

import gregtech.api.enchants.GTEnchantments;
import gregtech.api.items.GTItemGroups;
import gregtech.common.items.GTItems;
import net.fabricmc.api.ModInitializer;

public class GregTechFE implements ModInitializer {

    @Override
    public void onInitialize() {
        GTItemGroups.init();
        GTItems.init();
        GTEnchantments.init();
    }

}
