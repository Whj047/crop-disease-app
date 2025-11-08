\
package listview.tianhetbm.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);

        TextView tv = new TextView(this);
        tv.setText("欢迎使用：农作物病虫害识别（离线版）");
        tv.setTextSize(18f);
        root.addView(tv);

        Button btn = new Button(this);
        btn.setText("打开离线识别");
        btn.setOnClickListener(v -> startActivity(new Intent(this, RecognizeActivity.class)));
        root.addView(btn);

        setContentView(root);
    }
}
