\
package listview.tianhetbm.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.DecimalFormat;

import listview.tianhetbm.R;
import listview.tianhetbm.utils.ImageUtils;
import listview.tianhetbm.utils.TFLiteClassifier;
import listview.tianhetbm.utils.ToastUtils;

public class RecognizeActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvResult;
    private Uri imageUri;
    private TFLiteClassifier classifier;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imageView.setImageURI(uri);
                }
            });

    private final ActivityResultLauncher<Intent> takePhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap bmp = (Bitmap) result.getData().getExtras().get("data");
                    if (bmp != null) {
                        imageView.setImageBitmap(bmp);
                        // Save to cache to obtain a Uri if needed
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "capture", null);
                        if (!TextUtils.isEmpty(path)) imageUri = Uri.parse(path);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        imageView = findViewById(R.id.imageView);
        tvResult = findViewById(R.id.tv_result);
        Button btnPick = findViewById(R.id.btn_pick);
        Button btnCamera = findViewById(R.id.btn_camera);
        Button btnRecognize = findViewById(R.id.btn_recognize);

        classifier = new TFLiteClassifier(getApplicationContext());

        btnPick.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1001);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhotoLauncher.launch(intent);
            }
        });

        btnRecognize.setOnClickListener(v -> {
            if (imageView.getDrawable() == null && imageUri == null) {
                ToastUtils.showToast(getApplicationContext(), "请先选择或拍摄一张叶片图片");
                return;
            }
            try {
                Bitmap bmp;
                if (imageUri != null) {
                    bmp = ImageUtils.loadBitmapFromUri(this, imageUri, 512, 512);
                } else {
                    imageView.setDrawingCacheEnabled(true);
                    bmp = Bitmap.createBitmap(imageView.getDrawingCache());
                    imageView.setDrawingCacheEnabled(false);
                }
                TFLiteClassifier.Result r = classifier.classify(bmp);
                DecimalFormat df = new DecimalFormat("0.0%");
                StringBuilder sb = new StringBuilder();
                sb.append("判定：").append(r.label).append("（置信度 ").append(df.format(r.confidence)).append("）\n");
                if (r.topLabels != null) {
                    sb.append("Top 结果：\n");
                    for (int i = 0; i < r.topLabels.length; i++) {
                        sb.append(" - ").append(r.topLabels[i]).append("：").append(df.format(r.topScores[i])).append("\n");
                    }
                }
                tvResult.setText(sb.toString());
            } catch (IOException e) {
                ToastUtils.showToast(getApplicationContext(), "图片读取失败");
            }
        });
    }
}
