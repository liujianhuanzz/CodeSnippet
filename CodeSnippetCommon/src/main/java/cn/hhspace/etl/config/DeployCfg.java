package cn.hhspace.etl.config;

import cn.hhspace.etl.utils.ResourceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 15:32
 * @Descriptions: 部署一个流程的配置抽象类
 */
public abstract class DeployCfg extends Config {

    private static final String DEPLOY_CFG_FILE_NAME = "conf/deployCfgMapping.json";

    private static JSONObject deployServerMapping = null;

    @JsonIgnore
    public String getDeployServerClassName() throws Exception {
        if (null == deployServerMapping) {
            String jsonStr = ResourceUtil.readClassFileToStr(DEPLOY_CFG_FILE_NAME);
            deployServerMapping = JSON.parseObject(jsonStr);
        }
        String cfgClassName = this.getClass().getName();
        String deployServerClassName = (String) deployServerMapping.get(cfgClassName);
        if (null == deployServerClassName) {
            throw new Exception("此配置类找不到对应的ETLSERVER，配置类：" + cfgClassName);
        }
        return deployServerClassName;
    }

    @JsonIgnore
    public int getControlPort(){
        String controlPort = this.getParamMap().get("controlPort");
        if (null != controlPort) {
            try {
                return Integer.parseInt(controlPort);
            } catch (NumberFormatException e) {
                return -1;
            }

        }
        return 0;
    }
}
