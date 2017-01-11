package com.drollgames.crjump.model;

public class JsonLevel {

    public int levelNumber;
    public int stars;
    public boolean isUnlocked;

    @Override
    public String toString() {
        return "JsonLevel [levelNumber=" + levelNumber + ", stars=" + stars + ", isPassed=" + isUnlocked + "]";
    }
}
