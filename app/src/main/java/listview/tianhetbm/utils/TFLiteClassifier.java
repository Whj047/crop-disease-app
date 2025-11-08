\
package listview.tianhetbm.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class TFLiteClassifier {

    private Interpreter interpreter;
    private String[] labels;
    private boolean fallback = false;
    private int inputSize = 224;

    public TFLiteClassifier(Context ctx) {
        try {
            MappedByteBuffer modelBuffer = loadModelFile(ctx, "model.tflite");
            interpreter = new Interpreter(modelBuffer);
            labels = loadLabels(ctx);
        } catch (Throwable e) {
            // If model not present or init fails, switch to heuristic fallback
            fallback = true;
            labels = new String[]{"健康", "叶斑病", "白粉病", "锈病"};
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private String[] loadLabels(Context ctx) throws IOException {
        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(ctx.getAssets().open("labels.txt")))) {
            java.util.List<String> list = new java.util.ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) list.add(line.trim());
            }
            return list.toArray(new String[0]);
        }
    }

    public static class Result {
        public String label;
        public float confidence;
        public String[] topLabels;
        public float[] topScores;
    }

    public Result classify(Bitmap src) {
        Bitmap inputBmp = ImageUtils.resizeCenterCrop(src, inputSize);
        if (fallback || interpreter == null) {
            return heuristic(inputBmp);
        }

        // Assuming model expects [1, 224, 224, 3] float32 0..1
        float[][][][] input = new float[1][inputSize][inputSize][3];
        for (int y = 0; y < inputSize; y++) {
            for (int x = 0; x < inputSize; x++) {
                int px = inputBmp.getPixel(x, y);
                input[0][y][x][0] = ((px >> 16) & 0xFF) / 255f;
                input[0][y][x][1] = ((px >> 8) & 0xFF) / 255f;
                input[0][y][x][2] = (px & 0xFF) / 255f;
            }
        }

        float[][] output = new float[1][labels.length];
        interpreter.run(input, output);
        int best = argmax(output[0]);
        Result r = new Result();
        r.label = labels[best];
        r.confidence = softmaxMax(output[0]);
        // top3
        int[] idxs = topK(output[0], Math.min(3, labels.length));
        r.topLabels = new String[idxs.length];
        r.topScores = new float[idxs.length];
        for (int i = 0; i < idxs.length; i++) {
            r.topLabels[i] = labels[idxs[i]];
            r.topScores[i] = (float) (1.0 / (1.0 + Math.exp(-output[0][idxs[i]]))); // sigmoid-ish
        }
        return r;
    }

    private Result heuristic(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int total = w * h;
        int greenish = 0, whitepowder = 0, rusty = 0, brownspots = 0;

        for (int y = 0; y < h; y+=2) {        // sample every 2px for speed
            for (int x = 0; x < w; x+=2) {
                int c = bmp.getPixel(x, y);
                int r = (c >> 16) & 0xFF;
                int g = (c >> 8) & 0xFF;
                int b = c & 0xFF;

                if (g > r + 20 && g > b + 20) greenish++;

                int max = Math.max(r, Math.max(g, b));
                int min = Math.min(r, Math.min(g, b));
                int diff = max - min;
                if (r > 180 && g > 180 && b > 180 && diff < 30) whitepowder++;   // near-white

                if (r > 140 && g < 120 && b < 120) rusty++; // reddish/brown

                if (r > 120 && g > 120 && b < 80) brownspots++; // yellow/brown to dark
            }
        }

        float fg = greenish / (total / 4f);
        float fw = whitepowder / (total / 4f);
        float fr = rusty / (total / 4f);
        float fb = brownspots / (total / 4f);

        String label;
        float score;
        if (fw > 0.05f) {
            label = "白粉病"; score = Math.min(0.9f, fw * 3f);
        } else if (fr > 0.05f) {
            label = "锈病"; score = Math.min(0.85f, fr * 2.5f);
        } else if (fb > 0.06f) {
            label = "叶斑病"; score = Math.min(0.8f, fb * 2.0f);
        } else {
            label = "健康"; score = Math.max(0.5f, fg);
        }

        Result r = new Result();
        r.label = label;
        r.confidence = score;
        r.topLabels = new String[]{"健康", "叶斑病", "白粉病"};
        r.topScores = new float[]{fg, fb, fw};
        return r;
    }

    private static int argmax(float[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) if (arr[i] > arr[idx]) idx = i;
        return idx;
    }

    private static float softmaxMax(float[] arr) {
        float max = arr[0];
        for (int i = 1; i < arr.length; i++) max = Math.max(max, arr[i]);
        return (float) (1.0 / (1.0 + Math.exp(-max)));
    }

    private static int[] topK(float[] arr, int k) {
        int n = arr.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        java.util.Arrays.sort(idx, (a, b) -> Float.compare(arr[b], arr[a]));
        int[] out = new int[k];
        for (int i = 0; i < k; i++) out[i] = idx[i];
        return out;
    }
}
