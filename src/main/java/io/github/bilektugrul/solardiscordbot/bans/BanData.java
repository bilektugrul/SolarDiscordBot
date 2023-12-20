package io.github.bilektugrul.solardiscordbot.bans;

public class BanData {

    private final long userID;
    private final String lastKnownUsername;
    private final long banMilliseconds;
    private final long unbanMilliseconds;

    public BanData(long userID, String lastKnownUsername, long banMilliseconds, long unbanMilliseconds) {
        this.userID = userID;
        this.lastKnownUsername = lastKnownUsername;
        this.banMilliseconds = banMilliseconds;
        this.unbanMilliseconds = unbanMilliseconds;
    }

    public long getUserID() {
        return userID;
    }

    public long getBanMilliseconds() {
        return banMilliseconds;
    }

    public long getUnbanMilliseconds() {
        return unbanMilliseconds;
    }

    public String getLastKnownUsername() {
        return lastKnownUsername;
    }

}