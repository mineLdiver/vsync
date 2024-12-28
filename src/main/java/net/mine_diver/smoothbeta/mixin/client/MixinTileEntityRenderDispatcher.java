package net.mine_diver.smoothbeta.mixin.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(BlockEntityRenderDispatcher.class)
class MixinTileEntityRenderDispatcher {

    @Shadow private Map<Class<? extends BlockEntity>, BlockEntityRenderer> renderers;

    @Redirect(
            method = "<init>()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderers:Ljava/util/Map;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void overrideMap(BlockEntityRenderDispatcher instance, Map<Class<? extends BlockEntity>, BlockEntityRenderer> value) {
        renderers = new IdentityHashMap<>();
    }
}
