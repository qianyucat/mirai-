package com.qianyucat;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("com.qianyucat.plugin", "1.0-SNAPSHOT")
                .name("qqbot")
                .author("qianyu")
                .build());
    }

    Config config = new Config();


    @Override
    public void onEnable() {

        getLogger().info("Plugin loaded!");

        //初始化
        getLogger().info(config.toString());


        //如果缓存照片小于最大缓存，则开始缓存
        if (config.cache < config.maxCache) {
            cacheImages(config.maxCache);
        }


        // 创建监听
        Listener listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            String content = event.getMessage().serializeToMiraiCode();
            getLogger().info(content);

            if (content.contains("[mirai:at:2890151169]")) {
                if (content.contains("色图") || content.contains("涩图") || content.contains("setu")) {

                    PlainText text = new PlainText("喵");

                    MessageChain chain = text.plus(":");

                    //获取缓存图片张数
                    config.updateCache();
                    //更新图片指针
                    int point = config.point;
                    config.point = config.point + config.num;
                    if (config.point >= config.maxCache) {
                        config.point = 0;
                    }
                    config.saveConfig();
                    //补充缓存
                    if (config.cache == 0) {
                        event.getSubject().sendMessage("缓存中，请耐心等待。");
                        cacheImages(config.maxCache);
                    } else {
                        event.getSubject().sendMessage("收到啦，马上就来！");
                    }

                    //开始发送图片
                    String dirName = "./data/imageData/";
                    try {
                        for (int i = point; i < point + config.num; i++) {
                            File file = new File(dirName + i + ".png");
                            ExternalResource er = ExternalResource.create(file);

                            try {
                                chain = chain.plus(event.getSubject().uploadImage(er));

                            } catch (Exception e) {
                                getLogger().error(e.getMessage());
                            }
                            er.close();
                            FileUtils.forceDelete(file);
                        }

                    } catch (IOException e) {
                        getLogger().error(e.getMessage());
                    } finally {
                        event.getSubject().sendMessage(chain); // 回复消息
                    }
                }
            }



        });



        //listener.complete(); // 停止监听
    }

    public void download(String urlString, int i) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //添加请求头
        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36");
        // 输入流
        InputStream is = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        String filename = "./data/imageData/" + i + ".png";  //下载路径及下载图片名称
        File file = new File(filename);
        FileOutputStream os = new FileOutputStream(file);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        //System.out.println(i);
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

    private void cacheImages(int num) {
        getLogger().info("cacheImages");
        String base_url = "http://pan-yz.chaoxing.com/external/m/file/";
        List<String> urls = new ArrayList<String>();
        int i = config.index;
        int n = i + num;
        //获取图片链接
        while (i < n) {
            urls.add(base_url + config.picName.get(i));
            i++;
        }
        //超星获取直链
        for(int k=0;k < urls.size(); k++) {
            urls.set(k,getRequest.getUrl(urls.get(k)));
        }

        //开始缓存图片
        config.index = config.index + num;
        config.saveConfig();
        try {
            for(int j=0;j < urls.size(); j++) {
                getLogger().info(urls.get(j));
                download(urls.get(j),j);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        getLogger().info("cacheImagesOver");
    }

}