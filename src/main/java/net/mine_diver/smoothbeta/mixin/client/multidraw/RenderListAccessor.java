package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.minecraft.client.render.world.ChunkRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.IntBuffer;

@Mixin(ChunkRenderer.class)
public interface RenderListAccessor {
    @Accessor("glListBuffer")
    void smoothbeta_setField_2486(IntBuffer buffer);

    @Accessor("initialized")
    boolean smoothbeta_getField_2487();

    @Accessor("x")
    int smoothbeta_getField_2480();

    @Accessor("y")
    int smoothbeta_getField_2481();

    @Accessor("z")
    int smoothbeta_getField_2482();

    @Accessor("offsetX")
    float smoothbeta_getField_2483();

    @Accessor("offsetY")
    float smoothbeta_getField_2484();

    @Accessor("offsetZ")
    float smoothbeta_getField_2485();
}
