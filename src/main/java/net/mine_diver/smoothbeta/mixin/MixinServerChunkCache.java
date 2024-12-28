package net.mine_diver.smoothbeta.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.class_243;
import net.minecraft.class_326;
import net.minecraft.class_43;
import net.minecraft.class_51;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(class_326.class)
abstract class MixinServerChunkCache {

    @Shadow private Map<Integer, class_43> field_1229;

    @Shadow public abstract class_43 method_1807(int chunkX, int chunkZ);

    @Unique
    private Int2ObjectMap<class_43> smoothbeta$serverChunkCache;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void getMap(World level, class_243 arg1, class_51 arg2, CallbackInfo ci) {
        smoothbeta$serverChunkCache = new Int2ObjectOpenHashMap<>();
        field_1229 = smoothbeta$serverChunkCache;
    }

    // TODO: replace with ASM
    /**
     * @reason Redirecting {@code serverChunkCache.containsKey(Vec2i.hash(chunkX, chunkZ))} still boxes the integer, adding unnecessary memory usage.
     * @author mine_diver
     */
    @Overwrite
    public boolean method_1802(int chunkX, int chunkZ) {
        return smoothbeta$serverChunkCache.containsKey(ChunkPos.hashCode(chunkX, chunkZ));
    }

    // TODO: replace with ASM
    /**
     * @reason This is the only way to avoid integer boxing here.
     * @author mine_diver
     */
    @Overwrite
    public class_43 method_1806(int chunkX, int chunkZ) {
        class_43 var3 = smoothbeta$serverChunkCache.get(ChunkPos.hashCode(chunkX, chunkZ));
        return var3 == null ? method_1807(chunkX, chunkZ) : var3;
    }
}
