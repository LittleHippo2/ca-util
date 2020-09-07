package com.csse.common.base;




import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 中软信息系统工程有限公司
 */

public class Ca8Auth {


    public static String authCa(String ca_token,
                                String ca_challenge, String client_id, String client_secret) throws Exception {

        System.out.printf("桌面认证的ca_token为："+ca_token);
        System.out.printf("桌面认证的ca_challenge为："+ca_challenge);
        System.out.printf("client_id为："+client_id);
        System.out.printf("client_secret为："+client_secret);


        //先获取微服务token  url
        String access_token_url = "http://192.168.42.11:8080/GXZC_JCPZZX/api/token/apply";

        Map<String, Object> param_map = new HashMap<>();
        param_map.put("client_id", client_id);
        param_map.put("client_secret", client_secret);

        //请求获取微服务token
        String token_result = "";
        try {
            token_result = HttpUtils.doPost(access_token_url, param_map, new HashMap<>(), null);
            System.out.println("请求获取微服务token结果为："+ token_result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("获取微服务token失败，请检查ip地址和端口，以及appid和密钥是否正确");
            return "authentication failure";
        }


        JSONObject jsonObject = JSONObject.parseObject(token_result);
        //获取微服务token
        String visit_token = jsonObject.getString("visit_token");
        System.out.println("15s微服务token为：" +visit_token);

        //后续端口和ip需要更换成对应环境的，吉大认证url
        String jd_url = "http://192.168.42.11:8080/GXZC_JCDDDL/api/oauth/login/key";

        //吉大请求参数
        Map<String, Object> jd_param_map = new HashMap<>();
        jd_param_map.put("client_id", ca_token);
        jd_param_map.put("client_secret", ca_challenge);

        //吉大请求头参数
        Map<String, String> jd_header_map = new HashMap<>();
        jd_header_map.put("visit_token", visit_token);

        //请求获取吉大认证token
        String jd_result ="";
        try {
            jd_result = HttpUtils.doPost(jd_url, jd_param_map, jd_header_map, null);
            System.out.println("请求获取15s单点登录token结果为："+ jd_result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("吉大认证失败，请查询ca_token和ca_challenge是否正确");
            return "authentication failure";
        }


        JSONObject jd_result_json = JSONObject.parseObject(jd_result);

        //有效时间戳
        Long timestamp =  jd_result_json.getLong("timestamp");

        JSONObject data_json = jd_result_json.getJSONObject("data");

        //单点登录token
        String token = data_json.getString("token");
        System.out.println("15s单点登录token为：" +token);


        //使用点点token获取登录用户信息
        //使用token验证登录用户信息地址
        String token_user_url = "http://192.168.42.11:8080/GXZC_JCDDDL/api/oauth/token/validate";


        Map<String, Object> user_info_param_map = new HashMap<>();
        jd_param_map.put("token", token);

        Map<String, String> user_info_header_map = new HashMap<>();
        jd_header_map.put("visit_token", visit_token);


        //请求获取用户信息
        String user_info_result = "";
        try {
            user_info_result = HttpUtils.doPost(token_user_url, user_info_param_map, user_info_header_map, null);
            System.out.println("请求获取用户信息结果为："+ user_info_result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("使用单点登录获取用户信息失败，请检查微服务token 和单点登录token是否有效");
            return "authentication failure";
        }

        JSONObject user_info_json = JSONObject.parseObject(user_info_result);
        JSONObject user_info_data_json = user_info_json.getJSONObject("data");
        //获取账号信息
        String emailId = user_info_data_json.getString("emailId");
        System.out.println("获取到的用户账号为："+emailId);

        return emailId;

//        String ca = "";
//        Map<String, Object> map = null;
//        try {
//            map = AnalysisXML.getXml(username);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//
//        long result = 1;
//        PlaCipherApi pca = new PlaCipherApi();
//        pca.J_Dev_Open(0);
//        try {
//             result = pca.J_Cip_DataVerifyEx(0, username.getBytes("UTF-8"), StringToByteArray(password));
//             System.out.printf("吉大认证结果:"+result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.printf("吉大认证异常："+e.getCause());
//            System.out.printf("吉大认证异常："+e.getStackTrace().toString());
//            System.out.printf("吉大认证异常："+e.getLocalizedMessage());
//            System.out.printf("吉大认证异常："+e.getMessage());
//
//            return "authentication failure";
//        }
//        if(result==0){
//            return map.get("身份证号").toString();
//        }else{
//            return "authentication failure";
//        }


    }

    public static void main(String[] args) throws Exception {

//        String xml = "<WangYu><SecAssert ID=\"1590569387084@LC本级\" ><Info infoSourceID=\"\" timestamp=\"20200527 164947\" validityFrom=\"20200526 164947\" validityTo=\"20200528 164947\"><UserInfo userAuthID=\"521125168701011254@RNS\" userAuthType=\"2\" userIP=\"32.1.6.60\" ><UserID>521125168701011254@RNS</UserID><Property name=\"平台用户ID\" value=\"123456789\" /><Property name=\"姓名\" value=\"测试\" /><Property name=\"单位\" value=\"本级.\" unitId=\"000001\" /><Property name=\"民族\" value=\"高山族\" valueId=\"18\" /><Property name=\"保障卡号\" value=\"521125168701011254\" /><Property name=\"职务\" value=\"团长\" valueId=\"4\" /><Property name=\"登录名\" value=\"521125168701011254@RNS\" /><Property name=\"身份证号\" value=\"07122815\" /><Property name=\"注册时间\" value=\"2020-05-21\" /><Property name=\"要素\" value=\"第一大厅\" valueId=\"1\" /><Property name=\"性别\" value=\"男\" valueId=\"01\" /></UserInfo><SecContext sourceSecValue=\"3\"  /></Info><Signature type=\"1\" signerID=\"CN=005, O=二级, C=cn\" algorithm=\"SHA1\">be9d63d403b5537fa67ec9972af9d1a9f02c8c47c41ff552df56920e0b12b69d3cfd23009c433229df84477e5b27051ac6a909e1887dec2606309ba0a626a56b517e349b8aec090030820248308201eca003020102021027ab1f21376187d6f777a8556c9fcfac300b06072a56510528010c05003049310b300906035504061302636e310d300b060355040a1e044e8c7ea7312b302906035504031e224e2d56fd4eba6c1189e3653e519b6839004300414e8c7ea7005f0031003100300032301e170d3230303530393037333834345a170d3236303831313037333834345a302a310b300906035504061302636e310d300b060355040a1e044e8c7ea7310c300a060355040313033030353058300b06072a56510528010205000349004cab58e9c176514938dce5206ef3547c3952a49aa7e56df0a7ed52b30df9021c22715b01cb41d9322c7c9c91f021a8dfa9cb4f3cd72645b1f4f6192c17e55745a061b85961d5da00a381d43081d1301f0603551d230418301680147f9bf5f5b114ca04067fa8cf24d772ee8861c2a2301d0603551d0e0416041426fc26a97fec9ec50ca8a60293fa8faea27ecf89300b0603551d0f040403020780300c0603551d130405300301010030660603551d1f045f305d305ba059a057a4553053310b300906035504061302636e310d300b060355040a1e044e8c7ea7310a3008060355040b130132310c300a060355040b1303313031310c300a060355040b130363726c310d300b0603550403130463726c30300c06052a565107050403020102300b06072a56510528010c050003490057e2cc67e80dab2c46c4825c27eedb2b7b81c9db7eed17f2c01c764f43b6307a7560a00164d5180567336eb6792857eeb40c14c09ba32020fb8249c4e2f7f3cabbb715484f1a5800</Signature></SecAssert><RandomNum>A8FC8496</RandomNum></WangYu>\n";
//        /*long result = 1;
//        PlaCipherApi pca = new PlaCipherApi();
//        pca.J_Dev_Open(0);
//        byte[] testBy = pca.J_Cip_DataSignEx(0,"TEST".getBytes());
//        System.out.printf(testBy.toString());
//
//        try {
//            result = pca.J_Cip_DataVerifyEx(0, username.getBytes(), password.getBytes());
//        } catch (Exception e) {
//
//        }*/
//        System.out.println(authCa(xml, ""));

        String json = "{\n" +
                "    \"code\": \"0\",\n" +
                "    \"message\": \"单点登录，登录成功！\",\n" +
                "    \"data\": {\n" +
                "        \"token\": \"746d727a-8e27-480b-b1a0-7c01d9526e2f\"\n" +
                "    },\n" +
                "    \"timestamp\": \"1585720445182\"\n" +
                "}";
        JSONObject jsonObject =JSONObject.parseObject(json);
        JSONObject object = jsonObject.getJSONObject("data");
        String token = object.getString("token");
        System.out.println(token);
    }
//    public static  byte[] StringToByteArray(String buffer){
//        byte[] bufferByte = new byte[buffer.length()/2];
//        try{
//            List<Integer> bufferls = new ArrayList<>();
//            Integer j=0;
//            for (int i=2;i<buffer.length()+2;i=i+2){
//                bufferls.add(Integer.decode("0x"+buffer.substring(j,i)));
//                j = i;
//            }
//            for (int i=0;i<bufferls.size();i++){
//                bufferByte[i] = bufferls.get(i).byteValue();
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        return bufferByte;
//    }
}
