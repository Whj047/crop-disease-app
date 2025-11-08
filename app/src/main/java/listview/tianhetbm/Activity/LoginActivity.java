\
package listview.tianhetbm.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import listview.tianhetbm.Globalconstants.GlobalConstants;
import listview.tianhetbm.domain.userInfo;
import listview.tianhetbm.utils.MD5Encoder;
import listview.tianhetbm.utils.PrefUtils;
import listview.tianhetbm.utils.ToastUtils;
import listview.tianhetbm.utils.WebServiceRequester;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout logining;
    private EditText edname;
    private EditText edpassword;
    private Button loginBtn;
    private TextView tv_xieyi;
    private TextView tv_yinsi;
    private CheckBox checkBox;

    private boolean agreed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(listview.tianhetbm.R.layout.activity_login);

        logining = findViewById(listview.tianhetbm.R.id.logining_view);
        edname = findViewById(listview.tianhetbm.R.id.ed_name);
        edpassword = findViewById(listview.tianhetbm.R.id.ed_password);
        loginBtn = findViewById(listview.tianhetbm.R.id.but_login);
        checkBox = findViewById(listview.tianhetbm.R.id.checkBox);
        tv_xieyi = findViewById(listview.tianhetbm.R.id.tv_xieyi);
        tv_yinsi = findViewById(listview.tianhetbm.R.id.tv_yinsi);

        tv_xieyi.setOnClickListener(v -> startActivity(new Intent(this, WebViewActivity.class)));
        tv_yinsi.setOnClickListener(v -> startActivity(new Intent(this, WebViewActivity.class)));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> agreed = isChecked);

        loginBtn.setOnClickListener(v -> {
            if (!agreed) {
                ToastUtils.showToast(getApplicationContext(), "请先阅读并同意用户协议与隐私政策");
                return;
            }
            if (!isNetworkAvailable()) {
                ToastUtils.showToast(getApplicationContext(), "网络不可用");
                return;
            }
            String name = edname.getText().toString().trim();
            String ps = edpassword.getText().toString().trim();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ps)) {
                ToastUtils.showToast(getApplicationContext(), "账号或密码不能为空");
                return;
            }
            if (!isStrongPassword(ps)) {
                showWeakPwdDialog();
                return;
            }
            new LoginTask(name, ps).execute();
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    // Password must include number, lowercase, uppercase, and special char; length >= 8.
    private boolean isStrongPassword(String ps) {
        if (ps.length() < 8) return false;
        boolean hasDigit=false, hasLower=false, hasUpper=false, hasOther=false;
        for (char ch : ps.toCharArray()) {
            if (Character.isDigit(ch)) hasDigit = true;
            else if (Character.isLowerCase(ch)) hasLower = true;
            else if (Character.isUpperCase(ch)) hasUpper = true;
            else hasOther = true;
        }
        return hasDigit && hasLower && hasUpper && hasOther;
    }

    private void showWeakPwdDialog() {
        View dialogView = LayoutInflater.from(this).inflate(listview.tianhetbm.R.layout.login_dialogdis, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        dialogView.findViewById(listview.tianhetbm.R.id.tv_n).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(listview.tianhetbm.R.id.tv_y).setOnClickListener(v -> {
            edpassword.setText("");
            dialog.dismiss();
        });
        dialog.show();
    }

    class LoginTask extends AsyncTask<Void, Void, JSONObject> {
        private final String name;
        private final String ps;

        LoginTask(String name, String psPlain) {
            this.name = name;
            this.ps = MD5Encoder.encode(psPlain);
        }

        @Override
        protected void onPreExecute() {
            logining.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("userName", name);
                map.put("userPassword", ps);
                JSONObject jo = new JSONObject(map);
                return WebServiceRequester.callWebService(GlobalConstants.URL, "login", jo.toString(), "");
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            logining.setVisibility(View.GONE);
            if (result == null) {
                ToastUtils.showToast(getApplicationContext(), "登录失败，请稍后再试");
                return;
            }
            boolean ok = result.optBoolean("state", false);
            String msg = result.optString("message", "");
            if (!ok) {
                ToastUtils.showToast(getApplicationContext(), TextUtils.isEmpty(msg) ? "登录失败" : msg);
                return;
            }
            // Save simple info
            PrefUtils.setString(getApplicationContext(), "name", name);
            PrefUtils.setString(getApplicationContext(), "ps", ps);
            ToastUtils.showToast(getApplicationContext(), "登录成功");
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }
}
