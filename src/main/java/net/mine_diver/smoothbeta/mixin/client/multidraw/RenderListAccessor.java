package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.minecraft.class_472;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.IntBuffer;

@Mixin(class_472.class)
public interface RenderListAccessor {
    @Accessor("field_2486")
    void smoothbeta_setField_2486(IntBuffer buffer);

    @Accessor("field_2487")
    boolean smoothbeta_getField_2487();

    @Accessor("field_2480")
    int smoothbeta_getField_2480();

    @Accessor("field_2481")
    int smoothbeta_getField_2481();

    @Accessor("field_2482")
    int smoothbeta_getField_2482();

    @Accessor("field_2483")
    float smoothbeta_getField_2483();

    @Accessor("field_2484")
    float smoothbeta_getField_2484();

    @Accessor("field_2485")
    float smoothbeta_getField_2485();
}
