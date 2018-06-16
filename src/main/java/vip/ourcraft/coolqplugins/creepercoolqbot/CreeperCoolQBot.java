package vip.ourcraft.coolqplugins.creepercoolqbot;

import com.sobte.cqp.jcq.event.JcqAppAbstract;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.AntiSpamer;
import vip.ourcraft.coolqplugins.creepercoolqbot.entities.QqGroup;
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

        HashMap<Long, QqGroup> groups = new HashMap<>();
        Config config = ConfigFactory.parseFile(file);

        settings.setMsgPrefix(config.getString("msg_prefix"));
        settings.setOwnerQq(config.getLong("owner_qq"));

        for (Config groupConfig : config.getConfigList("qq_groups")) {
            Config antiSpamConfig = groupConfig.getConfig("anti_spam");
            List<? extends Config> regexFiltersConfigs = groupConfig.getConfigList("regex_filters");
            QqGroup group = new QqGroup();
            AntiSpamer antiSpamer = new AntiSpamer();
            List<RegexFilter> regexFilters = new ArrayList<>();

            for (Config filterConfig : regexFiltersConfigs) {
                RegexFilter setting = new RegexFilter();

                setting.setRegex(filterConfig.getString("regex"));
                setting.setWithdraw(filterConfig.getBoolean("withdraw"));
                setting.setMuteMinutes(filterConfig.getInt("mute_minutes"));
                setting.setPunishMsg(filterConfig.getString("punish_msg"));

                regexFilters.add(setting);
            }

            antiSpamer.setIntervalThreshold(antiSpamConfig.getInt("interval_threshold"));
            antiSpamer.setMuteVL(antiSpamConfig.getInt("mute_vl"));
            antiSpamer.setMuteMinutes(antiSpamConfig.getInt("mute_minutes"));
            antiSpamer.setMuteMsg(antiSpamConfig.getString("mute_msg"));

            group.setGroupId(groupConfig.getLong("group_id"));
            group.setAutoAcceptJoinRequest(groupConfig.getBoolean("auto_accept_join_request"));
            group.setJoinMsg(groupConfig.getString("join_msg"));
            group.setAntiSpamer(antiSpamer);
            group.setRegexFilters(regexFilters);
            group.setWhitelist(groupConfig.getLongList("whitelist"));
            groups.put(group.getGroupId(), group);
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
    public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
        functions.doAdminCommands(subType, msgId, fromQQ, msg, font);

        return 0;
    }

    @Override
    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        functions.doAntiSpam(subType, msgId, fromGroup, fromQQ, fromAnonymous, msg, font);
        functions.doRegexFilter(subType, msgId, fromGroup, fromQQ, fromAnonymous, msg, font);
        return 0;
    }

    @Override
    public int discussMsg(int subType, int msgId, long fromDiscuss, long fromQQ, String msg, int font) {
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
