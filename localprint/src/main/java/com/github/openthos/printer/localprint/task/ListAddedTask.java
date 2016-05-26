package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.PrinterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取已添加打印
 * Created by bboxh on 2016/5/16.
 */
public class ListAddedTask<Params, Progress> extends CommandTask<Params, Progress, List<PrinterItem>> {
    @Override
    protected String[] setCmd(Params... params) {
        return new String[]{"sh","proot.sh","lpstat","-v"};
    }

    @Override
    protected List<PrinterItem> handleCommand(List<String> stdOut, List<String> stdErr) {

        for(String line: stdErr){

            if( line.startsWith("WARNING") )
                continue;
            else if (line.contains("Bad file descriptor")){
                if( startCups() ){
                    runCommandAgain();      //再次运行命令
                    return null;
                }else{
                    ERROR = "Cups start failed.";
                    return null;
                }
            }


        }

        // TODO: 2016/5/10 获取已添加打印机 B3
        List<PrinterItem> list = new ArrayList<>();
        for(String line:stdOut){
            String[] splitLine = line.split(" ");
            String currentDeviceName = splitLine[2].substring(0,splitLine[2].length()-1);
            String currentDeviceURL= splitLine[3];
            list.add(new PrinterItem(currentDeviceName,currentDeviceURL,null));
        }

        //示例
        //list.add(new PrinterItem("HP LaserJet Professional P1108", "usb://HP/LaserJet%20Professional%20P1108?serial=000000000Q8D9XVKPR1a","direct"));
        return list;
    }

    @Override
    protected String bindTAG() {
        return "ListAddedTask";
    }
}
