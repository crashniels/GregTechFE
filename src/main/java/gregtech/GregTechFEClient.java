package gregtech;

import gregtech.api.items.material.MaterialItemRegistry;
import net.fabricmc.api.ClientModInitializer;

public class GregTechFEClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MaterialItemRegistry.INSTANCE.registerMaterialItemsClient();
    }

}
