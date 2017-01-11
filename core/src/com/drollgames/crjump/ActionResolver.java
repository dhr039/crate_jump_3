package com.drollgames.crjump;

public interface ActionResolver {
    public boolean getSignedInGPGS();

    public void loginGPGS();

    public void submitScoreLevel24(int score);

    public void submitScoreLevel40(int score);

    public void getLeaderboard24();

    public void getLeaderboard40();

    // public void getAchievementsGPGS();

    // public void unlockAchievementGPGS(String achievementId);

    public void signOut();
}
