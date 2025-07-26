package com.vbuser.mixin;

import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInput {

    @Redirect(
            method = "updatePlayerMoveState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z",
                    ordinal = 5
            )
    )
    private boolean redirectSneakKeyDown(net.minecraft.client.settings.KeyBinding instance) {
        return false;
    }
}
