package net.lawaxi.jujuaddon.u;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.lawaxi.Shitboy;
import net.lawaxi.handler.Pocket48Handler;
import net.lawaxi.jujuaddon.ShitBoyJuJuAddon;
import net.lawaxi.model.Pocket48MessageType;
import net.lawaxi.model.Pocket48RoomInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SJAHandler extends Pocket48Handler {
    private final Pocket48Handler pocket;
    private static final String APIMsgAll = "https://pocketapi.48.cn/im/api/v1/team/message/list/all";

    public SJAHandler(Pocket48Handler pocket) {
        this.pocket = pocket;
    }

    @Override
    protected HttpRequest setHeader(HttpRequest request) {
        return ShitBoyJuJuAddon.INSTANCE_SHITBOY.getHandlerPocket48().setHeader_Public(request);
    }

    public SJAMessage[] getMessages(Pocket48RoomInfo roomInfo, long[] endTime, int i) {
        long roomID = roomInfo.getRoomId();
        String roomName = roomInfo.getRoomName();
        String ownerName = this.getOwnerOrTeamName(roomInfo);
        List<Object> msgs = this.getOriMessages(roomID, roomInfo.getSeverId());
        if (msgs == null) {
            return new SJAMessage[0];
        } else {
            List<SJAMessage> rs = new ArrayList();
            Long latest = null;
            Iterator var10 = msgs.iterator();

            while (var10.hasNext()) {
                Object message = var10.next();
                JSONObject m = JSONUtil.parseObj(message);

                long time = m.getLong("msgTime");
                if (latest == null) { //首个
                    latest = time;
                }

                if (time <= endTime[i]) {
                    break;
                }

                //筛选
                switch (Pocket48MessageType.valueOf(m.getStr("msgType"))) {
                    case TEXT:
                    case GIFT_TEXT:
                    case REPLY:
                    case GIFTREPLY:
                        rs.add(SJAMessage.construct2(roomName, ownerName, m, time));
                }
            }

            endTime[i] = latest;
            return rs.toArray(new SJAMessage[0]);
        }
    }

    private List<Object> getOriMessages(long roomID, long serverID) {
        String s = this.post(APIMsgAll, String.format("{\"nextTime\":0,\"serverId\":%d,\"channelId\":%d,\"limit\":100}", serverID, roomID));
        JSONObject object = JSONUtil.parseObj(s);
        if (object.getInt("status") == 200) {
            JSONObject content = JSONUtil.parseObj(object.getObj("content"));
            List<Object> out = content.getBeanList("message", Object.class);
            out.sort((a, b) -> {
                return JSONUtil.parseObj(b).getLong("msgTime") - JSONUtil.parseObj(a).getLong("msgTime") > 0L ? 1 : 0;
            });
            return out;
        } else {
            this.logError(object.getStr("message"));
            return null;
        }
    }
}
