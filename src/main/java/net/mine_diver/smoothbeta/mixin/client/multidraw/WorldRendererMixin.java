package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.mine_diver.smoothbeta.client.render.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.world.ChunkRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.FloatBuffer;

@Mixin(WorldRenderer.class)
abstract class WorldRendererMixin implements SmoothWorldRenderer {
    @Shadow private ChunkRenderer[] field_1794;

    @Unique
    private VboPool smoothbeta_vboPool;

    @Override
    @Unique
    public VboPool smoothbeta_getTerrainVboPool() {
        return smoothbeta_vboPool;
    }

    @Inject(
            method = "reload()V",
            at = @At("HEAD")
    )
    private void smoothbeta_resetVboPool(CallbackInfo ci) {
        if (smoothbeta_vboPool != null)
            smoothbeta_vboPool.deleteGlBuffers();
        smoothbeta_vboPool = new VboPool(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "()Lnet/minecraft/client/render/world/ChunkRenderer;"
            )
    )
    private ChunkRenderer smoothbeta_injectRenderRegion() {
        return new RenderRegion((WorldRenderer) (Object) this);
    }

    @Inject(
            method = "renderChunks(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/world/ChunkRenderer;addGlList(I)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void smoothbeta_addBufferToRegion(int j, int k, int d, double par4, CallbackInfoReturnable<Integer> cir, int var6, LivingEntity var7, double var8, double var10, double var12, int var14, int var15, ChunkBuilder var16, int var17) {
        ((RenderRegion) this.field_1794[var17]).addBuffer(((SmoothChunkRenderer) var16).smoothbeta_getBuffer(d));
    }

    @Redirect(
            method = "renderChunks(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/world/ChunkRenderer;addGlList(I)V"
            )
    )
    private void smoothbeta_stopCallingRenderList(ChunkRenderer instance, int i) {}

    @Unique
    private final FloatBuffer
            smoothbeta_modelViewMatrix = GlAllocationUtils.allocateFloatBuffer(16),
            smoothbeta_projectionMatrix = GlAllocationUtils.allocateFloatBuffer(16);

    @Inject(
            method = "renderLastChunks(ID)V",
            at = @At("HEAD")
    )
    public void smoothbeta_beforeRenderRegion(int d, double par2, CallbackInfo ci) {
        Shader shader = Shaders.getTerrainShader();

        shader.addSampler("Sampler0", 0);

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, smoothbeta_modelViewMatrix.clear());
        shader.modelViewMat.set(smoothbeta_modelViewMatrix.position(0));

        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, smoothbeta_projectionMatrix.clear());
        shader.projectionMat.set(smoothbeta_projectionMatrix.position(0));

        shader.fogMode.set(switch (GL11.glGetInteger(GL11.GL_FOG_MODE)) {
            case GL11.GL_EXP -> 0;
            case GL11.GL_EXP2 -> 1;
            case GL11.GL_LINEAR -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + GL11.glGetInteger(GL11.GL_FOG_MODE));
        });

        shader.bind();
    }

    @Inject(
            method = "renderLastChunks(ID)V",
            at = @At("RETURN")
    )
    public void smoothbeta_afterRenderRegion(int d, double par2, CallbackInfo ci) {
        Shaders.getTerrainShader().unbind();

        GL20.glDisableVertexAttribArray(0); // pos
        GL20.glDisableVertexAttribArray(1); // texture
        GL20.glDisableVertexAttribArray(2); // color
        GL20.glDisableVertexAttribArray(3); // normal

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
