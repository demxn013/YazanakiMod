package com.yazanaki.mod.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Camera.class, remap = false)
public interface CameraAccessor {
    @Accessor("pos")
    Vec3d yazanaki_getPos();
}