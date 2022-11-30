package cn.hhspace.etl.config;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 19:36
 * @Descriptions: 必填项，比正常的配置项多一个remark字段
 */
public class RemarkParamItem extends ParamItem {

    public final static String PW_FORMAT = "password";

    public final static String PW_MASK = "**********";

    private String remark;

    private String format;

    protected boolean advanced = true;

    public RemarkParamItem() {
        this.inherent = true;
    }

    public RemarkParamItem(RemarkParamItem rpi) {
        this.setFormat(rpi.getFormat());
        this.setRemark(rpi.getRemark());
        this.key = rpi.getKey();
        this.value = rpi.getValue();
        this.inherent = rpi.isInherent();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }
}
