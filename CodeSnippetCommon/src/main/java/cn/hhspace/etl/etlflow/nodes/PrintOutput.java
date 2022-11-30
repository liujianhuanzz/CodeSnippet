package cn.hhspace.etl.etlflow.nodes;

import cn.hhspace.etl.etlflow.FlowRunEngine;
import cn.hhspace.etl.etlflow.OutputNode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/18 14:38
 * @Descriptions: 将处理后的数据输出到标准输出的插件
 */
public class PrintOutput extends OutputNode {
    @Override
    protected void outputRec(FlowRunEngine engine, JSONObject rootObj) {
        String s = JSON.toJSONString(rootObj, SerializerFeature.PrettyFormat);
        //System.out.println(s);
    }
}
