# TensorFlow.js 多选题

## 基础知识

### 1. TensorFlow.js的主要优势包括哪些？（多选）
- [ ] A. 只能在服务器端运行
- [x] B. 支持浏览器端GPU加速
- [x] C. 使用JavaScript语言，降低前端工程师门槛
- [x] D. 可以与Python版TensorFlow模型互转
- [x] E. 开箱即用，无需编写复杂数学运算

**答案**: BCDE
**解析**: TensorFlow.js的主要优势包括浏览器端运行、GPU加速、JavaScript语言友好、模型互转和开箱即用等特性。

### 2. TensorFlow.js支持哪些运行环境？（多选）
- [x] A. 浏览器环境
- [x] B. Node.js环境
- [ ] C. Python环境
- [x] D. 移动端WebView
- [ ] E. Java环境

**答案**: ABD
**解析**: TensorFlow.js是JavaScript版本，主要支持浏览器、Node.js和移动端WebView环境。

### 3. TensorFlow.js的架构层次包括哪些？（多选）
- [x] A. Layers API（高级API）
- [x] B. Core API（低级API）
- [x] C. 硬件加速层
- [ ] D. Python API
- [ ] E. Java API

**答案**: ABC
**解析**: TensorFlow.js的架构包括Layers API、Core API和硬件加速层三个主要层次。

## 核心概念

### 4. 在TensorFlow.js中，张量（Tensor）的特点包括哪些？（多选）
- [x] A. 是不可变的数据结构
- [x] B. 可以是一维或多维数组
- [x] C. 有shape属性定义数组形状
- [ ] D. 可以随时修改其值
- [x] E. 是中心数据单元

**答案**: ABCE
**解析**: 张量是不可变的，可以是一维或多维数组，有shape属性，是TensorFlow.js的中心数据单元。

### 5. 关于TensorFlow.js中的变量（Variable），下列说法正确的有？（多选）
- [x] A. 用张量的值进行初始化
- [x] B. 值是可变的
- [x] C. 可以使用assign方法分配新值
- [ ] D. 与张量完全相同
- [x] E. 通常用于存储模型参数

**答案**: ABCE
**解析**: 变量用张量初始化，值可变，可用assign方法修改，通常用于存储模型参数。

### 6. TensorFlow.js中的操作（Ops）特点包括哪些？（多选）
- [x] A. 不会改变原始张量的值
- [x] B. 返回新的张量
- [x] C. 包含线性代数运算
- [x] D. 包含机器学习运算
- [ ] E. 会修改原始张量

**答案**: ABCD
**解析**: 操作不会改变原始张量，返回新张量，包含线代和机器学习运算。

## 模型构建

### 7. TensorFlow.js中创建模型的方式包括哪些？（多选）
- [x] A. 使用tf.sequential()
- [x] B. 使用tf.model()
- [ ] C. 使用tf.python()
- [x] D. 使用Core API手动构建
- [ ] E. 使用tf.java()

**答案**: ABD
**解析**: TensorFlow.js支持sequential模型、functional模型和Core API三种创建方式。

### 8. Sequential模型的特点包括哪些？（多选）
- [x] A. 线性堆叠层
- [x] B. 简单易用
- [x] C. 适合大多数神经网络
- [ ] D. 支持复杂的层间连接
- [x] E. 类似Keras的Sequential

**答案**: ABCE
**解析**: Sequential模型是线性堆叠的，简单易用，适合大多数网络，类似Keras。

### 9. 关于TensorFlow.js中的层（Layers），下列说法正确的有？（多选）
- [x] A. 提供权重初始化
- [x] B. 支持模型序列化
- [x] C. 提供训练监测
- [x] D. 支持可迁移性
- [x] E. 提供安全检查

**答案**: ABCDE
**解析**: Layers API提供了完整的工具链，包括权重初始化、序列化、监测、迁移和安全检查。

## 内存管理

### 10. TensorFlow.js中内存管理的方法包括哪些？（多选）
- [x] A. 使用dispose()方法
- [x] B. 使用tf.tidy()函数
- [ ] C. 使用tf.python()
- [x] D. 自动垃圾回收
- [ ] E. 手动释放内存

**答案**: ABD
**解析**: TensorFlow.js提供dispose()、tf.tidy()和自动垃圾回收三种内存管理方式。

### 11. 使用tf.tidy()的好处包括哪些？（多选）
- [x] A. 防止内存泄漏
- [x] B. 自动清理中间张量
- [x] C. 谨慎控制内存回收
- [ ] D. 提高代码执行速度
- [x] E. 简化内存管理

**答案**: ABCE
**解析**: tf.tidy()可以防止内存泄漏，自动清理中间张量，谨慎控制内存回收，简化管理。

## 环境配置

### 12. 在浏览器中加载TensorFlow.js的方式包括哪些？（多选）
- [x] A. 使用script标签
- [x] B. 使用CDN链接
- [x] C. 使用npm安装
- [x] D. 使用构建工具
- [ ] E. 使用Python pip

**答案**: ABCD
**解析**: 浏览器中可以通过script标签、CDN、npm安装和构建工具等方式加载TensorFlow.js。

### 13. 使用构建工具的优势包括哪些？（多选）
- [x] A. 更好的代码组织
- [x] B. 支持模块化开发
- [x] C. 代码压缩和优化
- [x] D. 支持TypeScript
- [ ] E. 更简单的部署

**答案**: ABCD
**解析**: 构建工具提供更好的代码组织、模块化、压缩优化和TypeScript支持。

## 应用场景

### 14. TensorFlow.js适合的应用场景包括哪些？（多选）
- [x] A. 实时图像识别
- [x] B. 语音处理
- [x] C. 自然语言处理
- [x] D. 游戏AI
- [x] E. 数据可视化

**答案**: ABCDE
**解析**: TensorFlow.js适用于图像识别、语音处理、NLP、游戏AI和数据可视化等多种场景。

### 15. 浏览器端机器学习的优势包括哪些？（多选）
- [x] A. 减少服务器压力
- [x] B. 提高响应速度
- [x] C. 更好的隐私保护
- [x] D. 可调用设备硬件
- [x] E. 实时交互

**答案**: ABCDE
**解析**: 浏览器端机器学习具有减少服务器压力、提高响应速度、隐私保护、硬件调用和实时交互等优势。 