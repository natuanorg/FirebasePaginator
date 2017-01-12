package com.natuan.firebasepaginator;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by natuan on 16/12/11.
 */
@IgnoreExtraProperties
public class Video {

    public String cateId;
    public String desc;
    public String name;
    public String path;
    public String thumb;

    public Video() {}

    public Video(String cateId, String desc, String name, String path, String thumb) {
        this.cateId = cateId;
        this.desc = desc;
        this.name = name;
        this.path = path;
        this.thumb = thumb;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cateId", cateId);
        result.put("desc", desc);
        result.put("name", name);
        result.put("path", path);
        result.put("thumb", thumb);
        return result;
    }
}
