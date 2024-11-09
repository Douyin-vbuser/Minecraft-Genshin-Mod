package com.vbuser.genshin.event;

import com.vbuser.genshin.proxy.ClientProxy;
import com.vbuser.movement.event.PlayerMovement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AttackState {

    public static class Character{

        private final int e_cd;
        private final int q_cd;
        private final int q_enr;
        private int q_en;
        private long e_time,q_time;
        private boolean q=false,e=false;

        public Character(int id,int e_cd,int q_cd,int q_enr){
            character.put(id,this);
            this.e_cd = e_cd;
            this.q_cd = q_cd;
            this.q_enr = q_enr;
            this.q_en = 0;
        }

        public Character(){
            character.put(0,this);
            e_cd = 50;
            q_cd = 50;
            q_enr = 0;
            this.q_en = 0;
        }

        public int get_e_cd(){return e_cd;}
        public int get_q_cd(){return q_cd;}
        public int get_q_enr(){return q_enr;}
        public int get_q_en(){return q_en;}
        public void set_q_en(int value){q_en=value;}
        public long get_q_time(){return q_time;}
        public void set_q_time(){q_time = System.currentTimeMillis();}
        public void set_e_time(){e_time = System.currentTimeMillis();}
        public long get_e_time(){return e_time;}
        public static Character getById(int id){return character.get(id);}
        public boolean isQ(){return q;}
        public boolean isE(){return e;}
        public void setE(boolean value){e=value;}
        public void setQ(boolean value){q=value;}

        public boolean e(){
            boolean result = false;
            long time = System.currentTimeMillis();
            int interval = (int)((time - get_e_time())/100);
            if(interval>=get_e_cd()){
                set_e_time();
                setE(true);
                result = true;
            }
            return result;
        }
        public boolean q(){
            boolean result = false;
            long time = System.currentTimeMillis();
            int interval = (int)((time - get_q_time())/100);
            if(interval>=get_q_cd()){
                if(get_q_en()==get_q_enr()){
                    set_q_en(0);
                    set_q_time();
                    setQ(true);
                    result = true;
                }
            }
            return result;
        }

        public static void init(){
            new Character();
        }

    }

    static ConcurrentMap<UUID,Integer> state = new ConcurrentHashMap<>();

    public static int getState(UUID player){
        return state.get(player);
    }

    static ConcurrentMap<Integer, Character> character = new ConcurrentHashMap<>();

    int time_press=0;
    int time_interval=0;
    int animation_time=0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        EntityPlayer player = event.player;
        if(player != null){
            if(!player.isRiding()){
                int attack_state;
                attack_state = state.getOrDefault(player.getUniqueID(), 0);

                if(state.containsKey(player.getUniqueID())) {
                    //e&q attack.
                    int slot = CharacterChoice.getChoice(player.getUniqueID());
                    int character = CharacterChoice.get().get(player.getUniqueID()).get(slot);
                    if (ClientProxy.E.isPressed()) {
                        boolean result = Character.getById(character).e();
                        if (result) {
                            attack_state = 9;
                            animation_time = 40;
                        }
                    }
                    if (ClientProxy.Q.isPressed()) {
                        boolean result = Character.getById(character).q();
                        if (result) {
                            attack_state = 10;
                            animation_time = 40;
                        }
                    }
                    if (Character.getById(character).isE()) {
                        attack_state = 9;
                        animation_time = animation_time - 1;
                        if (animation_time <= 0) {
                            Character.getById(character).setE(false);
                            attack_state = 0;
                        }
                    }
                    if (Character.getById(character).isQ()) {
                        attack_state = 10;
                        animation_time = animation_time - 1;
                        if (animation_time <= 0) {
                            Character.getById(character).setQ(false);
                            attack_state = 0;
                        }
                    }

                    if (!Character.getById(character).isE() && !Character.getById(character).isQ()) {

                        //normal attack and swack.
                        if (player.onGround) {
                            if (attack_state < 8) {
                                if (ClientProxy.PG.isKeyDown()) {
                                    time_press = time_press + 1;
                                    if (time_press > 30) {
                                        time_interval = 0;
                                        attack_state = 7;
                                    }
                                } else {
                                    time_press = 0;
                                    time_interval = time_interval + 1;
                                    if (time_interval > 40) {
                                        attack_state = 0;
                                        time_interval = 41;
                                    }
                                }
                                if (ClientProxy.PG.isPressed() && time_press < 30) {
                                    int preState = state.get(player.getUniqueID());
                                    if (time_interval > 10) {
                                        attack_state = (preState > 5) ? 1 : preState + 1;
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
                state.put(player.getUniqueID(),attack_state);
            }
        }
    }
}
