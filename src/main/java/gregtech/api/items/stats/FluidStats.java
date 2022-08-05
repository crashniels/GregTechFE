package gregtech.api.items.stats;

import alexiil.mc.lib.attributes.fluid.GroupedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import gregtech.api.capability.impl.fluid.FullOrEmptyFluidHandlerItem;
import gregtech.api.capability.impl.fluid.SimpleFluidHandlerItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.ItemStack;

public class FluidStats {

    public final FluidAmount maxCapacity;
    public final boolean allowPartialFill;

    public FluidStats(FluidAmount maxCapacity, boolean allowPartialFill) {
        this.maxCapacity = maxCapacity;
        this.allowPartialFill = allowPartialFill;
    }

    public GroupedFluidInv createImplementation(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess) {
        if (allowPartialFill) {
            return new SimpleFluidHandlerItem(stack, excess, maxCapacity);
        }
        return new FullOrEmptyFluidHandlerItem(stack, excess, maxCapacity);
    }
}
