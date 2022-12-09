package com.example.spiritgo_1124;

public class Spirit {
    static byte[][] range = {  {33, 43, 80, 255, 80, 255}, // ban2
                        //{0, 0, 0, 0, 0, 0}, // bboong2
                        {125, 141, 80, 255, 80, 255}, // cwal2
                        //{0, 0, 0, 0, 0, 0}, // ddan2
                        //{0, 0, 0, 0, 0, 0}, // ggam2
                        {212, 32, 80, 255, 80, 255}, // hwal2
                        {100, 124, 80, 255, 80, 255}, // hwing2
                        //{0, 0, 0, 0, 0, 0}, // jjak2
                        {44, 99, 80, 255, 80, 255}, // ssuk2
                        //{0, 0, 0, 0, 0, 0}, // wang2
                        //{0, 0, 0, 0, 0, 0}, // zzek2
                        };

    public static byte[] hsvRange(int num){
        return range[num];
    }
}

