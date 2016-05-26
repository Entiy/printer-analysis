package com.github.openthos.printer.localprint.task;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bboxh on 2016/5/16.
 */
public class PrintTask<Progress> extends CommandTask<Map<String, String>, Progress, Integer> {
    public static final String LP_PRINTER = "printer";
    public static final String LP_FILE = "file";
    public static final String LP_MEDIA = "media";
    public static final String LP_RESOLUTION = "resolution";
    public static final String LP_LANDSCAPE = "landscape";
    public static final String LP_LABEL = "label";
    public static final String LP_COPIES = "cpoies";

    protected int JOB_ID = -1;        //填充任务编号

    @Override
    protected String[] setCmd(Map<String, String>... params) {
        Map<String, String> map = params[0];
        String printerName = map.get(LP_PRINTER);
        String fileName = map.get(LP_FILE);
        String media = map.get(LP_MEDIA);
        String resolution = map.get(LP_RESOLUTION);
        String landscape=map.get(LP_LANDSCAPE);
        String label = map.get(LP_LABEL);
        String copies = map.get(LP_COPIES);

        // TODO: 2016/5/16 打印 C1

        List<String> list = new ArrayList<String>();
        list.add("sh");
        list.add("proot.sh");
        list.add("lp");

        if ( !printerName.isEmpty()) {
            list.add("-d");
            list.add(printerName);
        }
        if ( !fileName.isEmpty()){
            list.add(fileName);
        }
        if (!media.isEmpty()){
            list.add("-o");
            list.add("media="+media);
        }
        if ( !resolution.isEmpty()){
            list.add("-o");
            list.add("Resolution="+resolution);
        }
        if (!landscape.isEmpty()){
            list.add("-o");
            list.add(landscape);
        }
        if (!label.isEmpty()){
            list.add("-t");
            list.add(label);
        }
        if(!copies.isEmpty()){
            list.add("-n");
            list.add(copies);
        }

        String[] cmd = (String[]) list.toArray();

        return cmd;
    }

    @Override
    protected final Integer handleCommand(List<String> stdOut, List<String> stdErr) {

        int flag = -1;

        for(String line: stdErr){

            if( line.startsWith("WARNING") )
                continue;
            else if (line.contains("Bad file descriptor")){
                if( startCups() ){
                    runCommandAgain();      //再次运行命令
                    return -1;
                }else{
                    ERROR = "Cups start failed.";
                    return -1;
                }
            }


        }

        for(String line: stdOut){
            if(line.startsWith("request id is")){
                //处理打印成功的数据，按空格分隔
                String[] data = line.split("\\s+");
                Log.d(TAG, "request id is -> " + data[3]);
            }else if(line.contains("scheduler not responding")){
                if( startCups() ){
                    runCommandAgain();      //再次运行命令
                    return -1;
                }else{
                    ERROR = "Cups start failed.";
                    return -1;
                }
            }else if (line.contains("No such file or directory")){
                //待打印文件不存在

            }
        }

        return flag;
    }

    @Override
    protected String bindTAG() {
        return "PrintTask";
    }
}
