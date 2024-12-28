package net.mine_diver.smoothbeta.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.storage.ChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ChunkCache.class)
abstract class MixinServerChunkCache {

    @Shadow private Map<Integer, Chunk> chunkByPos;

    @Shadow public abstract Chunk loadChunk(int chunkX, int chunkZ);

    @Unique
    private Int2ObjectMap<Chunk> smoothbeta$serverChunkCache;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void getMap(World level, ChunkStorage arg1, ChunkSource arg2, CallbackInfo ci) {
        smoothbeta$serverChunkCache = new Int2ObjectOpenHashMap<>();
        chunkByPos = smoothbeta$serverChunkCache;
    }

    // TODO: replace with ASM
    /**
     * @reason Redirecting {@code serverChunkCache.containsKey(Vec2i.hash(chunkX, chunkZ))} still boxes the integer, adding unnecessary memory usage.
     * @author mine_diver
     */
    @Overwrite
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return smoothbeta$serverChunkCache.containsKey(ChunkPos.hashCode(chunkX, chunkZ));
    }

    // TODO: replace with ASM
    /**
     * @reason This is the only way to avoid integer boxing here.
     * @author mine_diver
     */
    @Overwrite
    public Chunk getChunk(int chunkX, int chunkZ) {
        Chunk var3 = smoothbeta$serverChunkCache.get(ChunkPos.hashCode(chunkX, chunkZ));
        return var3 == null ? loadChunk(chunkX, chunkZ) : var3;
    }
}
