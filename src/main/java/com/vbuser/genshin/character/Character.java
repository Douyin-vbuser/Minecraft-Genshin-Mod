package com.vbuser.genshin.character;

import com.vbuser.genshin.event.AttackState;

@SuppressWarnings("unused")
public class Character {

    public final int e_cd;
    public final int q_cd;
    public final int q_enr;
    public int q_en;
    public long e_time, q_time;
    public boolean q = false, e = false;
    public final int att_category;

    public Character(int id, int e_cd, int q_cd, int q_enr, int att_category) {
        AttackState.character.put(id, this);
        this.e_cd = e_cd;
        this.q_cd = q_cd;
        this.q_enr = q_enr;
        this.q_en = 0;
        this.att_category = att_category;
    }

    public Character() {
        AttackState.character.put(0, this);
        this.e_cd = 50;
        this.q_cd = 50;
        this.q_enr = 0;
        this.q_en = 0;
        this.att_category = 0;
    }

    public boolean e() {
        boolean result = false;
        long time = System.currentTimeMillis();
        int interval = (int) ((time - e_time) / 100);
        if (interval >= e_cd) {
            e_time = time;
            e = true;
            result = true;
        }
        return result;
    }

    public boolean q() {
        boolean result = false;
        long time = System.currentTimeMillis();
        int interval = (int) ((time - q_time) / 100);
        if (interval >= q_cd) {
            if (q_en == q_enr) {
                q_en = 0;
                q_time = time;
                q = true;
                result = true;
            }
        }
        return result;
    }

    public void normal_attack(int state) {
        // Normal attack logic
    }

    public void sward() {
        // Sward logic
    }

    public static void init(){
        new Character();
    }
}
