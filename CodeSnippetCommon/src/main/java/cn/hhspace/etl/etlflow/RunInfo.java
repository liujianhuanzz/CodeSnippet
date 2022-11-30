package cn.hhspace.etl.etlflow;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 20:11
 * @Descriptions: 运行时信息
 */
public class RunInfo {

    private String runInstanceId;

    private String deployId;

    public String getRunInstanceId() {
        return runInstanceId;
    }

    public void setRunInstanceId(String runInstanceId) {
        this.runInstanceId = runInstanceId;
    }

    public String getDeployId() {
        return deployId;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }
}
