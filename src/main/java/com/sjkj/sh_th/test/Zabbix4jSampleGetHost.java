/*
package com.sjkj.sh_th.test;

import com.zabbix4j.ZabbixApi;
import com.zabbix4j.ZabbixApiException;
import com.zabbix4j.ZabbixApiParamter;
import com.zabbix4j.host.HostGetRequest;
import com.zabbix4j.host.HostGetResponse;
import com.zabbix4j.host.HostObject;
import com.zabbix4j.item.ItemObject;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * @Author: qh
 * @Date: 2018/11/20 17:20
 * @Description:
 *//*

public class Zabbix4jSampleGetHost extends ZabbixApiTestDummyMethodBase {

    public Zabbix4jSampleGetHost(ZabbixApi zabbixApi) {
        super(zabbixApi);
    }

    // 这里配置zabbix的url，帐号和密码
    public static final String ZBX_URL = "http://192.168.1.176/zabbix/api_jsonrpc.php";
    public static final String USERNAME = "Admin";
    public static final String PASSWORD = "zabbix";


    // 调用api的host get方法的封装
    public HostGetResponse getHost() throws ZabbixApiException {

        Integer targetHostId = 10105;
        HostGetRequest request = new HostGetRequest();
        HostGetRequest.Params params = request.getParams();

        ArrayList<Integer> hostIds = new ArrayList<Integer>();
        hostIds.add(targetHostId);
        // params.setHostids(hostIds);
        // 这里可以设指定的id值，也可以不设值。设值的话，取指定的内容，不设的话，获取全部的host
        params.setHostids(null);

        params.setSelectDiscoveryRule(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectGroups(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectItems(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectApplications(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectDiscoveries(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectGraphs(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectHostDiscovery(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectHttpTests(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectInterfaces(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectInventory(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectMacros(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectParentTemplates(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectScreens(ZabbixApiParamter.QUERY.extend.name());
        params.setSelectTriggers(ZabbixApiParamter.QUERY.extend.name());

        System.out.println("parames setting complete...");
        HostGetResponse response = zabbixApi.host().get(request);

        return response;
    }

    public static void main(String[] args) {

        try {
            // login to Zabbix
            System.out.println("beging...");
            ZabbixApi zabbixApi = new ZabbixApi(ZBX_URL);
            zabbixApi.login(USERNAME, PASSWORD);

            System.out.println("create new get host instance...");
            Zabbix4jSampleGetHost myGetHost = new Zabbix4jSampleGetHost(
                    zabbixApi);

            System.out.println("Get host beging...");
            HostGetResponse response = myGetHost.getHost();

            System.out.println("Get host end...let's print result");

            // 对response进行处理
            for (int i = 0; i < response.getResult().size(); i++) {

                //response 返回的信息非常大，可以赋值给HostObject，也可以其他对象
                HostObject myHostObject = response.getResult().get(i);
                //response 返回的信息非常大，可以赋值给ItemObject，也可以其他对象
                List<ItemObject> myItemObjectList = response.getResult().get(i)
                        .getItems();

                //打印host信息
                if (null == myHostObject)
                    System.out.println("Get host null, program will exit");
                else {
                    System.out.println("++++++Print Result " + i
                            + " HostObject+++++++++++");
                    System.out.println(myHostObject.getHost());
                    System.out.println(myHostObject.getName());
                    System.out.println(myHostObject.getAvailable());
                    System.out.println(myHostObject.getHostid());
                    System.out.println(myHostObject.getStatus());
                }

                //打印这个host下的item信息
                for (int j = 0; j < myItemObjectList.size(); j++) {
                    if (j == 0)
                        System.out.println("++++++Print ItemObject List "
                                + "+++++++++++");
                    ItemObject tmpItemObject = myItemObjectList.get(j);
                    System.out.println("Itemid:" + tmpItemObject.getItemid());
                    System.out.println("Templateid:"
                            + tmpItemObject.getTemplateid());
                    System.out.println("Hostid:" + tmpItemObject.getHostid());
                    System.out.println("Name:" + tmpItemObject.getName());
                    System.out.println("Key:" + tmpItemObject.getKey_());
                    System.out.println("Lastvalue:"
                            + tmpItemObject.getLastvalue());
                    System.out.println("Lastclock:"
                            + tmpItemObject.getLastclock());
                    System.out
                            .println("------------------------------------------");

                }

                System.out
                        .println("*****************%%%%%%%%%%%%%%************");

            }

        } catch (ZabbixApiException e) {
            e.printStackTrace();
        }
    }

}
*/
