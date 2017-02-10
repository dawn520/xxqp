package cn.zhouchenxi.xxqp.controller;

import com.google.gson.Gson;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by zhouchenxi on 2017/1/5.
 */
public class  ceshi {

    public static int nullMSGNumber = 0;

    public static void main(String args[]){
        System.out.println("--------12306验证码抓取识别开始-----");
        long startTime = System.currentTimeMillis();
        int result=0;
        int mode = 0;
        switch (mode){
            case 0:
                System.out.println("程序运行模式：成功率测试！");
                int allTimes = 10;
                int verifySuccessfulTimes = 0;
                for(int i = 1;i<allTimes+1;i++){
                    result =  fuck12306();
                    if(result==0){
                        long continueTime = System.currentTimeMillis();
                        System.out.println("第"+i+"次"+"失败"+",当前总共花了"+(continueTime-startTime)+"毫秒");
//                        System.out.println("程序先休息1秒钟");
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println("休息完了，继续！");
                        System.out.println("正在请求:第"+(i+1)+"次");
                    }else{
                        long continueTime = System.currentTimeMillis();
                        System.out.println("第"+i+"次"+"识别成功!"+",当前总共花了"+(continueTime-startTime)+"毫秒");
                        verifySuccessfulTimes++;
                    }
                }
                System.out.println("成功率测试结束：总共运行："+allTimes+"次，"+"成功："+verifySuccessfulTimes+"次");
                System.out.println("成功率为："+((double)verifySuccessfulTimes/(double)allTimes)*100+"%");
                System.out.println("出现返回为空的情况："+nullMSGNumber+"次，"+"概率为："+(nullMSGNumber*100/allTimes)+"%");
                break;
            case 1:
                System.out.println("程序运行模式：验证一直到成功！！");
                for(int i = 1;result!=1;i++){
                    result =  fuck12306();
                    if(result==0){
                        long continueTime = System.currentTimeMillis();
                        System.out.println("第"+i+"次"+"失 败"+",当前总共花了"+(continueTime-startTime)+"毫秒");
//                        System.out.println("程序先休息1秒钟");
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println("休息完了，继续！");
                        System.out.println("正在请求:第"+(i+1)+"次");
                    }else{
                        System.out.println("识别成功!");
                    }
                }
                break;
        }



        long endTime = System.currentTimeMillis();
        System.out.println("总共花费"+(endTime-startTime)+"毫秒");
    }

    public static int fuck12306(){

        String UrlOf12306 = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&0.06286522761897828";
        String method = "GET";
        String parameter = "";
        HashMap result = HttpRequest.process(UrlOf12306,method,parameter,"",1);
        long lastTime = System.currentTimeMillis();
        if (result.get("state").equals("0")) {
            return 0;
        }
        String reCookie = (String) result.get("cookie");
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] picByte = (byte[]) result.get("data");
        if (picByte==null) {
            return 0;
        }
        String data = encoder.encode(picByte);
        try {
            data = URLEncoder.encode(data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String UrlOfQQ = "http://12306.qq.com/api/get_code";
        method = "POST";
        long time = System.currentTimeMillis();
        //String time = "1483496669889";

        parameter = "method=upload" +
                "&ts=" + time +
                "&guid=5defd8fef91599c627485430d0c5e729" +
                "&file=" + data +
                "&code_type=";
        result = HttpRequest.process(UrlOfQQ,method,parameter,"",0);
        if(result.get("state").equals("0")){
            System.out.println("获取验证码失败了！");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("休息结束，继续");
            return 0;
        }
        byte[] verifyData = (byte[]) result.get("data");
        String verifyString = new String(verifyData);
        System.out.println("识别结果："+verifyString);
        Gson gson = new Gson();
        VerifyResult re = gson.fromJson(verifyString, VerifyResult.class);
        if (re.getRet() != 1) {
            System.out.println("识别结果：识别失败了");
            System.out.println("程序先休息3秒钟");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("休息结束，继续");
            return 0;
        } else {

            UrlOf12306 = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
            method = "POST";
            parameter = "randCode="+re.getData() +
                    "&rand=sjrand";
            long nowTime = System.currentTimeMillis();
            System.out.println("刷出验证码到现在已经用了："+(nowTime-lastTime)+"毫秒");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = HttpRequest.process(UrlOf12306, method, parameter, reCookie, 1);
            if (result.get("state").equals("0")) {
                return 0;
            }
            byte[] checkData = (byte[]) result.get("data");
            if (checkData != null) {
                String checkString = new String(checkData);
                System.out.println("12306返回验证结果："+checkString);
                CheckResult res = gson.fromJson(checkString, CheckResult.class);
                if(res.getData().getMsg().equals("")){
                    nullMSGNumber++;
                }
                return res.getData().getResult();
            } else {

                return 0;
            }
        }

    }

}
