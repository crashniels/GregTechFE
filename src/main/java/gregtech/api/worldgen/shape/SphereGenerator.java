package gregtech.api.worldgen.shape;

import com.google.gson.JsonObject;
import crafttweaker.annotations.ZenRegister;
import gregtech.api.worldgen.config.OreConfigUtils;
import net.minecraft.util.math.Vec3i;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.Random;

@ZenClass("mods.gregtech.ore.generator.SphereGenerator")
@ZenRegister
public class SphereGenerator extends ShapeGenerator {

    private int radiusMin;
    private int radiusMax;

    public SphereGenerator() {
    }

    public SphereGenerator(int radiusMin, int radiusMax) {
        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
    }

    @ZenGetter("minRadius")
    public int getRadiusMin() {
        return radiusMin;
    }

    @ZenGetter("maxRadius")
    public int getRadiusMax() {
        return radiusMax;
    }

    @Override
    public void loadFromConfig(JsonObject object) {
        int[] data = OreConfigUtils.getIntRange(object.get("radius"));
        this.radiusMin = data[0];
        this.radiusMax = data[1];
    }

    @Override
    public Vec3i getMaxSize() {
        return new Vec3i(radiusMax * 2, radiusMax * 2, radiusMax * 2);
    }

    @Override
    public void generate(Random gridRandom, IBlockGeneratorAccess relativeBlockAccess) {
        int sphereRadius = radiusMin >= radiusMax ? radiusMin : radiusMin + gridRandom.nextInt(radiusMax - radiusMin);
        for (int x = -sphereRadius; x <= sphereRadius; x++) {
            for (int z = -sphereRadius; z <= sphereRadius; z++) {
                for (int y = -sphereRadius; y <= sphereRadius; y++) {
                    if (x * x + y * y + z * z > sphereRadius * sphereRadius)
                        continue;
                    relativeBlockAccess.generateBlock(x, y, z);
                }
            }
        }

    }
}
