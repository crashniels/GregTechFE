package gregtech.api.damagesources;

import net.minecraft.entity.damage.DamageSource;

public class GTDamageSource extends DamageSource {

    public static final DamageSource EXPLOSION = new GTDamageSource("explosion").setExplosive();
    public static final DamageSource HEAT = new GTDamageSource("heat").setBypassesArmor();
    public static final DamageSource FROST = new GTDamageSource("frost").setBypassesArmor();
    public static final DamageSource ELECTRIC = new GTDamageSource("electric").setBypassesArmor();
    public static final DamageSource RADIATION = new GTDamageSource("radiation").setBypassesArmor();
    public static final DamageSource TURBINE = new GTDamageSource("turbine");
    public static final DamageSource CRUSHER = new GTDamageSource("crusher");

    protected GTDamageSource(String name) {
        super(name);
    }
}