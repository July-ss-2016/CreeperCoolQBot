package vip.ourcraft.coolqplugins.creepercoolqbot.entities;

import com.sobte.cqp.jcq.event.JcqAppAbstract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by July on 2018/05/26.
 */
public class QQGroup {
    private long groupID;
    private boolean autoAcceptJoinRequest;
    private String joinMsg;
    private String kickMsg;
    private AntiSpamer antiSpamer;
    private List<Long> whitelist;
    private List<RegexFilter> regexFilters;
    private GroupNickChecker groupNickChecker;
    private HashMap<Long, Long> memberLastGroupSpokeTimes;
    private HashMap<Long, Integer> spamBreakVls;
    // QQ, 禁言結束时间
    private HashMap<Long, Long> mutedMembers;

    public QQGroup() {
        this.memberLastGroupSpokeTimes = new HashMap<>();
        this.spamBreakVls = new HashMap<>();
        this.mutedMembers = new HashMap<>();
    }

    public GroupNickChecker getGroupNickChecker() {
        return groupNickChecker;
    }

    public void setGroupNickChecker(GroupNickChecker groupNickChecker) {
        this.groupNickChecker = groupNickChecker;
    }

    public List<Long> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<Long> whitelist) {
        this.whitelist = whitelist;
    }

    public boolean isAutoAcceptJoinRequest() {
        return autoAcceptJoinRequest;
    }

    public void setAutoAcceptJoinRequest(boolean autoAcceptJoinRequest) {
        this.autoAcceptJoinRequest = autoAcceptJoinRequest;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public long getGroupID() {
        return groupID;
    }

    public String getJoinMsg() {
        return joinMsg;
    }

    public void setJoinMsg(String joinMsg) {
        this.joinMsg = joinMsg;
    }

    public AntiSpamer getAntiSpamer() {
        return antiSpamer;
    }

    public void setAntiSpamer(AntiSpamer antiSpamer) {
        this.antiSpamer = antiSpamer;
    }

    public List<RegexFilter> getRegexFilters() {
        return regexFilters;
    }

    public void setRegexFilters(List<RegexFilter> regexFilters) {
        this.regexFilters = regexFilters;
    }

    public void setMemberLastGroupSpokeTime(long qqID, long time) {
        memberLastGroupSpokeTimes.put(qqID, time);
    }

    public long getMemberLastGroupSpokeTime(long qqID) {
        return memberLastGroupSpokeTimes.getOrDefault(qqID, 0L);
    }

    public void setSpamBreakVl(long qqID, int vl) {
        if (vl == 0) {
            spamBreakVls.remove(qqID);
            return;
        }

        spamBreakVls.put(qqID, vl);
    }

    public int getSpamBreakVl(long qqID) {
        return spamBreakVls.getOrDefault(qqID, 0);
    }

    public void muteMember(long qqID, long seconds) {
        mutedMembers.put(qqID, System.currentTimeMillis() + seconds * 1000);
        JcqAppAbstract.CQ.setGroupBan(groupID, qqID, seconds);
    }

    public long getMemberMuteRemainingTime(long qqID) {
        if (mutedMembers.containsKey(qqID)) {
            long temp = (mutedMembers.get(qqID) - System.currentTimeMillis()) / 1000L;

            if (temp < 0L) {
                mutedMembers.remove(qqID);
                return 0L;
            }

            return temp;
        }

        return 0L;
    }

    public String getKickMsg() {
        return kickMsg;
    }

    public void setKickMsg(String kickMsg) {
        this.kickMsg = kickMsg;
    }
}
