package net.lawaxi.jujuaddon.u;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.lawaxi.model.Pocket48Message;

public class SJAMessage extends Pocket48Message {

    private final long userId;
    private final String room;

    public SJAMessage(String room, String ownerName, String nickName, long userId, String type, String body, long time) {
        super(null, nickName, ownerName, type, body, time);
        this.userId = userId;
        this.room = room;
    }

    public static final SJAMessage construct2(String roomName, String ownerName, JSONObject m, long time) {
        JSONObject extInfo = JSONUtil.parseObj(m.getObj("extInfo"));
        JSONObject user = JSONUtil.parseObj(extInfo.getObj("user"));
        return new SJAMessage(roomName, ownerName, user.getStr("nickName"), user.getLong("userId"), m.getStr("msgType"), m.getStr("bodys"), time);
    }

    public String getRoomName() {
        return room;
    }

    public long getUserId() {
        return this.userId;
    }
}
