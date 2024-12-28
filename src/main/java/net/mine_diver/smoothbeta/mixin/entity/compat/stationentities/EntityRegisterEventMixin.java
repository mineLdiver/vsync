package net.mine_diver.smoothbeta.mixin.entity.compat.stationentities;

import net.mine_diver.smoothbeta.entity.SmoothEntityRegistry;
import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRegister.EntityRegisterBuilder.class)
public class EntityRegisterEventMixin {
    @ModifyVariable(
            method = "registerNoID",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true,
            remap = false
    )
    private EntityRegister.RegisterFunctionNoId smoothbeta_registerNoID(EntityRegister.RegisterFunctionNoId value) {
        return SmoothEntityRegistry::registerNoID;
    }
}
