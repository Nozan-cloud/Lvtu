package com.Lvtu.handler;

import com.Lvtu.properties.AliyunSmsProperties;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
@Slf4j
public class AliyunSmsHandler {

    private IAcsClient client;
    //短信发送成功状态码
    private static final String OK = "OK";
    //-------------------------阿里云短信发送相关参数设置，保持不变即可------------------
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String VERSION = "2017-05-25";
    private static final String ACTION = "SendSms";
    private AliyunSmsProperties aliyunSmsProperties;
    /**
     * @return : 构造方法，初始化构造短信发送对象-IAcsClient
     */
    public AliyunSmsHandler(AliyunSmsProperties aliyunSmsProperties) {
        this.aliyunSmsProperties = aliyunSmsProperties;
        DefaultProfile profile = DefaultProfile.getProfile(
                aliyunSmsProperties.getRegionId(),
                aliyunSmsProperties.getAccessKeyId(),
                aliyunSmsProperties.getAccessKeySecret()
        );
        this.client = new DefaultAcsClient(profile);
    }

    /**
     * 阿里云短信发送接口，支持通知、验证码、激活码类短信
     * map中所需参数，参数名称固定，如下(注意：参数首字母都是小写的)：
     *      phoneNumbers ： 手机号码，支持多个中间以“,”英文逗号分隔 （必填）
     *      signName ：签名（必填）
     *      templateCode ：模板code（必填）
     *      templateParam ：模板参数（不必填，如果是通知类短信，该参数可以不设置）
     * @return : java.lang.Boolean
     * true-短信发送成功；false-短信发送失败
     */
    public Boolean sendSms(Map<String, String> map) {
        Boolean flag = false;
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        //发送短信域名，不要改变
        request.setSysDomain(DOMAIN);
        request.setSysVersion(VERSION);
        request.setSysAction(ACTION);
        request.putQueryParameter("TemplateParam", map.get("templateParam"));
        request.putQueryParameter("PhoneNumbers", map.get("phoneNumbers"));
        request.putQueryParameter("SignName",aliyunSmsProperties.getSignName());
        request.putQueryParameter("TemplateCode", aliyunSmsProperties.getTemplateCode());
        //通知类短信，模板不需要参数。判断传递的模板参数是否为空，如果为空可能是通知类短信，则不需要添加模板参数条件
        if (map.get("templateParam") != null) {
            request.putQueryParameter("TemplateParam", map.get("templateParam"));
        }
        try {
            //发送短信
            CommonResponse response = client.getCommonResponse(request);
            //发送短信结果转为Map类型
            Map<String, String> responseMap = JSON.parseObject(response.getData(), Map.class);
            //判断短信发送是否成功
            if (OK.equals(responseMap.get("Code"))) {
                flag = true;
            }
            log.debug("send fail[code={}, message={}]", responseMap.get("Code"), responseMap.get("Message"));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return flag;
    }
}