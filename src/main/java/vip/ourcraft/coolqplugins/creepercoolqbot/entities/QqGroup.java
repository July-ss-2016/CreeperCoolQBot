package vip.ourcraft.coolqplugins.creepercoolqbot.entities;

import com.sobte.cqp.jcq.event.JcqAppAbstract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by July on 2018/05/26.
 */
public class QqGroup {
    private long groupId;
    private boolean autoAcceptJoinRequest;
    private String joinMsg;
    private AntiSpamer antiSpamer;
    private List<Long> whitelist;
    private List<RegexFilter> regexFilters;
    private HashMap<Long, Long> memberLastGroupSpokeTimes;
    private HashMap<Long, Integer> spamBreakVls;
    // QQ, 禁言結束时间
    private HashMap<Long, Long> mutedMembers;

    public QqGroup() {
        this.memberLastGroupSpokeTimes = new HashMap<>();
        this.spamBreakVls = new HashMap<>();
        this.mutedMembers = new HashMap<>();
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

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getGroupId() {
        return groupId;
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

    public void setMemberLastGroupSpokeTime(long qqId, long time) {
        memberLastGroupSpokeTimes.put(qqId, time);
    }

    public long getMemberLastGroupSpokeTime(long qqId) {
        return memberLastGroupSpokeTimes.getOrDefault(qqId, 0L);
    }

    public void setSpamBreakVl(long qqId, int vl) {
        if (vl == 0) {
            spamBreakVls.remove(qqId);
            return;
        }

        spamBreakVls.put(qqId, vl);
    }

    public int getSpamBreakVl(long qqId) {
        return spamBreakVls.getOrDefault(qqId, 0);
    }

    public void muteMember(long qqId, long seconds) {
        mutedMembers.put(qqId, System.currentTimeMillis() + seconds * 1000);
        JcqAppAbstract.CQ.setGroupBan(groupId, qqId, seconds);
    }

    public long getMemberMuteRemainingTime(long qqId) {
        if (mutedMembers.containsKey(qqId)) {
            long temp = (mutedMembers.get(qqId) - System.currentTimeMillis()) / 1000L;

            if (temp < 0L) {
                mutedMembers.remove(qqId);
                return 0L;
            }

            return temp;
        }

        return 0L;
    }
}
