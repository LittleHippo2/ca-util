package com.csse.common.base;

import org.dom4j.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AnalysisXML {

    /**
     * 解析xml
     *
     * @param xml
     * @return
     */
    public static Map<String, Object> getXml(String xml) throws DocumentException {

        Map<String, Object> map = new HashMap<>();


        // 将字符串转为XML
        Document document = DocumentHelper.parseText(xml);
        // 获取根节点
        Element rootElt = document.getRootElement();

        //获取SecAssert节点
        Iterator SecAssert = rootElt.elementIterator("SecAssert");
        Element recordEle = (Element) SecAssert.next();

        //获取info节点
        Iterator Info = recordEle.elementIterator("Info");
        Element InfoEle = (Element) Info.next();

        //获取userinfo节点
        Iterator UserInfo = InfoEle.elementIterator("UserInfo");
        Element abkRecord = (Element) UserInfo.next();

        //获取userinfo下所有的节点
        Iterator f = abkRecord.elementIterator();

        while (f.hasNext()) {
            Element recordEless = (Element) f.next();
            if ("平台用户ID".equals(recordEless.attributeValue("name"))) {
                String userid = recordEless.attributeValue("value");
                map.put("平台用户ID", userid);
            }
            if ("身份证号".equals(recordEless.attributeValue("name"))) {
                String idCard = recordEless.attributeValue("value");
                map.put("身份证号", idCard);
            }
        }
        return map;
    }

    //
//    public static void main(String[] args) throws DocumentException {
//
//        String xml = "<WangYu><SecAssert ID=\"1590569387084@LC本级\" ><Info infoSourceID=\"\" timestamp=\"20200527 164947\" validityFrom=\"20200526 164947\" validityTo=\"20200528 164947\"><UserInfo userAuthID=\"521125168701011254@RNS\" userAuthType=\"2\" userIP=\"32.1.6.60\" ><UserID>521125168701011254@RNS</UserID><Property name=\"平台用户ID\" value=\"123456789\" /><Property name=\"姓名\" value=\"测试\" /><Property name=\"单位\" value=\"本级.\" unitId=\"000001\" /><Property name=\"民族\" value=\"高山族\" valueId=\"18\" /><Property name=\"保障卡号\" value=\"521125168701011254\" /><Property name=\"职务\" value=\"团长\" valueId=\"4\" /><Property name=\"登录名\" value=\"521125168701011254@RNS\" /><Property name=\"身份证号\" value=\"07122815\" /><Property name=\"注册时间\" value=\"2020-05-21\" /><Property name=\"要素\" value=\"第一大厅\" valueId=\"1\" /><Property name=\"性别\" value=\"男\" valueId=\"01\" /></UserInfo><SecContext sourceSecValue=\"3\"  /></Info><Signature type=\"1\" signerID=\"CN=005, O=二级, C=cn\" algorithm=\"SHA1\">be9d63d403b5537fa67ec9972af9d1a9f02c8c47c41ff552df56920e0b12b69d3cfd23009c433229df84477e5b27051ac6a909e1887dec2606309ba0a626a56b517e349b8aec090030820248308201eca003020102021027ab1f21376187d6f777a8556c9fcfac300b06072a56510528010c05003049310b300906035504061302636e310d300b060355040a1e044e8c7ea7312b302906035504031e224e2d56fd4eba6c1189e3653e519b6839004300414e8c7ea7005f0031003100300032301e170d3230303530393037333834345a170d3236303831313037333834345a302a310b300906035504061302636e310d300b060355040a1e044e8c7ea7310c300a060355040313033030353058300b06072a56510528010205000349004cab58e9c176514938dce5206ef3547c3952a49aa7e56df0a7ed52b30df9021c22715b01cb41d9322c7c9c91f021a8dfa9cb4f3cd72645b1f4f6192c17e55745a061b85961d5da00a381d43081d1301f0603551d230418301680147f9bf5f5b114ca04067fa8cf24d772ee8861c2a2301d0603551d0e0416041426fc26a97fec9ec50ca8a60293fa8faea27ecf89300b0603551d0f040403020780300c0603551d130405300301010030660603551d1f045f305d305ba059a057a4553053310b300906035504061302636e310d300b060355040a1e044e8c7ea7310a3008060355040b130132310c300a060355040b1303313031310c300a060355040b130363726c310d300b0603550403130463726c30300c06052a565107050403020102300b06072a56510528010c050003490057e2cc67e80dab2c46c4825c27eedb2b7b81c9db7eed17f2c01c764f43b6307a7560a00164d5180567336eb6792857eeb40c14c09ba32020fb8249c4e2f7f3cabbb715484f1a5800</Signature></SecAssert><RandomNum>A8FC8496</RandomNum></WangYu>\n";
//
//
//        Map map = getXml(xml);
//
//        System.out.println(map);
//    }


}
