package com.brendan.dadlibs.engine;

public class Replacement implements Comparable<Replacement>{
    public final int startPos;
    public final int length;
    public final Placeholder placeholder;

    public Replacement(int startPos, int length, Placeholder placeholder){
        this.length = length;
        this.startPos = startPos;
        this.placeholder = placeholder;
    }

    @Override
    public int compareTo(Replacement other) {
        return Integer.compare(startPos, other.startPos);
    }
}
