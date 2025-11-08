\
package listview.tianhetbm.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class WebServiceRequester {
    public static JSONObject callWebService(String url, String action, String payload, String fallback) {
        // Mock response for demo purpose
        try {
            JSONObject userinfo = new JSONObject();
            userinfo.put("userName", "demo");
            userinfo.put("empNumber", "0001");
            userinfo.put("empName", "演示账户");
            userinfo.put("empSex", "M");
            userinfo.put("userRegEmail", "demo@example.com");
            userinfo.put("userRegDateTime", "2025-01-01");
            userinfo.put("userValidStatus", "true");
            userinfo.put("userExpDate", "2099-12-31");
            userinfo.put("HaveZS", "Y");

            JSONObject ret = new JSONObject();
            ret.put("state", true);
            ret.put("message", "登录成功");
            ret.put("userinfo", userinfo);
            return ret;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
}
