package net.lawaxi.jujuaddon;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class roomids {
    private static final String api2 = "https://raw.githubusercontent.com/duan602728596/qqtools/main/packages/NIMTest/node/roomId.json";
    public final File dataFile;
    private long[] roomIds;

    public roomids(File file) {
        this.dataFile = file;
        if (!file.exists()) {
            if (!this.download())
                roomIds = new long[0];
        }

        this.init();
    }

    private static String getApi() {
        return ShitBoyJuJuAddon.config.proxy ? "https://ghproxy.com/" + api2 : api2;
    }

    public boolean download() {
        if (!this.dataFile.exists()) {
            FileUtil.touch(this.dataFile);
        }

        try {
            HttpUtil.downloadFile(getApi(), this.dataFile);
            ShitBoyJuJuAddon.INSTANCE.getLogger().info("下载成员房间列表成功");
            return true;
        } catch (Exception var3) {
            var3.printStackTrace();
            ShitBoyJuJuAddon.INSTANCE.getLogger().info("下载成员房间列表错误");
            return false;
        }

    }

    public void init() {
        try {
            JSONObject a = JSONUtil.readJSONObject(this.dataFile, Charset.defaultCharset());
            Object[] b = a.getJSONArray("roomId").toArray();
            List ids = new ArrayList();
            Object[] var4 = b;
            int var5 = b.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Object b0 = var4[var6];
                JSONObject room = JSONUtil.parseObj(b0);
                if (room.containsKey("channelId")) {
                    ids.add(room.getLong("channelId"));
                }
            }

            this.roomIds = ids.stream().mapToLong((t) -> {
                return (Long) t;
            }).toArray();
            ShitBoyJuJuAddon.INSTANCE.getLogger().info("读取本地成员房间列表成功，共" + getRoomAmount() + "个");
        } catch (Exception var9) {
            var9.printStackTrace();
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
