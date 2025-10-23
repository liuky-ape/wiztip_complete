# 🎤 唤醒词功能使用指南

## 功能介绍

Wiztip语音助手支持通过唤醒词"**你好小笔**"来触发录音功能，无需手动点击按钮。

## 快速开始

### 1. 启动应用

```bash
cd /Users/bytedance/Documents/wiztip_complete
mvn spring-boot:run
```

### 2. 打开Web界面

在Chrome浏览器中访问：
```
http://localhost:8080
```

### 3. 开始使用

1. 点击"**启动唤醒词检测**"按钮
2. 允许浏览器使用麦克风权限
3. 对着麦克风说："**你好小笔**"
4. 系统自动开始录音（10秒）
5. 录音完成后自动上传到服务器处理

## 功能特点

### 🎯 语音唤醒
- 持续监听唤醒词"你好小笔"
- 支持多种发音变体（你好小笔、你好 小笔、小笔等）
- 检测到唤醒词后立即开始录音

### 🎙️ 智能录音
- **自动静音检测** - 5秒无声音自动停止录音
- **音量可视化** - 实时显示录音音量
- **倒计时提示** - 显示剩余静音时间
- 支持WAV格式
- 实时显示录音状态

### 📤 自动上传与识别
- 录音完成后自动上传到后端
- 后端自动调用ASR进行语音识别
- **识别结果实时显示** - 转写文本直接显示在页面上
- 生成文本向量并保存到数据库

### 📋 操作日志
- 实时显示所有操作记录
- 包含时间戳和详细信息
- 自动保持最近10条记录

## 界面说明

### 状态指示
- 👂 **正在监听** - 等待唤醒词
- 🎯 **检测到唤醒词** - 识别成功
- 🎙️ **正在录音** - 录制中
- 📤 **正在上传** - 上传音频
- ✅ **上传成功** - 处理完成

### 按钮说明
- **启动唤醒词检测** - 开始监听唤醒词
- **停止检测** - 停止监听
- **手动录音** - 不使用唤醒词，直接开始录音

## 浏览器兼容性

### ✅ 完全支持
- Chrome 25+
- Edge 79+
- Opera 27+

### ⚠️ 部分支持
- Safari 14.1+（需要macOS Big Sur以上）

### ❌ 不支持
- Firefox（不支持Web Speech API）
- IE浏览器

## 常见问题

### Q: 为什么检测不到唤醒词？

**A: 请检查以下几点：**
1. 确认使用Chrome浏览器
2. 检查麦克风权限是否允许
3. 确认麦克风硬件正常工作
4. 尝试清晰地说"你好小笔"
5. 检查浏览器控制台是否有错误信息

### Q: 录音质量不好怎么办？

**A: 改善建议：**
1. 使用外置麦克风而非笔记本内置麦克风
2. 在安静的环境中录音
3. 距离麦克风15-30cm
4. 说话清晰、音量适中

### Q: 可以修改唤醒词吗？

**A: 可以！** 编辑 `src/main/resources/static/index.html`，找到：
```javascript
if (transcript.includes('你好小笔') || 
    transcript.includes('你好 小笔') ||
    transcript.includes('你好小比') ||
    transcript.includes('小笔')) {
```
修改为您想要的唤醒词，例如：
```javascript
if (transcript.includes('小助手') || 
    transcript.includes('嘿Siri')) {
```

### Q: 可以修改静音检测时长吗？

**A: 可以！** 在 `index.html` 中找到：
```javascript
const SILENCE_THRESHOLD = 5000; // 5秒静音
```
修改为您需要的时长（毫秒），例如：
```javascript
const SILENCE_THRESHOLD = 3000; // 3秒静音
const SILENCE_THRESHOLD = 10000; // 10秒静音
```

### Q: 可以调整音量检测灵敏度吗？

**A: 可以！** 修改音量阈值：
```javascript
const VOLUME_THRESHOLD = 0.01; // 默认值
const VOLUME_THRESHOLD = 0.02; // 降低灵敏度（需要更大声音）
const VOLUME_THRESHOLD = 0.005; // 提高灵敏度（更容易检测到声音）
```

### Q: 为什么上传失败？

**A: 可能的原因：**
1. 后端服务未启动 - 检查 `mvn spring-boot:run` 是否成功运行
2. 端口被占用 - 确认8080端口可用
3. 数据库未配置 - 检查MySQL和配置文件
4. 网络问题 - 查看浏览器Network标签

## 技术实现

### 使用的Web API
- **Web Speech API** - 语音识别（唤醒词检测）
- **MediaRecorder API** - 音频录制
- **Web Audio API** - 音频分析和音量检测
- **Fetch API** - 文件上传

### 工作流程
```
用户说"你好小笔" 
  ↓
Web Speech API检测到唤醒词
  ↓
触发MediaRecorder开始录音
  ↓
Web Audio API实时监测音量
  ↓
检测到5秒静音后自动停止录音
  ↓
将音频Blob转换为WAV文件
  ↓
通过Fetch API上传到后端
  ↓
后端调用ASR服务识别
  ↓
生成向量并存储到数据库
  ↓
返回转写文本到前端
  ↓
前端显示识别结果（8秒后自动隐藏）
```

## 安全说明

1. **麦克风权限** - 仅在用户允许后才能使用
2. **HTTPS要求** - 生产环境需要HTTPS（开发环境localhost除外）
3. **数据隐私** - 音频数据仅上传到您的服务器
4. **用户控制** - 用户可随时停止检测和录音

## 性能优化建议

### 前端优化
1. 使用音频压缩减小上传文件大小
2. 添加本地缓存避免重复上传
3. 实现断点续传机制

### 后端优化
1. 使用异步处理提高响应速度
2. 添加消息队列处理大量请求
3. 实现音频文件压缩和格式转换

## 扩展功能建议

### 可以添加的功能
- 🔧 支持自定义唤醒词
- 🎨 可配置的录音时长
- 📊 音频波形可视化
- 💾 本地音频缓存
- 🔒 语音加密传输
- 🌐 多语言支持
- 📱 移动端适配
- 🎵 背景噪音消除

## 文件位置

```
wiztip_complete/
└── src/main/resources/static/
    └── index.html          # 前端页面（唤醒词功能）
```

## 相关文档

- [README.md](README.md) - 项目完整文档
- [STARTUP_GUIDE.md](STARTUP_GUIDE.md) - 启动指南
- [Web Speech API文档](https://developer.mozilla.org/en-US/docs/Web/API/Web_Speech_API)
- [MediaRecorder API文档](https://developer.mozilla.org/en-US/docs/Web/API/MediaRecorder)

## 反馈与支持

如有问题或建议，请查看项目README或查看浏览器控制台的详细错误信息。

---

**版本**: 1.0.0  
**最后更新**: 2025-10-20

