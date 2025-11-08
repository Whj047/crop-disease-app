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
