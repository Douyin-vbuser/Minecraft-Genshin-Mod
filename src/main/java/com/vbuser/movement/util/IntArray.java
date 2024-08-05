package com.vbuser.movement.util;

public class IntArray{
    private static float a,b;

    public IntArray(int code){
        switch (code){
            default:a=0;b=0;break;
            case 1:a=0.35f;b=0;break;
            case 2:a=0;b=0.35f;break;
            case 3:a=-0.35f;b=0;break;
            case 4:a=0;b=-0.35f;break;
        }
    }

    public float getX(){return a;}

    public float getZ(){return b;}

    @Override
    public String toString(){
        return "("+a+","+b+")";
    }
}
