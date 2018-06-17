package vip.ourcraft.coolqplugins.creepercoolqbot;

import com.sobte.cqp.jcq.event.JcqAppAbstract;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.AntiSpamer;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.GroupNickChecker;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.QQGroup;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.RegexFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by July on 2018/05/26.
 */
public class CreeperCoolQBot extends JcqAppAbstract {
    private Settings settings;
    private Functions functions;

    public Settings getSettings() {
        return settings;
    }

    public boolean loadConfig() {
        File file = new File(CQ.getAppDirectory() + "config.conf");

        if (!file.exists()) {
            try {
                Files.copy(getClass().getClassLoader().getResourceAsStream("config.conf"), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        HashMap<Long, QQGroup> groups = new HashMap<>();
        Config config = ConfigFactory.parseFile(file);

        settings.setMsgPrefix(config.getString("msg_prefix"));
        settings.setOwnerQQ(config.getLong("owner_qq"));

        // 得到所有群的根配置
        for (Config groupConfig : config.getConfigList("qq_groups")) {
            Config antiSpamerConfig = groupConfig.getConfig("anti_spamer"); // 反刷屏根配置
            List<? extends Config> regexFiltersConfigs = groupConfig.getConfigList("regex_filters"); // 正则筛选器根配置列表
            Config groupNickCheckerConfig = groupConfig.getConfig("group_nick_checker"); // 群昵称根配置

            QQGroup group = new QQGroup();
            AntiSpamer antiSpamer = null;
            GroupNickChecker groupNickChecker = null;
            List<RegexFilter> regexFilters = null;

            // 存储正则筛选器到list中
            for (Config regexFilterConfig : regexFiltersConfigs) {
                if (!regexFilterConfig.isEmpty()) {
                    RegexFilter regexFilter = new RegexFilter();

                    regexFilter.setRegex(regexFilterConfig.getString("regex"));
                    regexFilter.setWithdraw(regexFilterConfig.getBoolean("withdraw"));
                    regexFilter.setMuteMinutes(regexFilterConfig.getInt("mute_minutes"));
                    regexFilter.setPunishMsg(regexFilterConfig.getString("punish_msg"));

                    if (regexFilters == null) {
                        regexFilters = new ArrayList<>();
                    }

                    regexFilters.add(regexFilter);
                }
            }

            // 设置反刷屏
            if (!antiSpamerConfig.isEmpty()) {
                antiSpamer = new AntiSpamer();

                antiSpamer.setIntervalThreshold(antiSpamerConfig.getInt("interval_threshold"));
                antiSpamer.setMuteVL(antiSpamerConfig.getInt("mute_vl"));
                antiSpamer.setMuteMinutes(antiSpamerConfig.getInt("mute_minutes"));
                antiSpamer.setMuteMsg(antiSpamerConfig.getString("mute_msg"));
            }

            // 设置群昵称检查者
            if (!groupNickCheckerConfig.isEmpty()) {
                groupNickChecker = new GroupNickChecker();
                groupNickChecker.setBlackKeywords(groupNickCheckerConfig.getStringList("black_keywords"));
                groupNickChecker.setWithdraw(groupNickCheckerConfig.getBoolean("withdraw"));
                groupNickChecker.setMuteMinutes(groupNickCheckerConfig.getInt("mute_minutes"));
                groupNickChecker.setPunishMsg(groupNickCheckerConfig.getString("punish_msg"));
            }

            // 应用到group中
            group.setGroupID(groupConfig.getLong("group_id"));
            group.setAutoAcceptJoinRequest(groupConfig.getBoolean("auto_accept_join_request"));
            group.setJoinMsg(groupConfig.getString("join_msg"));
            group.setAntiSpamer(antiSpamer);
            group.setRegexFilters(regexFilters);
            group.setWhitelist(groupConfig.getLongList("whitelist"));
            group.setGroupNickChecker(groupNickChecker);

            CQ.logDebug("qn", group.getGroupID() + "");
            groups.put(group.getGroupID(), group);
        }

        settings.setGroups(groups);
        return true;
    }

    @Override
    public String appInfo() {
        return "1.0.0,vip.ourcraft.coolqplugins.creepercoolqbot.CreeperCoolQBot";
    }

    @Override
    public int startup() {
        this.settings = new Settings();
        this.functions = new Functions(this);

        loadConfig();
        return 0;
    }

    @Override
    public int exit() {
        return 0;
    }

    @Override
    public int enable() {
        return 0;
    }

    @Override
    public int disable() {
        return 0;
    }

    @Override
    public int privateMsg(int subType, int msgID, long fromQQ, String msg, int font) {
        functions.doAdminCommands(subType, msgID, fromQQ, msg, font);

        return 0;
    }

    @Override
    public int groupMsg(int subType, int msgID, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        functions.doAntiSpamer(subType, msgID, fromGroup, fromQQ, fromAnonymous, msg, font);
        functions.doRegexFilter(subType, msgID, fromGroup, fromQQ, fromAnonymous, msg, font);
        functions.doGroupNickChecker(subType, msgID, fromGroup, fromQQ, fromAnonymous, msg, font);
        return 0;
    }

    @Override
    public int discussMsg(int subType, int msgID, long fromDiscuss, long fromQQ, String msg, int font) {
        return 0;
    }

    @Override
    public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
        return 0;
    }

    @Override
    public int groupAdmin(int subType, int sendTime, long fromGroup, long beingOperateQQ) {
        return 0;
    }

    @Override
    public int groupMemberDecrease(int subType, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        return 0;
    }

    @Override
    public int groupMemberIncrease(int subType, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        return 0;
    }

    @Override
    public int friendAdd(int subType, int sendTime, long fromQQ) {

        return 0;
    }

    @Override
    public int requestAddFriend(int subType, int sendTime, long fromQQ, String msg, String responseFlag) {
        return 0;
    }

    @Override
    public int requestAddGroup(int subType, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
        functions.doAddGroup(subType, sendTime, fromGroup, fromQQ, msg, responseFlag);
        return 0;
    }
}
