package net.mine_diver.smoothbeta.client.render;

import net.mine_diver.smoothbeta.client.render.gl.GlUniform;
import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;
import net.mine_diver.smoothbeta.mixin.client.multidraw.RenderListAccessor;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.world.ChunkRenderer;
import net.modificationstation.stationapi.api.util.math.Vec3f;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class RenderRegion extends ChunkRenderer {

    private final RenderListAccessor _super = (RenderListAccessor) this;
    private final SmoothWorldRenderer stationWorldRenderer;
    private final List<VertexBuffer> buffers = new ArrayList<>();

    public RenderRegion(WorldRenderer worldRenderer) {
        _super.smoothbeta_setGlListBuffer(IntBuffer.allocate(0));
        stationWorldRenderer = ((SmoothWorldRenderer) worldRenderer);
    }

    @Override
    public void init(int i, int j, int k, double d, double e, double f) {
        super.init(i, j, k, d, e, f);
        buffers.clear();
    }

    @Override
    public void addGlList(int i) {
        throw new UnsupportedOperationException("Call lists can't be added to VBO regions!");
    }

    public void addBuffer(VertexBuffer buffer) {
        buffers.add(buffer);
    }

    public void render() {
        if (!_super.smoothbeta_getInitialized() || buffers.isEmpty()) return;
        Shader shader = Shaders.getTerrainShader();
        GlUniform chunkOffset = shader.chunkOffset;
        chunkOffset.set(_super.smoothbeta_getX() - _super.smoothbeta_getOffsetX(), _super.smoothbeta_getY() - _super.smoothbeta_getOffsetY(), _super.smoothbeta_getZ() - _super.smoothbeta_getOffsetZ());
        chunkOffset.upload();
        for (VertexBuffer vertexBuffer : buffers) vertexBuffer.uploadToPool();
        stationWorldRenderer.smoothbeta_getTerrainVboPool().drawAll();
        chunkOffset.set(Vec3f.ZERO);
    }
}