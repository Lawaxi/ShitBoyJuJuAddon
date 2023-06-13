package net.lawaxi.jujuaddon;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class roomids {
    private static final String api1 = "https://api.github.com/repos/duan602728596/qqtools/commits";
    private static final String api2 = "https://github.com/duan602728596/qqtools/blob/%s/packages/NIMTest/node/roomId.json";

    private static String getApi2() {
        return ShitBoyJuJuAddon.config.proxy ? "https://ghproxy.com/" + api2 : api2;
    }

    public final File dataFile;

    private long[] roomIds;

    public roomids(File file) {
        this.dataFile = file;
        if (!file.exists()) {
            download();
        }
        init();
    }

    public void download() {
        try {
            String sha = null;
            while (sha == null) {
                String r = HttpUtil.get(api1);
                if (r != null) {
                    sha = JSONUtil.parseObj(JSONUtil.parseArray(r).get(0)).getStr("sha");
                }
            }

            HttpUtil.downloadFile(getApi2(), dataFile);
        } catch (Exception e) {
            e.printStackTrace();
            ShitBoyJuJuAddon.INSTANCE.getLogger().info("下载成员房间列表错误");
        }
    }

    public void init() {
        try {
            JSONObject a = JSONUtil.parseObj(FileUtil.readUtf8String(dataFile));
            Object[] b = a.getJSONArray("roomId").toArray();
            List ids = new ArrayList<>();
            for (Object b0 : b) {
                JSONObject room = JSONUtil.parseObj(b0);
                if (room.containsKey("roomId"))
                    ids.add(Long.valueOf(room.getStr("roomId")));

            }

            this.roomIds = ids.stream().mapToLong(t -> ((Long) t).longValue()).toArray();
        } catch (Exception e) {
            e.printStackTrace();
            ShitBoyJuJuAddon.INSTANCE.getLogger().info("读取本地成员房间列表错误");
        }
    }

    public long[] getRoomIds() {
        return this.roomIds;
    }

    public int getRoomAmount() {
        return getRoomIds().length;
    }
}
