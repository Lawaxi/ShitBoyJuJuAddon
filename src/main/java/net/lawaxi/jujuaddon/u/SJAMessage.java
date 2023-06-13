package net.lawaxi.jujuaddon.u;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.lawaxi.model.Pocket48Message;

public class SJAMessage extends Pocket48Message {

    private final long userId;

    public SJAMessage(String room, String ownerName, String nickName, long userId, String type, String body, long time) {
        super(room, ownerName, nickName, "", type, body, time);
        this.userId = userId;
    }


    public static final SJAMessage construct2(String roomName, String ownerName, JSONObject m, long time) {
        JSONObject extInfo = JSONUtil.parseObj(m.getObj("extInfo"));
        JSONObject user = JSONUtil.parseObj(extInfo.getObj("user"));
        return new SJAMessage(roomName, ownerName, user.getStr("nickName"), user.getLong("userId"), m.getStr("msgType"), m.getStr("bodys"), time);
    }

    public long getUserId() {
        return userId;
    }
}
