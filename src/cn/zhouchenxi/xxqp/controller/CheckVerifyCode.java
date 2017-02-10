package cn.zhouchenxi.xxqp.controller;

import com.google.gson.Gson;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by zhouchenxi on 2017/1/5.
 */
public class CheckVerifyCode {


    private static String returnCookie;

    private static String picBase64Data;

    private static String recognizeResult;


    /**
     *
     * @param setCookie 是否带上cookie，不带保持为空就行了
     * @return
     */
    private static int getVerifyCode(String setCookie){
        String UrlOf12306 = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&0.06286522761897828";
        HashMap result = HttpRequest.process(UrlOf12306,"GET","",setCookie,1);
        if (result.get("state").equals("0")) {
            return 0;
        }
        returnCookie = (String) result.get("cookie");
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] picByte = (byte[]) result.get("data");
        if (picByte==null) {
            return 0;
        }
        picBase64Data = encoder.encode(picByte);
        try {
            picBase64Data = URLEncoder.encode(picBase64Data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int recognizeVerifyCode(){

        String UrlOfQQ = "http://12306.qq.com/api/get_code";
        long time = System.currentTimeMillis();
        String parameter = "method=upload" +
                "&ts=" + time +
                "&guid=5defd8fef91599c627485430d0c5e729" +
                "&file=" + picBase64Data +
                "&code_type=";
        HashMap result = HttpRequest.process(UrlOfQQ,"POST",parameter,"",0);
        if(result.get("state").equals("0")){
            return 0;
        }
        byte[] verifyData = (byte[]) result.get("data");
        String verifyString = new String(verifyData);
        System.out.println("识别结果："+verifyString);
        Gson gson = new Gson();
        VerifyResult re = gson.fromJson(verifyString, VerifyResult.class);
        if (re.getRet() != 1) {
            return 0;
        } else {
            recognizeResult = re.getData();
            return 1;
        }
    }

    private static int checkVerifyCode(){
        String UrlOf12306 = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
        String parameter = "randCode="+recognizeResult +
                "&rand=sjrand";
        HashMap result = HttpRequest.process(UrlOf12306, "POST", parameter, returnCookie, 1);
        if (result.get("state").equals("0")) {
            return 0;
        }
        byte[] checkData = (byte[]) result.get("data");
        if (checkData != null) {
            String checkString = new String(checkData);
            System.out.println("12306返回验证结果："+checkString);
            Gson gson = new Gson();
            CheckResult res = gson.fromJson(checkString, CheckResult.class);
            if(res.getData().getMsg().equals("")){

            }
            return res.getData().getResult();
        } else {

            return 0;
        }

    }


}
