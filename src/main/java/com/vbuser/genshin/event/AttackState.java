package com.vbuser.genshin.event;

import com.vbuser.genshin.character.Character;
import com.vbuser.genshin.proxy.ClientProxy;
import com.vbuser.movement.event.PlayerMovement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
public class AttackState {

    public static ConcurrentMap<UUID, Integer> state = new ConcurrentHashMap<>();

    public static int getState(UUID player) {
        return state.get(player);
    }

    public static ConcurrentMap<Integer, com.vbuser.genshin.character.Character> character = new ConcurrentHashMap<>();

    public static Character getById(int id) {
        return AttackState.character.get(id);
    }

    int time_press = 0;
    int time_interval = 0;
    int animation_time = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player != null) {
            if (!player.isRiding()) {
                int attack_state;
                attack_state = state.getOrDefault(player.getUniqueID(), 0);

                if (state.containsKey(player.getUniqueID())) {
                    //e&q attack.
                    int slot = CharacterChoice.getChoice(player.getUniqueID());
                    int character = CharacterChoice.get().get(player.getUniqueID()).get(slot);
                    if (ClientProxy.E.isPressed()) {
                        boolean result = getById(character).e();
                        if (result) {
                            attack_state = 9;
                            animation_time = 40;
                        }
                    }
                    if (ClientProxy.Q.isPressed()) {
                        boolean result = getById(character).q();
                        if (result) {
                            attack_state = 10;
                            animation_time = 40;
                        }
                    }
                    if (getById(character).e) {
                        attack_state = 9;
                        animation_time = animation_time - 1;
                        if (animation_time <= 0) {
                            getById(character).e = false;
                            attack_state = 0;
                        }
                    }
                    if (getById(character).q) {
                        attack_state = 10;
                        animation_time = animation_time - 1;
                        if (animation_time <= 0) {
                            getById(character).q = false;
                            attack_state = 0;
                        }
                    }

                    if (!getById(character).e && !getById(character).q) {
                        //normal attack and swack.
                        if (player.onGround) {
                            if (attack_state < 8) {
                                if (ClientProxy.PG.isKeyDown()) {
                                    time_press = time_press + 1;
                                    if (time_press > 30) {
                                        time_interval = 0;
                                        attack_state = 7;
                                        getById(character).sward();
                                    }
                                } else {
                                    time_press = 0;
                                    time_interval = time_interval + 1;
                                    if (time_interval > (getById(character).att_category == 1 ? 400 : 40)) {
                                        attack_state = 0;
                                        time_interval = time_interval + 1;
                                    }
                                }
                                if (ClientProxy.PG.isPressed() && time_press < 30) {
                                    int preState = state.get(player.getUniqueID());
                                    if (time_interval > 10) {
                                        attack_state = (preState > 4) ? 1 : preState + 1;
                                        getById(character).normal_attack(attack_state);
                                        time_interval = 0;
                                    }
                                }
                            } else if (attack_state == 8) {
                                player.fallDistance = (float) (player.fallDistance * 0.01);
                                attack_state = 0;
                            }
                        } else {
                            //falling attack.
                            if (!player.isInWater()) {
                                if (ClientProxy.PG.isPressed()) {
                                    attack_state = 8;
                                    PlayerMovement.setUsingGlider(false);
                                } else {
                                    if (state.get(player.getUniqueID()) == 8) {
                                        player.motionY = -1.2;
                                        attack_state = 8;
                                    } else {
                                        attack_state = 0;
                                    }
                                }
                            }
                        }
                    }
                }
                state.put(player.getUniqueID(), attack_state);
            }
        }
    }
}