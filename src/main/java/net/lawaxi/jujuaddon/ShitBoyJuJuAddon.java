package net.lawaxi.jujuaddon;

import cn.hutool.core.convert.Convert;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.Scheduler;
import net.lawaxi.Shitboy;
import net.lawaxi.jujuaddon.u.SJAHandler;
import net.lawaxi.jujuaddon.u.SJASender;
import net.lawaxi.model.Pocket48Message;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.plugin.Plugin;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;

import java.util.Date;
import java.util.List;

public final class ShitBoyJuJuAddon extends JavaPlugin {
    public static final ShitBoyJuJuAddon INSTANCE = new ShitBoyJuJuAddon();
    public static Shitboy INSTANCE_SHITBOY;
    public static config config;
    public static roomids data;
    public static SJAHandler handler;

    private ShitBoyJuJuAddon() {
        super(new JvmPluginDescriptionBuilder("net.lawaxi.shitboyjja", "0.1.2-test6")
                .name("ShitBoyJuJuAddon")
                .author("delay0delay")
                .dependsOn("net.lawaxi.shitboy", false)
                .build());
    }

    @Override
    public void onEnable() {
        config = new config(resolveConfigFile("config.setting"));
        data = new roomids(resolveConfigFile("roomId.json"));
        if (loadShitboy()) {
            if (INSTANCE_SHITBOY.getProperties().save_login) {
                getLogger().info("请关闭Shitboy配置中save_login");
                INSTANCE_SHITBOY = null;
                return;
            }
            handler = new SJAHandler(INSTANCE_SHITBOY.getHandlerPocket48());
            listenBroadcast();
            GlobalEventChannel.INSTANCE.registerListenerHost(new listener());
        }
    }


    private void listenBroadcast() {
        long[] roomIds = data.getRoomIds();
        long[] endTime = new long[roomIds.length];
        final long now = new Date().getTime();
        for (int i = 0; i < endTime.length; i++) {
            endTime[i] = now;
        }
        Scheduler jja = new Scheduler();
        jja.schedule(config.schedule, new Runnable() { //每一小时
                    @Override
                    public void run() {
                        List<Pocket48Message> m = null;
                        for (Bot b : Bot.getInstances()) {
                            getLogger().info("on search");
                            new SJASender(b, config.groupId, handler, roomIds, endTime, m).run();
                        }
                    }
                }
        );
        jja.start();
    }

    private boolean loadShitboy() {
        for (Plugin plugin : PluginManager.INSTANCE.getPlugins()) {
            if (plugin instanceof Shitboy) {
                INSTANCE_SHITBOY = (Shitboy) plugin;
                getLogger().info("读取Shitboy插件成功");
                return true;
            }
        }
        getLogger().warning("请安装0.1.7-test16以上版本的Shitboy插件再使用本Addon");
        return false;
    }

    public Long[] getSubs() {
        return Convert.toLongArray(config.subscribes);
    }
}