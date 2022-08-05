package gregtech.api.recipes.recipes;

import alexiil.mc.lib.attributes.fluid.FixedFluidInvView;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gregtech.api.recipes.CacheableMachineRecipe;
import gregtech.api.recipes.MachineRecipe;
import gregtech.api.recipes.RecipeSerializer;
import gregtech.api.recipes.context.GeneratorMachineContext;
import gregtech.api.recipes.context.RecipeContext;
import gregtech.api.recipes.util.RecipeUtil;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.Collections;
import java.util.List;

public class GeneratorFuelRecipe<C extends GeneratorMachineContext> implements MachineRecipe<C>, CacheableMachineRecipe {

    protected final Identifier id;
    protected final FluidVolume fuelFluid;
    protected final int duration;
    protected final int EUt;

    public GeneratorFuelRecipe(Identifier id, FluidVolume fuelFluid, int duration, int EUt) {
        this.id = id;
        this.fuelFluid = fuelFluid;
        this.duration = duration;
        this.EUt = EUt;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.GENERATOR_FUEL_RECIPE;
    }

    @Override
    public Class<? extends RecipeContext> getMinimumSupportedContextClass() {
        return GeneratorMachineContext.class;
    }

    public FluidVolume getFuelFluid() {
        return fuelFluid;
    }

    public int getDuration() {
        return duration;
    }

    public int getEUt() {
        return EUt;
    }

    private FluidAmount getFuelInInventory(FixedFluidInvView fluidInvView) {
        FluidAmount fuelStored = FluidAmount.ZERO;

        for (int i = 0; i < fluidInvView.getTankCount(); i++) {
            FluidVolume fluidVolume = fluidInvView.getInvFluid(i);
            if (fluidVolume.canMerge(this.fuelFluid)) {
                fuelStored = fuelStored.add(fluidVolume.getAmount_F());
            }
        }
        return fuelStored;
    }

    @Override
    public boolean matches(C context) {
        if (context.getMaxVoltage() < this.EUt) {
            return false;
        }

        FixedFluidInvView fluidInvView = context.getFluidInventory();
        FluidAmount fuelStored = getFuelInInventory(fluidInvView);

        return fuelStored.isGreaterThan(this.fuelFluid.getAmount_F());
    }

    @Override
    public void onStarted(C context) {
        FixedFluidInvView fluidInvView = context.getFluidInventory();
        FluidAmount fuelStored = getFuelInInventory(fluidInvView);

        int maxMultiplier = (int) Math.floor(fuelStored.div(this.fuelFluid.getAmount_F()).asInexactDouble());
        int recipeMultiplier = (int) (context.getMaxVoltage() / this.EUt);

        int actualMultiplier = Math.min(maxMultiplier, recipeMultiplier);

        FluidVolume consumedFuel = this.fuelFluid.withAmount(fuelStored);

        RecipeUtil.consumeFluids(context.getFluidInventory(), Collections.singletonList(consumedFuel));
        context.setGeneratedEUt(this.EUt * actualMultiplier);
        context.setRemainingRecipeDuration(this.duration);
    }

    @Override
    public boolean canBeCached() {
        return true;
    }

    @Override
    public List<Item> getReferencedItems() {
        return Collections.emptyList();
    }

    @Override
    public List<FluidKey> getReferencedFluids() {
        return Collections.singletonList(this.fuelFluid.getFluidKey());
    }

    @Override
    public boolean canFitOutputs(C context) {
        return true;
    }

    @Override
    public void addOutputs(C context) {
    }

    public static final class Serializer implements RecipeSerializer<GeneratorFuelRecipe<?>> {

        @Override
        public GeneratorFuelRecipe<?> read(Identifier id, JsonObject json) {
            FluidVolume fuel = FluidVolume.fromJson(JsonHelper.getObject(json, "fuel"));
            int duration = JsonHelper.getInt(json, "duration");
            int recipeEUt = JsonHelper.getInt(json, "eu_per_tick");

            if (fuel.isEmpty()) {
                throw new JsonParseException("Invalid fuel: should not be empty");
            }
            if (duration <= 0) {
                throw new JsonParseException("Invalid duration: should be positive");
            }
            if (recipeEUt <= 0) {
                throw new JsonParseException("Invalid eu_per_tick: should be positive");
            }
            return new GeneratorFuelRecipe<>(id, fuel, duration, recipeEUt);
        }

        @Override
        public GeneratorFuelRecipe<?> read(Identifier id, PacketByteBuf buf) {
            FluidVolume fuel = BasicMachineRecipeSerializer.fluidVolumeFromMCBuffer(buf);
            int duration = buf.readVarInt();
            int recipeEUt = buf.readVarInt();

            return new GeneratorFuelRecipe<>(id, fuel, duration, recipeEUt);
        }

        @Override
        public void write(PacketByteBuf buf, GeneratorFuelRecipe<?> recipe) {
            recipe.getFuelFluid().toMcBuffer(buf);
            buf.writeVarInt(recipe.getDuration());
            buf.writeVarInt(recipe.getEUt());
        }
    }
}
