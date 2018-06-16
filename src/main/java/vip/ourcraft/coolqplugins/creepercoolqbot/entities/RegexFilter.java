package vip.ourcraft.coolqplugins.creepercoolqbot.entities;

/**
 * Created by July on 2018/05/26.
 */
public class RegexFilter {
    private String regex;
    private boolean withdraw;
    private int muteMinutes;
    private String punishMsg;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
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
