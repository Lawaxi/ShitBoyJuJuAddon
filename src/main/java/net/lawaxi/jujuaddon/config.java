package net.lawaxi.jujuaddon;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.setting.Setting;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class config {

    private final Setting setting;
    public boolean proxy;
    public String schedule;
    public long groupId;
    public List<String> subscribes;

    public config(File file) {
        if (!file.exists()) {
            FileUtil.touch(file);
            Setting setting = new Setting(file, StandardCharsets.UTF_8, false);
            setting.set("proxy", "true");
            setting.set("schedule", "0 0 0-2,10-23 * * ?");
            setting.set("group", "817151561");
            setting.set("subscribes", "");
            setting.store();
        }

        this.setting = new Setting(file, StandardCharsets.UTF_8, false);
        init();
    }

    private void init() {
        proxy = this.setting.getBool("proxy", true);
        schedule = this.setting.getStr("schedule", "0 0 0-2,10-23 * * ?");
        groupId = this.setting.getLong("group");
        String[] subs = this.setting.getStrings("subscribes");
        subscribes = subs == null ? new ArrayList<>() : Arrays.asList(subs);
    }

    public void subscribe(long id) {
        subscribes.add(String.valueOf(id));
        this.setting.set("subscribes", ArrayUtil.join(subscribes, ","));
        this.setting.store();
    }

    public boolean unsubscribe(long id) {
        if (subscribes.contains(String.valueOf(id))) {
            subscribes.remove(String.valueOf(id));
            this.setting.set("subscribes", ArrayUtil.join(subscribes, ","));
            this.setting.store();
            return true;
        } else {
            return false;
        }
    }
}
