package cn.zhouchenxi.xxqp.controller;

import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by zhouchenxi on 2016/3/5.
 * 连接到到服务器的类的封装
 * 2016/4/15更新
 */

public class connectServer {


    public static HashMap getData(String requestUrl,String method, HashMap map)  {
        HashMap remap = new HashMap<>();
        try {
            // 请求的地址
            String spec = requestUrl;
            // 根据地址创建URL对象
            URL url = new URL(spec);
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod(method);
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 遍历map输出为要传递的数据
            String data ="";
            int isFirst = 1;
            for (Object obj : map.keySet()) {
                Object value = map.get(obj);
                if(isFirst==1) {
                    data = obj.toString() + "=" + URLEncoder.encode(value.toString(), "UTF-8");
                }else{
                    data = data + "&" + obj.toString() + "=" + URLEncoder.encode(value.toString(), "UTF-8");
                }
                isFirst++;
            }
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            urlConnection.connect();
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());
               // System.out.println(result.toString());
                remap.put("data", result);
                remap.put("state", 1);//返回状态为1，连接服务器成功
            } else {
                remap.put("state",0);//返回状态为0，连接服务器失败
            }
        } catch (UnsupportedEncodingException e) {
            remap.put("state",0);
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            remap.put("state",0);
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            remap.put("state",0);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return remap;
    }




}