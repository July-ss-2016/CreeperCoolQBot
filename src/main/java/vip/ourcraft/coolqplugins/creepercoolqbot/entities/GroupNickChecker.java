package vip.ourcraft.coolqplugins.creepercoolqbot.entities;

import java.util.List;

public class GroupNickChecker {
    private List<String> blackKeywords;
    private boolean withdraw;
    private int muteMinutes;
    private String punishMsg;

    public List<String> getBlackKeywords() {
        return blackKeywords;
    }

    public void setBlackKeywords(List<String> blackKeywords) {
        this.blackKeywords = blackKeywords;
    }

    public boolean isWithdraw() {
        return withdraw;
    }

    public void setWithdraw(boolean withdraw) {
        this.withdraw = withdraw;
    }

    public int getMuteMinutes() {
        return muteMinutes;
    }

    public void setMuteMinutes(int muteMinutes) {
        this.muteMinutes = muteMinutes;
    }

    public String getPunishMsg() {
        return punishMsg;
    }

    public void setPunishMsg(String punishMsg) {
        this.punishMsg = punishMsg;
    }
}
