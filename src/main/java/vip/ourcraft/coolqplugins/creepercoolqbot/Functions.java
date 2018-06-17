package vip.ourcraft.coolqplugins.creepercoolqbot;

import com.sobte.cqp.jcq.entity.CoolQ;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqAppAbstract;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.AntiSpamer;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.GroupNickChecker;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.QQGroup;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.RegexFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.sobte.cqp.jcq.entity.IRequest.REQUEST_ADOPT;

/**
 * Created by July on 2018/06/02.
 */
public class Functions {
    private CoolQ cq = JcqAppAbstract.CQ;
    private CreeperCoolQBot plugin;
    private Settings settings;

    public Functions(CreeperCoolQBot plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    public void doAntiSpamer(int subType, int msgID, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        if (settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);

            if (group.getWhitelist() != null && group.getWhitelist().contains(fromQQ)) {
                return;
            }

            AntiSpamer antiSpamer = group.getAntiSpamer();

            if (antiSpamer != null) {
                if (System.currentTimeMillis() - group.getMemberLastGroupSpokeTime(fromQQ) < antiSpamer.getIntervalThreshold()) {
                    group.setSpamBreakVl(fromQQ, group.getSpamBreakVl(fromQQ) + 1);
                } else {
                    int currentVl = group.getSpamBreakVl(fromQQ);

                    if (currentVl > 0) {
                        group.setSpamBreakVl(fromQQ, currentVl - 1);
                    }
                }

                if (group.getSpamBreakVl(fromQQ) >= antiSpamer.getMuteVL()) {
                    String muteMsg =  antiSpamer.getMuteMsg();

                    group.muteMember(fromQQ, antiSpamer.getMuteMinutes() * 60);

                    if (!muteMsg.equals("")) {
                        cq.sendGroupMsg(fromGroup, muteMsg
                                .replace("%qq_name%", cq.getGroupMemberInfo(fromGroup, fromQQ, true).getNick())
                                .replace("%qq_num%", String.valueOf(fromQQ))
                                .replace("%mute_minutes%", String.valueOf(antiSpamer.getMuteMinutes())));
                    }

                    group.setSpamBreakVl(fromQQ, 0);
                }

                group.setMemberLastGroupSpokeTime(fromQQ, System.currentTimeMillis());
            }
        }
    }

    public void doRegexFilter(int subType, int msgID, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        if (settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);

            if (group.getWhitelist() != null && group.getWhitelist().contains(fromQQ)) {
                return;
            }

            List<RegexFilter> regexFilters = group.getRegexFilters();

            if (regexFilters != null) {
                for (RegexFilter regexFilter : regexFilters) {
                    Pattern pattern = Pattern.compile(regexFilter.getRegex());

                    if (pattern.matcher(msg).find()) {
                        boolean withdraw = regexFilter.isWithdraw();
                        String punishMsg = regexFilter.getPunishMsg();

                        group.muteMember(fromQQ, regexFilter.getMuteMinutes() * 60);

                        if (withdraw) {
                            cq.deleteMsg(msgID);
                        }

                        if (!punishMsg.equals("")) {
                            cq.sendGroupMsg(fromGroup, punishMsg
                                    .replace("%punish_type%", withdraw ? "撤回&禁言" + regexFilter.getMuteMinutes() + "(分钟)" :  "禁言" + regexFilter.getMuteMinutes() + "(分钟)")
                                    .replace("%qq_name%", cq.getGroupMemberInfo(fromGroup, fromQQ, true).getNick())
                                    .replace("%qq_num%", String.valueOf(fromQQ))
                            );
                        }
                    }
                }
            }
        }
    }

    public void doAddGroup(int subType, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
        if (settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);

            if (group.isAutoAcceptJoinRequest()) {
                String joinMsg = group.getJoinMsg();

                cq.setGroupAddRequest(responseFlag, subType, REQUEST_ADOPT, null);

                // 防止玩家退群重加消除禁言时间
                long muteRemainingTime = group.getMemberMuteRemainingTime(fromQQ);

                if (muteRemainingTime > 0) {
                    group.muteMember(fromQQ, muteRemainingTime);
                    return;
                }

                if (!joinMsg.equals("")) {
                    cq.sendGroupMsg(fromGroup, joinMsg
                            .replace("%qq_name%", cq.getGroupMemberInfo(fromGroup, fromQQ, true).getNick())
                            .replace("%qq_num%", String.valueOf(fromQQ))
                    );
                }
            }
        }
    }

    public void doAdminCommands(int subType, int msgID, long fromQQ, String msg, int font) {
        if (fromQQ == settings.getOwnerQQ() && Objects.equals(msg, "#ccqb reload")) {
            plugin.loadConfig();
            cq.sendPrivateMsg(fromQQ, "重载配置成功!");
        }
    }

    public void doGroupNickChecker(int subType, int msgID, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        if (settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);

            if (group.getWhitelist() != null && group.getWhitelist().contains(fromQQ)) {
                return;
            }

            GroupNickChecker groupNickChecker = group.getGroupNickChecker();

            for (String keyWord : groupNickChecker.getBlackKeywords()) {
                Member member = cq.getGroupMemberInfoV2(fromGroup, fromQQ, true);

                if (member == null) {
                    return;
                }

                String groupNick = member.getCard();

                if (groupNick != null && groupNick.contains(keyWord)) {
                    boolean withdraw = groupNickChecker.isWithdraw();
                    String punishMsg = groupNickChecker.getPunishMsg();

                    group.muteMember(fromQQ, groupNickChecker.getMuteMinutes() * 60);

                    if (withdraw) {
                        cq.deleteMsg(msgID);
                    }

                    if (!punishMsg.equals("")) {
                        cq.sendGroupMsg(fromGroup, punishMsg
                                .replace("%punish_type%", withdraw ? "撤回&禁言" + groupNickChecker.getMuteMinutes() + "(分钟)" :  "禁言" + groupNickChecker.getMuteMinutes() + "(分钟)")
                                .replace("%qq_name%", cq.getGroupMemberInfo(fromGroup, fromQQ, true).getNick())
                                .replace("%qq_num%", String.valueOf(fromQQ))
                        );
                    }
                }
            }
        }
    }
}
