[README.md](https://github.com/user-attachments/files/23421514/README.md)
# 农作物病虫害识别（示例工程）

这是根据你提供的 Word 代码说明自动生成的 **Android Studio 可编译项目**。
已包含：
- 登录界面与弱密码提醒
- 本地模拟后端（无需真实服务器即可体验登录流程）
- 偏好存储工具类、MD5 编码、简单的 WebView 条款页面
- 依赖：AppCompat、Material、ConstraintLayout、Gson、HelloCharts（可用于后续图表展示）

## 打开与运行
1. 用 **Android Studio** 打开根目录 `CropDiseaseRecognitionApp`。
2. 首次打开会自动 Sync（若提示需要 Gradle Wrapper，可使用 Android Studio 自带 Gradle 或创建 wrapper）。
3. 连接模拟器/真机，点击运行即可。
4. 默认任意账号 + 满足强度的密码即可“登录”（使用本地 Mock）。

> 注意：这是一个**可跑通的壳工程**。后续你可以把真实的后端接口地址填入 `Globalconstants/GlobalConstants.java`，
并在 `utils/WebServiceRequester.java` 中实现实际的网络请求。


## 离线识别版（本地推理）
- 已集成 `TFLiteClassifier`，默认若 `assets/model.tflite` 不存在，会启用**颜色特征启发式**离线识别（无需网络）。
- 如果你已有训练好的模型（输入 224x224x3，输出与 `assets/labels.txt` 一致）：
  1. 将你的模型文件命名为 **model.tflite**，放入 `app/src/main/assets/`；
  2. 确保 `assets/labels.txt` 与模型输出顺序一致；
  3. 重新运行即可在设备端离线推理。
- 需要我帮你把数据集训练成 `.tflite` 模型吗？可以继续发我数据格式与类别清单。

使用：登录后进入主页，点击“打开离线识别”，然后拍照或选择叶片图片，再点击“识别”。


## 不装 Android Studio，在线自动打 APK（适合新手）
你可以用 **GitHub Actions** 在云端自动编译 APK，然后直接网页里下载：

1. 打开 https://github.com 并登录（没有账号先注册，一个邮箱就行）。
2. 右上角 **+ → New repository**，仓库名可填 `crop-disease-app`，勾选 **Public**（或 Private 也行），点 **Create repository**。
3. 在新仓库页面，点击 **"uploading an existing file"**，把整个 `CropDiseaseRecognitionApp` 文件夹 **压缩成 zip** 后上传，或把文件夹内容全选拖进去（保持目录结构）。
4. 上传完成后点 **Commit changes**。
5. 进入仓库的 **Actions** 标签页，第一次会提示 “Workflows aren’t being run” → 点击 **I understand… Enable workflows**。
6. 左侧选择 **Android CI (Build APK)**，点右侧的 **Run workflow**（绿色按钮）。
7. 等待 3–10 分钟，跑完后点击该工作流的 **Artifacts**，下载 **app-debug**，里面就是 **app-debug.apk**。
8. 把 `app-debug.apk` 传到手机，打开安装即可。

> 这个仓库里我已经帮你准备好了 `.github/workflows/android.yml`，会自动：安装 JDK → 安装 Android SDK → 用 Gradle 编译 → 上传 APK。
