package com.vbuser.movement.data;

import net.minecraft.entity.player.EntityPlayer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GliderData {
    public static final class State {
        public boolean open;
        public boolean hasInput;
        public float dirX;
        public float dirZ;
    }
    private static final Map<UUID, State> MAP = new ConcurrentHashMap<>();
    public static State get(EntityPlayer p) { return MAP.computeIfAbsent(p.getUniqueID(), k -> new State()); }
    public static void remove(UUID id) { MAP.remove(id); }
    private GliderData() {}
}