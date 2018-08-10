package vip.ourcraft.coolqplugins.creepercoolqbot;

import com.sobte.cqp.jcq.entity.CoolQ;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.entity.QQInfo;
import com.sobte.cqp.jcq.event.JcqAppAbstract;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.AntiSpamer;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.GroupNickChecker;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.QQGroup;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.RegexFilter;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.sobte.cqp.jcq.entity.IRequest.REQUEST_ADOPT;

/**
 * Created by July on 2018/06/02.
 */
class Functions {
    private CoolQ cq = JcqAppAbstract.CQ;
    private CreeperCoolQBot plugin;
    private Settings settings;

    Functions(CreeperCoolQBot plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    // 反刷屏
    int doAntiSpamer(int subType, int msgID, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        if (settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);

            if (group.getWhitelist() != null && group.getWhitelist().contains(fromQQ)) {
                return 0;
            }

            AntiSpamer antiSpamer = group.getAntiSpamer();

            if (antiSpamer != null) {
                // 小于规定间隔
                if (System.currentTimeMillis() - group.getMemberLastGroupSpokeTime(fromQQ) < antiSpamer.getIntervalThreshold()) {
                    group.setSpamBreakVl(fromQQ, group.getSpamBreakVl(fromQQ) + 1);
                } else {
                    // 违规等级
                    int currentVl = group.getSpamBreakVl(fromQQ);

                    // 一次合法的发言降低一级违规等级
                    if (currentVl > 0) {
                        group.setSpamBreakVl(fromQQ, currentVl - 1);
                    }
                }

                // 违规等级超过阈值
                if (group.getSpamBreakVl(fromQQ) >= antiSpamer.getMuteVL()) {
                    String muteMsg =  antiSpamer.getMuteMsg();

                    group.muteMember(fromQQ, antiSpamer.getMuteMinutes() * 60);

                    if (!muteMsg.equals("")) {
                        cq.sendGroupMsg(fromGroup, muteMsg
                                .replace("%qq_name%", getCardOrNick(fromGroup, fromQQ))
                                .replace("%qq_num%", String.valueOf(fromQQ))
                                .replace("%mute_minutes%", String.valueOf(antiSpamer.getMuteMinutes())));
                    }

                    // 重置次数
                    group.setSpamBreakVl(fromQQ, 0);
                    return 1;
                }

                group.setMemberLastGroupSpokeTime(fromQQ, System.currentTimeMillis());
            }
        }

        return 0;
    }

    // 正则过滤器
    int doRegexFilter(int subType, int msgID, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        if (settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);

            if (group.getWhitelist() != null && group.getWhitelist().contains(fromQQ)) {
                return 0;
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
                                    .replace("%qq_name%", getCardOrNick(fromGroup, fromQQ))
                                    .replace("%qq_num%", String.valueOf(fromQQ))
                            );
                        }

                        return 1;
                    }
                }
            }
        }

        return 0;
    }

    // 加群
    int doAddGroupMsgAndRemute(int subType, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
        if (settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);

            if (group.isAutoAcceptJoinRequest()) {
                String joinMsg = group.getJoinMsg();

                cq.setGroupAddRequest(responseFlag, subType, REQUEST_ADOPT, null);

                // 防止玩家退群重加消除禁言时间
                long muteRemainingTime = group.getMemberMuteRemainingTime(fromQQ);

                if (muteRemainingTime > 0) {
                    group.muteMember(fromQQ, muteRemainingTime);
                    return 1;
                }

                if (!joinMsg.equals("")) {
                    cq.sendGroupMsg(fromGroup, joinMsg
                            .replace("%qq_name%", getCardOrNick(fromGroup, fromQQ))
                            .replace("%qq_num%", String.valueOf(fromQQ))
                    );
                }

                return 1;
            }
        }

        return 0;
    }

    // 管理员命令
    int doAdminCommands(int subType, int msgID, long fromQQ, String msg, int font) {
        if (fromQQ == settings.getOwnerQQ() && Objects.equals(msg, "#ccqb reload")) {
            plugin.loadConfig();
            cq.sendPrivateMsg(fromQQ, "重载配置成功!");

            return 1;
        }

        return 0;
    }

    // 名字检查
    int doGroupNickChecker(int subType, int msgID, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        if (settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);

            if (group.getWhitelist() != null && group.getWhitelist().contains(fromQQ)) {
                return 0;
            }

            GroupNickChecker groupNickChecker = group.getGroupNickChecker();

            for (String keyWord : groupNickChecker.getBlackKeywords()) {
                String name = getCardOrNick(fromGroup, fromQQ);

                if (name.contains(keyWord)) {
                    boolean withdraw = groupNickChecker.isWithdraw();
                    String punishMsg = groupNickChecker.getPunishMsg();

                    group.muteMember(fromQQ, groupNickChecker.getMuteMinutes() * 60);

                    if (withdraw) {
                        cq.deleteMsg(msgID);
                    }

                    if (groupNickChecker.isResetCard()) {
                        Member member = cq.getGroupMemberInfo(fromGroup, fromQQ, true);

                        if (member != null) {
                            cq.setGroupCard(fromGroup, fromQQ, "-");
                        }
                    }

                    if (!punishMsg.equals("")) {
                        cq.sendGroupMsg(fromGroup, punishMsg
                                .replace("%punish_type%", withdraw ? "撤回&禁言" + groupNickChecker.getMuteMinutes() + "(分钟)" :  "禁言" + groupNickChecker.getMuteMinutes() + "(分钟)")
                                .replace("%qq_name%", name)
                                .replace("%qq_num%", String.valueOf(fromQQ))
                        );
                    }

                    return 1;
                }
            }
        }

        return 0;
    }

    // 踢出
    int doMemberKickMsg(int subType, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        if (subType == 2 && settings.getGroups().containsKey(fromGroup)) {
            QQGroup group = settings.getGroups().get(fromGroup);
            String kickMsg = group.getKickMsg();
            QQInfo strangerInfo = cq.getStrangerInfo(beingOperateQQ);
            String nick;

            if (!kickMsg.equals("")) {
                cq.sendGroupMsg(fromGroup, kickMsg
                        .replace("%qq_name%", strangerInfo == null ? "" : (nick = strangerInfo.getNick()) == null ? "" : nick)
                        .replace("%qq_num%", String.valueOf(beingOperateQQ))
                );

                return 1;
            }
        }

        return 0;
    }

    private String getCardOrNick(long fromGroup, long fromQQ) {
        Member member = cq.getGroupMemberInfo(fromGroup, fromQQ, true);
        String card;
        String nick;

        return member == null ? "" :
                (card = member.getCard()) == null || card.equals("") ?
                        (nick = member.getNick()) == null ? "" : nick : card;

    }
}
