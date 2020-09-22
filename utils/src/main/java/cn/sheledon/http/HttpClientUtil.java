package cn.sheledon.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author sheledon
 */
public class HttpClientUtil {

    /**
     * URI路径找不到！！！！ ，解决
     * @param host
     * @param path
     * @param parameters
     * @throws IOException
     */
    public static JSONObject doGet(String host, String path, Map<String,String> parameters) throws IOException {
        HttpClient httpClient= HttpClients.createDefault();
        String uri="http://"+host+"/"+path+createPar(parameters);
        HttpGet httpGet=new HttpGet(uri);
        HttpResponse response=httpClient.execute(httpGet);
        JSONObject jsonObject=parseResponse(response);
        return jsonObject;
    }
    private static String createPar(Map<String,String> maps){
        StringBuilder builder=new StringBuilder("?");
        for (Map.Entry<String,String> entry:maps.entrySet()){
             builder.append(entry.getKey()+"="+entry.getValue());
        }
        return builder.toString();
    }
    private static JSONObject parseResponse(HttpResponse response) throws IOException {
        HttpEntity entity=response.getEntity();
        String content= EntityUtils.toString(entity,"UTF-8");
        return JSONObject.parseObject(content);
    }
}
