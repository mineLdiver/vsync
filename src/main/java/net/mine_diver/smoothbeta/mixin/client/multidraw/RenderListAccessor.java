package net.mine_diver.smoothbeta.mixin.client.multidraw;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.IntBuffer;
import net.minecraft.client.render.world.ChunkRenderer;

@Mixin(ChunkRenderer.class)
public interface RenderListAccessor {
    @Accessor("glListBuffer")
    void smoothbeta_setGlListBuffer(IntBuffer buffer);

    @Accessor("initialized")
    boolean smoothbeta_getInitialized();

    @Accessor("x")
    int smoothbeta_getX();

    @Accessor("y")
    int smoothbeta_getY();

    @Accessor("z")
    int smoothbeta_getZ();

    @Accessor("offsetX")
    float smoothbeta_getOffsetX();

    @Accessor("offsetY")
    float smoothbeta_getOffsetY();

    @Accessor("offsetZ")
    float smoothbeta_getOffsetZ();
}
