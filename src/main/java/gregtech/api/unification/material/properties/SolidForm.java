package gregtech.api.unification.material.properties;

import gregtech.api.unification.forms.MaterialForm;
import gregtech.api.unification.forms.MaterialForms;

public enum SolidForm {

    METAL(MaterialForms.INGOTS),
    GEM(MaterialForms.GEMS);
//    POLYMER(MaterialForms.POLYMER);

    private final MaterialForm solidForm;

    SolidForm(MaterialForm solidForm) {
        this.solidForm = solidForm;
    }

    public boolean isMetalOrGem() {
        return this == METAL || this == GEM;
    }

    public MaterialForm getMaterialForm() {
        return this.solidForm;
    }
}
