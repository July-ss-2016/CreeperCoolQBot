package vip.ourcraft.coolqplugins.creepercoolqbot.entities;

/**
 * Created by July on 2018/05/26.
 */
public class AntiSpamer {
    private int intervalThreshold;
    private int muteVL;
    private int muteMinutes;
    private String muteMsg;

    public int getIntervalThreshold() {
        return intervalThreshold;
    }

    public void setIntervalThreshold(int intervalThreshold) {
        this.intervalThreshold = intervalThreshold;
    }

    public int getMuteVL() {
        return muteVL;
    }

    public void setMuteVL(int muteVL) {
        this.muteVL = muteVL;
    }

    public int getMuteMinutes() {
        return muteMinutes;
    }

    public void setMuteMinutes(int muteMinutes) {
        this.muteMinutes = muteMinutes;
    }

    public String getMuteMsg() {
        return muteMsg;
    }

    public void setMuteMsg(String muteMsg) {
        this.muteMsg = muteMsg;
    }
}
