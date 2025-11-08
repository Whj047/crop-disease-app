\
package listview.tianhetbm.Activity;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wv = new WebView(this);
        setContentView(wv);
        String content = loadAssetText("yszc.txt");
        wv.loadDataWithBaseURL(null, "<pre style='font-size:16px'>" + content + "</pre>",
                "text/html", "utf-8", null);
    }

    private String loadAssetText(String name) {
        try {
            InputStream is = getAssets().open(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            return "未找到内容";
        }
    }
}
