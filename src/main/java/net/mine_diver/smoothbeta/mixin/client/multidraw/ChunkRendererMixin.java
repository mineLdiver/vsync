package net.mine_diver.smoothbeta.mixin.client.multidraw;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.smoothbeta.client.render.*;
import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.class_42;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;

import static net.mine_diver.smoothbeta.client.render.ChunkBuilderManager.THREAD_TESSELLATOR;

@Mixin(ChunkBuilder.class)
abstract
class ChunkRendererMixin implements SmoothChunkRenderer {
    @Shadow private static Tessellator tessellator;

    @Shadow public boolean[] renderLayerEmpty;
    @Shadow public int renderX;
    @Shadow public int renderY;
    @Shadow public int renderZ;
    @Shadow public boolean invalidated;

    @Shadow public abstract void rebuild();

    @Unique
    private VertexBuffer[] smoothbeta_buffers;
    @Unique
    private int smoothbeta_currentBufferIndex = -1;

    @Override
    @Unique
    public VertexBuffer smoothbeta_getBuffer(int pass) {
        return smoothbeta_buffers[pass];
    }

    @Override
    @Unique
    public VertexBuffer smoothbeta_getCurrentBuffer() {
        return smoothbeta_buffers[smoothbeta_currentBufferIndex];
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void smoothbeta_init(CallbackInfo ci) {
        smoothbeta_buffers = new VertexBuffer[renderLayerEmpty.length];
        //noinspection deprecation
        VboPool pool = ((SmoothWorldRenderer) ((Minecraft) FabricLoader.getInstance().getGameInstance()).worldRenderer).smoothbeta_getTerrainVboPool();
        for (int i = 0; i < smoothbeta_buffers.length; i++)
            smoothbeta_buffers[i] = new VertexBuffer(pool);
    }

    @Inject(
            method = "rebuild",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;startQuads()V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void smoothbeta_startRenderingTerrain(
            CallbackInfo ci,
            int var1, int var2, int var3, int var4, int var5, int var6, HashSet<BlockEntity> var7, int var8, class_42 var9, BlockRenderManager var10, int var11
    ) {
        smoothbeta_currentBufferIndex = var11;
        ((SmoothTessellator) tessellator).smoothbeta_startRenderingTerrain(this);
    }

    @Inject(
            method = "rebuild",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;translate(DDD)V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void smoothbeta_offsetBufferData(CallbackInfo ci) {
        tessellator.translate(this.renderX, this.renderY, this.renderZ);
    }

    @Inject(
            method = "rebuild",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;draw()V",
                    shift = At.Shift.AFTER
            )
    )
    private void smoothbeta_stopRenderingTerrain(CallbackInfo ci) {
        smoothbeta_currentBufferIndex = -1;
        ((SmoothTessellator) tessellator).smoothbeta_stopRenderingTerrain();
    }

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;INSTANCE:Lnet/minecraft/client/render/Tessellator;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private static Tessellator smoothbeta_returnThreadTessellator(Tessellator value) {
        return THREAD_TESSELLATOR;
    }

    @Inject(
            method = "rebuild",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/chunk/ChunkBuilder;chunkUpdates:I",
                    opcode = Opcodes.GETSTATIC
            ),
            cancellable = true
    )
    private void smoothbeta_execute(CallbackInfo ci) {
        if (Thread.currentThread().getName().equals("Minecraft main thread")) {
            boolean invalidated = this.invalidated;
            ChunkBuilderManager.EXECUTOR_SERVICE.execute(() -> {
                this.invalidated = invalidated;
                rebuild();
                this.invalidated = false;
            });
            ci.cancel();
        }
    }
}
