package cn.hhspace.etl.etlflow;

import cn.hhspace.etl.common.ErrorCode;
import cn.hhspace.etl.framework.BeanException;
import cn.hhspace.etl.framework.BeanInventory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/18 14:29
 * @Descriptions: 输出节点，本质上还是一个processNode，只是增加了统计
 */
public abstract class OutputNode extends ProcessNode {

    @JsonSchemaTitle("输出筛选条件（JS表达式）")
    public String exportCond;

    private String genFuncName;
    /**
     * 输出一条记录，子类重载
     */
    protected abstract void outputRec(FlowRunEngine engine, JSONObject rootObj);
    /**
     * 输出总数
     */
    @JsonIgnore
    private AtomicLong outputCounts;

    @JsonIgnore
    public long getOutputCounts() {
        if (null == outputCounts) {
            return 0;
        } else {
            return outputCounts.get();
        }
    }

    private String genScript() {
        return "var " +genFuncName + "=function(_in) { return (" +exportCond +");} \n";
    }

    @Override
    public synchronized void init(BeanInventory beanInventory, Flow flow) {
        super.init(beanInventory, flow);
        if (Strings.isNullOrEmpty(exportCond)) {
            genFuncName = null;
        } else {
            genFuncName = "F_" + flow.getId() + "_" + this.getId();
        }
        outputCounts = new AtomicLong(0);
    }

    private String script;
    @Override
    public void prepareRunEngine(FlowRunEngine engine) throws BeanException {
        if (null != genFuncName) {
            script = getScript();
            try {
                engine.getScriptEngine().eval(script);
            } catch (ScriptException e) {
                throw new BeanException(ErrorCode.NODE_SCRIPT_ERROR, this.getId(), e);
            }
        }
        super.prepareRunEngine(engine);
    }

    private String getScript() {
        return "var " +genFuncName + "=function(_in) { return (" +exportCond +");} \n";
    }

    @Override
    public JSONArray process(FlowRunEngine engine, JSONArray in) {
        Invocable invocable = null;
        try {
            if (null != genFuncName) {
                invocable = (Invocable) engine.getScriptEngine();
            }
            for (int i = 0; i < in.size(); i++) {
                JSONObject rootObj = in.getJSONObject(i);
                boolean isOutput = true;
                if (null != invocable) {
                    JSONObject inObj = rootObj.getJSONObject("_in");
                    Object result = invocable.invokeFunction(genFuncName, inObj);
                    if (result.equals(Boolean.TRUE)) {
                        isOutput = true;
                    } else {
                        isOutput = false;
                    }
                }
                if (isOutput) {
                    outputRec(engine, rootObj);
                    outputCounts.incrementAndGet();
                }
            }
            return in;
        } catch (ScriptException e) {
            throw new BeanException(ErrorCode.NODE_SCRIPT_ERROR, this.getId(), e);
        } catch (NoSuchMethodException e) {
            throw new BeanException(ErrorCode.NODE_SCRIPT_ERROR, this.getId(), e);
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        outputCounts = null;
    }
}
