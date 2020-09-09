package com.csse.common.base;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author 中软信息系统工程有限公司
 */
public class Ca8Auth {

    private static Logger logger = Logger.getLogger(Ca8Auth.class);

    public static String authCa(String ca_token,
                                String ca_challenge, String client_id, String client_secret) throws IOException {


        Resource resource = new ClassPathResource("/application.properties");
        Properties props = PropertiesLoaderUtils.loadProperties(resource);

        String ip_prot = props.getProperty("ip.prot");
        String get_visit_token_url = props.getProperty("get.visit.token.url");
        String jd_key_url = props.getProperty("jd.key.url");
        String get_token_user = props.getProperty("get.token.user");

        System.out.println("桌面认证的ca_token为：" + ca_token);
        System.out.println("桌面认证的ca_challenge为：" + ca_challenge);
        System.out.println("client_id为：" + client_id);
        System.out.println("client_secret为：" + client_secret);

        //先获取微服务token  url
        //String access_token_url = "http://192.168.42.11:8080/GXZC_JCPZZX/api/token/apply";
        String access_token_url = ip_prot + get_visit_token_url;
        System.out.println("获取微服务token地址为：" + access_token_url);


        Map<String, Object> param_map = new HashMap<>();
        param_map.put("client_id", client_id);
        param_map.put("client_secret", client_secret);
        System.out.println("请求获取微服务token的参数为：client_id="+client_id+",client_secret="+client_secret);

        //请求获取微服务token
        String token_result = "";
        try {
            token_result = HttpUtils.doPost(access_token_url, param_map, new HashMap<>(), null);
            System.out.println("请求获取微服务token结果为：" + token_result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取微服务token失败，请检查ip地址和端口，以及appid和密钥是否正确");
            return "authentication failure";
        }

        JSONObject jsonObject = JSONObject.parseObject(token_result);
        //获取微服务token
        String visit_token = jsonObject.getString("visit_token");
        System.out.println("15所微服务token为：" + visit_token);

        //后续端口和ip需要更换成对应环境的，吉大认证url
        //String jd_url = "http://192.168.42.11:8080/GXZC_JCDDDL/api/oauth/login/key";
        String jd_url = ip_prot + jd_key_url;
        System.out.println("吉大认证地址为：" + jd_url);

        //吉大请求参数
        Map<String, Object> jd_param_map = new HashMap<>();
        jd_param_map.put("client_id", ca_token);
        jd_param_map.put("client_secret", ca_challenge);
        System.out.println("请求吉大认证参数为：client_id="+ca_token+",client_secret="+ca_challenge);


        //吉大请求头参数
        Map<String, String> jd_header_map = new HashMap<>();
        jd_header_map.put("visit_token", visit_token);
        System.out.println("请求吉大认证请求头为：visit_token="+visit_token);

        //请求获取吉大认证token
        String jd_result = "";
        try {
            jd_result = HttpUtils.doPost(jd_url, jd_param_map, jd_header_map, null);
            System.out.println("请求获取吉大认证登录token结果为：" + jd_result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("吉大认证失败，请查询ca_token和ca_challenge是否正确");
            return "authentication failure";
        }

        JSONObject jd_result_json = JSONObject.parseObject(jd_result);

        //有效时间戳
        Long timestamp = jd_result_json.getLong("timestamp");

        JSONObject data_json = jd_result_json.getJSONObject("data");

        //单点登录token
        String token = data_json.getString("token");
        System.out.println("15所单点登录token为：" + token);

        //使用点点token获取登录用户信息
        //使用token验证登录用户信息地址
        // String token_user_url = "http://192.168.42.11:8080/GXZC_JCDDDL/api/oauth/token/validate";
        String token_user_url = ip_prot + get_token_user;
        System.out.println("根据token获取用户信息的地址为：" + token_user_url);


        Map<String, Object> user_info_param_map = new HashMap<>();
        jd_param_map.put("token", token);
        System.out.println("请求根据token获取人员信息接口的参数为：token="+token);

        Map<String, String> user_info_header_map = new HashMap<>();
        jd_header_map.put("visit_token", visit_token);
        System.out.println("请求根据token获取人员信息接口的请求头为：visit_token="+visit_token);

        //请求获取用户信息
        String user_info_result = "";
        try {
            user_info_result = HttpUtils.doPost(token_user_url, user_info_param_map, user_info_header_map, null);
            System.out.println("请求获取用户信息结果为：" + user_info_result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("使用单点登录获取用户信息失败，请检查微服务token 和单点登录token是否有效");
            return "authentication failure";
        }

        JSONObject user_info_json = JSONObject.parseObject(user_info_result);
        JSONObject user_info_data_json = user_info_json.getJSONObject("data");
        //获取账号信息
        String emailId = user_info_data_json.getString("emailId");
        System.out.println("获取到的用户账号为：" + emailId);

        return emailId;
    }

    public static void main(String[] args) throws IOException {

//        String json = "{\n" +
//                "    \"code\": \"0\",\n" +
//                "    \"message\": \"单点登录，登录成功！\",\n" +
//                "    \"data\": {\n" +
//                "        \"token\": \"746d727a-8e27-480b-b1a0-7c01d9526e2f\"\n" +
//                "    },\n" +
//                "    \"timestamp\": \"1585720445182\"\n" +
//                "}";
//        JSONObject jsonObject =JSONObject.parseObject(json);
//        JSONObject object = jsonObject.getJSONObject("data");
//        String token = object.getString("token");
//        System.out.println(token);

        authCa("1", "2", "3", "4");
    }
}
