package com.qianyucat;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static sun.font.FontUtilities.getLogger;

public class Config {
    int index;
    int num;
    int cache;
    int maxCache;
    int point;
    List<String> picName;

    Config() {
        readConfig();
        updatePicName();
    }

    public void readConfig() {
        File file = new File("./data/config.json");//设定为当前文件夹
        try {
            //读取json配置文件
            String content= FileUtils.readFileToString(file,"UTF-8");
            JSONObject jsonObject=new JSONObject(content);
            index = jsonObject.getInt("index");
            num = jsonObject.getInt("num");

            maxCache = jsonObject.getInt("maxCache");
            point = 0;
            updateCache();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePicName() {
        try {
            picName = FileUtils.readLines(new File("./data/list.txt"),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateCache() {
        String path = "./data/imageData";
        int fileCount = 0;
        File d = new File(path);
        File list[] = d.listFiles();
        for(int i = 0; i < list.length; i++){
            if(list[i].isFile()){
                fileCount++;
            }
        }
        cache = fileCount;
    }
    public  void saveConfig() {
        JSONObject config = new JSONObject();
        config.put("index", index);
        config.put("num", num);
        config.put("maxCache", maxCache);
        config.put("point", point);
        String c = config.toString();
        try {
        FileUtils.writeStringToFile(new File("./data/config.json"), c);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "index:" + index + ",num:"+ num +  ",cache:" + cache +  ",maxCache:" + maxCache + ",point:" + point;
    }

}
