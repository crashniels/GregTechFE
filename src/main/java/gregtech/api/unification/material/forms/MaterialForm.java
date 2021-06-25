package gregtech.api.unification.material.forms;

import gregtech.api.unification.stack.MaterialAmount;
import gregtech.api.util.registry.AlreadyRegisteredKeyException;
import gregtech.api.util.registry.GTRegistry;
import gregtech.api.util.registry.GTRegistryKey;

public class MaterialForm implements GTRegistryKey {
    private final String name;
    private final MaterialAmount materialAmount;

    private MaterialForm(String name, MaterialAmount materialAmount) {
        this.name = name;
        this.materialAmount = materialAmount;
    }


    @Override
    public String getKey() {
        return name;
    }

    public static class Builder {
        private static final GTRegistry<MaterialForm> registry = new GTRegistry<>();

        private final String name;
        private MaterialAmount materialAmount = MaterialAmount.DUST;

        public Builder(String name) {
            this.name = name;
        }

        public MaterialForm.Builder materialAmount(MaterialAmount materialAmount) {
            this.materialAmount = materialAmount;
            return this;
        }

        public MaterialForm build() {
            if (!validate()) {
                return null;
            }

            var materialForm = new MaterialForm(name, materialAmount);

            try {
                registry.put(materialForm);
            } catch (AlreadyRegisteredKeyException e) {
                //TODO: Log
                return null;
            }


            return materialForm;
        }

        private boolean validate() {
            if (materialAmount.equals(MaterialAmount.ZERO)) {
                //TODO: log error
                return false;
            }

            return true;
        }
    }
}
