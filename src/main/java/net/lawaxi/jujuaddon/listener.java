package net.lawaxi.jujuaddon;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class listener extends SimpleListenerHost {

    @EventHandler()
    public ListeningStatus onGroupMessageEvent(GroupMessageEvent event) {
        config config = ShitBoyJuJuAddon.config;
        Group group = event.getGroup();

        if (group.getId() == config.groupId) {
            String message = event.getMessage().contentToString();
            if (message.startsWith("加关注 ")) {
                try {
                    long id = Long.valueOf(message.substring(message.indexOf(" ") + 1));
                    config.subscribe(id);
                    String nickName = ShitBoyJuJuAddon.handler.getUserInfo(id).getStr("nickname");
                    group.sendMessage("新增聚聚关注：" + nickName + "(" + id + ")");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (message.startsWith("取消关注 ")) {
                try {
                    long id = Long.valueOf(message.substring(message.indexOf(" ") + 1));
                    if (config.unsubscribe(id)) {
                        group.sendMessage("取消聚聚关注：" + id);
                    } else {
                        group.sendMessage("本群原没有关注此聚聚");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (message.startsWith("聚聚关注")) {
                Long[] subs = ShitBoyJuJuAddon.INSTANCE.getSubs();
                String out = "本群聚聚关注：\n";
                for (int i = 0; i < subs.length; i++) {
                    String nickName = "null";
                    try {
                        nickName = ShitBoyJuJuAddon.handler.getUserInfo(subs[i]).getStr("nickname");
                    } catch (Exception e) {

                    }
                    out += (i + 1) + "." + nickName + "(" + subs[i] + ")\n";
                }
                group.sendMessage(out);
            }
        }

        return ListeningStatus.LISTENING;
    }
}
