package gregtech.api.unification.material;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import gregtech.api.GTValues;
import gregtech.api.enchants.EnchantmentData;
import gregtech.api.unification.element.Element;
import gregtech.api.unification.material.flags.MaterialFlag;
import gregtech.api.unification.material.flags.MaterialProperty;
import gregtech.api.unification.material.properties.*;
import gregtech.api.unification.util.MaterialIconSet;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static gregtech.api.unification.material.flags.MaterialFlags.*;

public class Material implements Comparable<Material> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Material.class);
    private static final boolean MATERIAL_ERRORS_ARE_FATAL = true;
    private static final int CABLE_MINIMAL_VOLTAGE = GTValues.V[GTValues.ULV];

    public static final Registry<Material> REGISTRY = FabricRegistryBuilder
            .createSimple(Material.class, new Identifier(GTValues.MODID, "materials"))
            .buildAndRegister();

    private final Set<MaterialFlag> materialFlags;
    private final Map<MaterialProperty<?>, Object> properties;

    public Material(Settings settings) {
        this.materialFlags = ImmutableSet.copyOf(settings.materialFlags);
        this.properties = ImmutableMap.copyOf(settings.properties);
    }

    private boolean verifyMaterial() {
        boolean isMaterialValid = true;

        for (MaterialFlag flag : materialFlags) {
            isMaterialValid &= flag.verifyMaterialFlags(this);
        }
        for (MaterialProperty<?> property : properties.keySet()) {
            isMaterialValid &= property.verifyPropertyValue(this, properties.get(property));
        }
        if (!isMaterialValid) {
            LOGGER.error("Material {} is not valid. See above messages for errors", this);
            return false;
        }
        return true;
    }

    public Identifier getName() {
        return Preconditions.checkNotNull(REGISTRY.getId(this), "Material not registered");
    }

    public String getTranslationKey() {
        Identifier id = REGISTRY.getId(this);
        Preconditions.checkNotNull(id);
        return "material." + id.getNamespace() + "." + id.getPath();
    }

    public Text getDisplayName() {
        return new TranslatableText(getTranslationKey());
    }

    public boolean hasFlag(MaterialFlag flag) {
        return this.materialFlags.contains(flag);
    }

    public <T> Optional<T> queryProperty(MaterialProperty<T> property) {
        Object propertyValue = this.properties.get(property);
        if (propertyValue == null) {
            return Optional.ofNullable(property.getDefaultValue());
        }
        return Optional.of(property.cast(propertyValue));
    }

    public <T> T queryPropertyChecked(MaterialProperty<T> property) {
        return queryProperty(property).orElseThrow(() -> {
            String errorMessage = String.format("Material %s does not have a property %s", this, property);
            return new IllegalArgumentException(errorMessage);
        });
    }

    @Override
    public int compareTo(Material material) {
        return toString().compareTo(material.toString());
    }

    @Override
    public String toString() {
        return String.valueOf(REGISTRY.getId(this));
    }

    /** Verifies all material flags and values in registry */
    public static void verifyMaterialRegistry() {
        int invalidMaterialsFound = 0;

        for (Identifier materialId : REGISTRY.getIds()) {
            Material material = Preconditions.checkNotNull(REGISTRY.get(materialId));
            if (!material.verifyMaterial()) {
                invalidMaterialsFound++;
            }
        }

        if (invalidMaterialsFound > 0) {
            LOGGER.error("Found {} invalid materials, see messages above for errors", invalidMaterialsFound);

            if (MATERIAL_ERRORS_ARE_FATAL) {
                LOGGER.error("Loading cannot continue");
                throw new IllegalStateException("Material registry is in invalid state");
            }
        }
    }

    public static class Settings {
        private static final String FLAG_PROPERTY_ERROR_MESSAGE =
                "Cannot use .flag(MaterialFlag) to add material properties, use .property(MaterialProperty, T) instead!";
        private final Set<MaterialFlag> materialFlags = new HashSet<>();
        private final Map<MaterialProperty<?>, Object> properties = new HashMap<>();

        public Settings flag(MaterialFlag flag) {
            Preconditions.checkNotNull(flag, "flag");
            Preconditions.checkArgument(!(flag instanceof MaterialProperty<?>), FLAG_PROPERTY_ERROR_MESSAGE);

            this.materialFlags.add(flag);
            return this;
        }

        public Settings flags(Set<MaterialFlag> flags) {
            Preconditions.checkNotNull(flags, "flags");

            for (MaterialFlag flag : flags) {
                flag(flag);
            }

            return this;
        }

        @Deprecated
        public void flag(MaterialProperty<?> property) {
            throw new IllegalArgumentException(FLAG_PROPERTY_ERROR_MESSAGE);
        }

        public <T> Settings property(MaterialProperty<T> property, T value) {
            Preconditions.checkNotNull(property, "property");
            Preconditions.checkNotNull(value, "value");

            this.materialFlags.add(property);
            this.properties.put(property, value);
            return this;
        }

        public Settings visual(int color) {
            property(COLOR, color);

            return this;
        }

        public Settings visual(int color, MaterialIconSet iconSet) {
            property(COLOR, color);
            property(ICON_SET, iconSet);

            return this;
        }

        public Settings composition(MaterialComponent... composition) {
            property(CHEMICAL_COMPOSITION, ChemicalComposition.composite(composition));

            return this;
        }

        public Settings element(Element element) {
            property(CHEMICAL_COMPOSITION, ChemicalComposition.element(element));

            return this;
        }

        public Settings dust(int harvestLevel) {
            Preconditions.checkArgument(harvestLevel >= 0, "harvestLevel >= 0");

            flag(GENERATE_DUST);

            return this;
        }

        public Settings metal(int harvestLevel, int moltenTemperature) {
            Preconditions.checkArgument(harvestLevel >= 0, "harvestLevel >= 0");
            Preconditions.checkArgument(moltenTemperature >= 0, "moltenTemperature >= 0");

            flag(GENERATE_DUST);
            property(SOLID_FORM, SolidForm.METAL);
            property(HARVEST_LEVEL, harvestLevel);
            property(FLUID_PROPERTIES, FluidProperties.fluid(moltenTemperature));

            return this;
        }

        public Settings fluid() {
            property(FLUID_PROPERTIES, FluidProperties.fluid(FluidProperties.DEFAULT_TEMPERATURE));

            return this;
        }

        public Settings gas() {
            property(FLUID_PROPERTIES, FluidProperties.gas(FluidProperties.DEFAULT_TEMPERATURE));

            return this;
        }

        public Settings canCreateTools(float miningSpeed, float attackDamage, int durability) {
            return canCreateToolsWithDefaultEnchant(miningSpeed, attackDamage, durability, 12);
        }

        public Settings canCreateToolsWithDefaultEnchant(float miningSpeed, float attackDamage, int durability, int enchantability, EnchantmentData... enchantments) {
            Preconditions.checkArgument(miningSpeed >= 0, "miningSpeed >= 0");
            Preconditions.checkArgument(attackDamage >= 0, "attackDamage >= 0");
            Preconditions.checkArgument(durability >= 1, "durability >= 1");
            Preconditions.checkArgument(enchantability >= 1, "enchantability >= 1"); //MC minimal enchantability is 1

            flag(GENERATE_BOLT_SCREW);
            flag(GENERATE_ROD);
            flag(GENERATE_LONG_ROD);
            flag(GENERATE_PLATE);
            property(TOOL_PROPERTIES, ToolProperties.create(miningSpeed, attackDamage, durability, enchantability));

            return this;
        }

        public Settings canCreateCables(int voltage, int amperage, int lossPerBlock) {
            Preconditions.checkArgument(voltage >= CABLE_MINIMAL_VOLTAGE, "voltage >= " + CABLE_MINIMAL_VOLTAGE);
            Preconditions.checkArgument(amperage > 0, "amperage > 0");
            Preconditions.checkArgument(lossPerBlock >= 0, "lossPerBlock >= 0");

            property(CABLE_PROPERTIES, CableProperties.create(voltage, amperage, lossPerBlock));

            return this;
        }

        public Settings smeltsInBlastFurnace(int temperature) {
            Preconditions.checkArgument(temperature >= 0, "temperature >= 0");

            property(BLAST_FURNACE_TEMPERATURE, temperature);

            return this;
        }


        /**
         * This material is used as base in electric tiered components (Electric Motor, Electric Piston, etc.)
         */
        public Settings baseForElectricComponents() {
            flag(GENERATE_PLATE);
            flag(GENERATE_ROD);
            flag(GENERATE_SMALL_GEAR);

            return this;
        }


        public Settings plasma() {
            flag(GENERATE_PLASMA);

            return this;
        }
    }
}
