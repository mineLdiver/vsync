package net.mine_diver.smoothbeta.client.render;

import net.mine_diver.smoothbeta.client.render.gl.GlUniform;
import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;
import net.mine_diver.smoothbeta.mixin.client.multidraw.RenderListAccessor;
import net.minecraft.class_472;
import net.minecraft.client.render.WorldRenderer;
import net.modificationstation.stationapi.api.util.math.Vec3f;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class RenderRegion extends class_472 {

    private final RenderListAccessor _super = (RenderListAccessor) this;
    private final SmoothWorldRenderer stationWorldRenderer;
    private final List<VertexBuffer> buffers = new ArrayList<>();

    public RenderRegion(WorldRenderer worldRenderer) {
        _super.smoothbeta_setField_2486(IntBuffer.allocate(0));
        stationWorldRenderer = ((SmoothWorldRenderer) worldRenderer);
    }

    @Override
    public void method_1912(int i, int j, int k, double d, double e, double f) {
        super.method_1912(i, j, k, d, e, f);
        buffers.clear();
    }

    @Override
    public void method_1910(int i) {
        throw new UnsupportedOperationException("Call lists can't be added to VBO regions!");
    }

    public void addBuffer(VertexBuffer buffer) {
        buffers.add(buffer);
    }

    public void method_1909() {
        if (!_super.smoothbeta_getField_2487() || buffers.isEmpty()) return;
        Shader shader = Shaders.getTerrainShader();
        GlUniform chunkOffset = shader.chunkOffset;
        chunkOffset.set(_super.smoothbeta_getField_2480() - _super.smoothbeta_getField_2483(), _super.smoothbeta_getField_2481() - _super.smoothbeta_getField_2484(), _super.smoothbeta_getField_2482() - _super.smoothbeta_getField_2485());
        chunkOffset.upload();
        for (VertexBuffer vertexBuffer : buffers) vertexBuffer.uploadToPool();
        stationWorldRenderer.smoothbeta_getTerrainVboPool().drawAll();
        chunkOffset.set(Vec3f.ZERO);
    }
}