package vip.ourcraft.coolqplugins.creepercoolqbot;

import vip.ourcraft.coolqplugins.creepercoolqbot.entities.QQGroup;

import java.util.HashMap;

/**
 * Created by July on 2018/05/26.
 */
public class Settings {
    private String msgPrefix;
    private long ownerQQ;
    private HashMap<Long, QQGroup> groups;

    public long getOwnerQQ() {
        return ownerQQ;
    }

    public void setOwnerQQ(long ownerQQ) {
        this.ownerQQ = ownerQQ;
    }

    public String getMsgPrefix() {
        return msgPrefix;
    }

    public void setMsgPrefix(String msgPrefix) {
        this.msgPrefix = msgPrefix;
    }

    public HashMap<Long, QQGroup> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<Long, QQGroup> groups) {
        this.groups = groups;
    }
}
