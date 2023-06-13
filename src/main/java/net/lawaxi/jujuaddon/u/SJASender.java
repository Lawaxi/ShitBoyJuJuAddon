package net.lawaxi.jujuaddon.u;

import net.lawaxi.Shitboy;
import net.lawaxi.handler.Pocket48Handler;
import net.lawaxi.jujuaddon.ShitBoyJuJuAddon;
import net.lawaxi.model.Pocket48Message;
import net.lawaxi.model.Pocket48RoomInfo;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SJASender extends net.lawaxi.util.sender.Sender {
    private final SJAHandler handler;
    private final long[] roomIds;
    private final long[] endTime;

    public SJASender(Bot bot, long group, SJAHandler handler, long[] roomIds, long[] endTime) {
        super(bot, group);
        this.handler = handler;
        this.roomIds = roomIds;
        this.endTime = endTime;
    }

    @Override
    public void run() {
        List<Pocket48Message> m = new ArrayList<>();
        for (int i = 0; i < roomIds.length; i++) {
            Pocket48RoomInfo roomInfo = handler.getRoomInfoByChannelID(roomIds[i]);
            if (roomInfo != null) {
                for (SJAMessage message : handler.getMessages(roomInfo, endTime, i)) {
                    if (fit(message.getUserId())) {
                        m.add(message);
                    }
                }
            }
        }

        m.sort((a, b) -> a.getTime() - b.getTime() > 0 ? 1 : -1);
        for (Pocket48Message message : m) {
            try {
                Message m0 = pharseMessage(message);
                if (m0 == null)
                    ShitBoyJuJuAddon.INSTANCE.getLogger().warning("解析" + message.getOwnerName() + "房间发言错误");
                else
                    group.sendMessage(m0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Message pharseMessage(final Pocket48Message message) throws IOException {

        Pocket48Handler pocket = Shitboy.INSTANCE.getHandlerPocket48();
        String n = message.getNickName();
        String r = message.getRoom() + "(" + message.getOwnerName() + ")";
        String name = "【" + n + "@" + r + "】\n";
        switch (message.getType()) {
            case TEXT:
            case GIFT_TEXT:
                return new PlainText(name).plus(this.pharsePocketTextWithFace(message.getText()));
            case REPLY:
            case GIFTREPLY:
                return new PlainText(name + message.getReply().getNameTo() + "：").plus(this.pharsePocketTextWithFace(message.getReply().getMsgTo())).plus("\n-----").plus(this.pharsePocketTextWithFace(message.getReply().getMsgFrom()));
            default:
                return null;
        }

    }

    private boolean fit(long userId) {
        for (Long a : ShitBoyJuJuAddon.INSTANCE.getSubs()) {
            if (a != null) {
                if (userId == a.longValue())
                    return true;
            }
        }
        return false;
    }

    public Message pharsePocketTextWithFace(String body) {
        String[] a = body.split("\\[.*?\\]", -1);
        if (a.length < 2) {
            return new PlainText(body);
        } else {
            Message out = new PlainText(a[0]);
            int count = 1;

            for (Matcher b = Pattern.compile("\\[.*?\\]").matcher(body); b.find(); ++count) {
                out = out.plus(this.pharsePocketFace(b.group()));
                out = out.plus(a[count]);
            }

            return out;
        }
    }

    public Message pharsePocketFace(String face) {
        if (face.equals("[亲亲]")) {
            face = "[左亲亲]";
        }

        for (int i = 0; i < Face.names.length; ++i) {
            if (Face.names[i].equals(face)) {
                return new Face(i);
            }
        }

        return new PlainText(face);
    }

}
