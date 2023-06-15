package net.lawaxi.jujuaddon;

import net.lawaxi.util.CommandOperator;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class listener extends SimpleListenerHost {

    public listener() {
        CommandOperator.INSTANCE.addHelp(getHelp());
    }

    @EventHandler()
    public ListeningStatus onGroupMessageEvent(GroupMessageEvent event) {
        config config = ShitBoyJuJuAddon.config;
        Group group = event.getGroup();
        if (group.getId() == config.groupId) {
            String message = event.getMessage().contentToString();

            if (message.startsWith("/")) {
                String[] args = message.split(" ");
                if (args[0].equals("/聚聚")) {
                    if (args.length == 3 && args[1].equals("关注")) {
                        long id = Long.valueOf(args[2]);
                        config.subscribe(id);
                        String nickName = ShitBoyJuJuAddon.INSTANCE_SHITBOY.getHandlerPocket48().getUserNickName(id);
                        group.sendMessage("新增聚聚关注：" + nickName + "(" + id + ")");

                    } else if (args.length == 3 && args[1].equals("取消关注")) {
                        long id = Long.valueOf(args[2]);
                        if (config.unsubscribe(id)) {
                            group.sendMessage("取消聚聚关注：" + id);
                        } else {
                            group.sendMessage("本群原没有关注此聚聚");
                        }

                    } else if (args.length == 2 && args[1].equals("关注列表")) {
                        Long[] subs = ShitBoyJuJuAddon.INSTANCE.getSubs();
                        String out = "本群聚聚关注：\n";
                        for (int i = 0; i < subs.length; i++) {
                            out += (i + 1) + "." + ShitBoyJuJuAddon.INSTANCE_SHITBOY.getHandlerPocket48().getUserNickName(subs[i]) + "(" + subs[i] + ")\n";
                        }
                        group.sendMessage(out);

                    } else {
                        group.sendMessage(getHelp());
                    }
                }
            }
        }
        return ListeningStatus.LISTENING;
    }

    public String getHelp() {
        return "【口袋48聚聚相关】\n"
                + "/聚聚 关注 <ID>\n"
                + "/聚聚 取消关注 <ID>\n"
                + "/聚聚 关注列表\n";
    }
}
