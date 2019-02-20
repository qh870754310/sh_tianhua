package com.sjkj.sh_th.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;

/**
 * @Author: qh
 * @Date: 2018/11/20 17:06
 * @Description:
 */
public class ZabbixApiTestBase {

    protected static String user = "Admin";//zabbix登录账号
    protected static String password = "zabbix";//zabbix登录密码

    protected ZabbixApi zabbixApi;

    public ZabbixApiTestBase() {
        login(user, password);
    }

    protected void login(String user, String password) {
        /*try {
            zabbixApi = new ZabbixApi("http://192.168.1.176/zabbix/api_jsonrpc.php");
            zabbixApi.login(user, password);
        } catch (ZabbixApiException e) {
          e.printStackTrace();
        }*/
    }




    protected Gson getGson() {

        return new GsonBuilder().setPrettyPrinting().create();
    }

}
