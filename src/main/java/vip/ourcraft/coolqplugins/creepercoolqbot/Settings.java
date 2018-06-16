package vip.ourcraft.coolqplugins.creepercoolqbot;

import vip.ourcraft.coolqplugins.creepercoolqbot.entities.QqGroup;

import java.util.HashMap;

/**
 * Created by July on 2018/05/26.
 */
public class Settings {
    private String msgPrefix;
    private long ownerQq;
    private HashMap<Long, QqGroup> groups;

    public long getOwnerQq() {
        return ownerQq;
    }

    public void setOwnerQq(long ownerQq) {
        this.ownerQq = ownerQq;
    }

    public String getMsgPrefix() {
        return msgPrefix;
    }

    public void setMsgPrefix(String msgPrefix) {
        this.msgPrefix = msgPrefix;
    }

    public HashMap<Long, QqGroup> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<Long, QqGroup> groups) {
        this.groups = groups;
    }
}
