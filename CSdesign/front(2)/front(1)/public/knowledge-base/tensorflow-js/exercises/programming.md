# TensorFlow.js 编程题

## 基础编程题

### 1. 创建张量
**题目**: 创建一个2x3的张量，包含数值[1, 2, 3, 4, 5, 6]，并打印其形状和值。

**参考答案**:
```javascript
const shape = [2, 3];
const tensor = tf.tensor([1, 2, 3, 4, 5, 6], shape);
console.log('Shape:', tensor.shape);
tensor.print();
```

### 2. 张量运算
**题目**: 创建两个2x2的张量A和B，计算A+B、A*B和A的平方。

**参考答案**:
```javascript
const A = tf.tensor2d([[1, 2], [3, 4]]);
const B = tf.tensor2d([[5, 6], [7, 8]]);

const sum = A.add(B);
const product = A.mul(B);
const square = A.square();

sum.print();
product.print();
square.print();
```

### 3. 使用tf.tidy()进行内存管理
**题目**: 使用tf.tidy()函数计算一个向量的平均值和标准差。

**参考答案**:
```javascript
const result = tf.tidy(() => {
  const data = tf.tensor1d([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
  const mean = data.mean();
  const variance = data.sub(mean).square().mean();
  const std = variance.sqrt();
  return { mean, std };
});
```

## 模型构建编程题

### 4. 创建Sequential模型
**题目**: 创建一个简单的Sequential模型，包含一个输入层（784个输入）和一个输出层（10个输出）。

**参考答案**:
```javascript
function createSimpleModel() {
  const model = tf.sequential();
  model.add(tf.layers.dense({
    inputShape: [784],
    units: 10,
    activation: 'softmax'
  }));
  model.compile({
    optimizer: 'adam',
    loss: 'categoricalCrossentropy',
    metrics: ['accuracy']
  });
  return model;
}
```

### 5. 创建卷积神经网络
**题目**: 创建一个简单的CNN模型，用于28x28灰度图像分类。

**参考答案**:
```javascript
function createCNN() {
  const model = tf.sequential();
  model.add(tf.layers.conv2d({
    inputShape: [28, 28, 1],
    kernelSize: 3,
    filters: 16,
    activation: 'relu'
  }));
  model.add(tf.layers.maxPooling2d({poolSize: 2}));
  model.add(tf.layers.flatten());
  model.add(tf.layers.dense({units: 10, activation: 'softmax'}));
  model.compile({optimizer: 'adam', loss: 'categoricalCrossentropy'});
  return model;
}
```

## 数据处理编程题

### 6. 数据归一化
**题目**: 创建一个函数，对输入数据进行min-max归一化处理。

**参考答案**:
```javascript
function normalizeData(data) {
  return tf.tidy(() => {
    const tensor = tf.tensor2d(data);
    const min = tensor.min();
    const max = tensor.max();
    const normalized = tensor.sub(min).div(max.sub(min));
    return {normalized, min: min.dataSync()[0], max: max.dataSync()[0]};
  });
}
```

### 7. 数据打乱和分割
**题目**: 创建一个函数，随机打乱数据集并分割为训练集和测试集。

**参考答案**:
```javascript
function splitData(data, labels, trainRatio = 0.8) {
  const shuffledIndices = tf.util.createShuffledIndices(data.length);
  const shuffledData = [];
  const shuffledLabels = [];
  
  for (let i = 0; i < data.length; i++) {
    shuffledData.push(data[shuffledIndices[i]]);
    shuffledLabels.push(labels[shuffledIndices[i]]);
  }
  
  const trainSize = Math.floor(data.length * trainRatio);
  return {
    trainData: tf.tensor2d(shuffledData.slice(0, trainSize)),
    trainLabels: tf.tensor2d(shuffledLabels.slice(0, trainSize)),
    testData: tf.tensor2d(shuffledData.slice(trainSize)),
    testLabels: tf.tensor2d(shuffledLabels.slice(trainSize))
  };
}
```

## 训练编程题

### 8. 自定义训练循环
**题目**: 实现一个简单的自定义训练循环，不使用model.fit()。

**参考答案**:
```javascript
async function customTraining(model, trainData, trainLabels, epochs = 10) {
  const optimizer = tf.train.adam(0.01);
  
  for (let epoch = 0; epoch < epochs; epoch++) {
    const loss = tf.tidy(() => {
      const predictions = model.predict(trainData);
      return tf.losses.meanSquaredError(trainLabels, predictions);
    });
    
    const lossValue = loss.dataSync()[0];
    console.log(`Epoch ${epoch + 1}, Loss: ${lossValue.toFixed(4)}`);
    
    const grads = tf.grad(loss => {
      const predictions = model.predict(trainData);
      return tf.losses.meanSquaredError(trainLabels, predictions);
    })(model.trainableVariables);
    
    optimizer.applyGradients(grads);
    loss.dispose();
  }
}
```

### 9. 模型预测
**题目**: 创建一个函数，使用训练好的模型进行预测。

**参考答案**:
```javascript
function predict(model, inputData) {
  return tf.tidy(() => {
    const input = tf.tensor2d(inputData);
    const predictions = model.predict(input);
    const predictedClasses = predictions.argMax(-1);
    return {
      predictions: predictions.dataSync(),
      predictedClasses: predictedClasses.dataSync()
    };
  });
}
```

## 高级编程题

### 10. 模型保存和加载
**题目**: 实现模型的保存和加载功能。

**参考答案**:
```javascript
async function saveModel(model, path) {
  try {
    await model.save(`localstorage://${path}`);
    console.log('Model saved successfully');
  } catch (error) {
    console.error('Error saving model:', error);
  }
}

async function loadModel(path) {
  try {
    const model = await tf.loadLayersModel(`localstorage://${path}`);
    console.log('Model loaded successfully');
    return model;
  } catch (error) {
    console.error('Error loading model:', error);
    return null;
  }
}
```

### 11. 数据增强
**题目**: 实现简单的数据增强功能，对图像数据进行旋转和缩放。

**参考答案**:
```javascript
function augmentImage(imageTensor) {
  return tf.tidy(() => {
    const rotation = tf.randomUniform([], -0.3, 0.3);
    const rotated = tf.image.rotateWithOffset(imageTensor, rotation);
    const scale = tf.randomUniform([], 0.8, 1.2);
    const scaled = tf.image.resizeBilinear(rotated, 
      [Math.floor(28 * scale), Math.floor(28 * scale)]);
    return tf.image.resizeBilinear(scaled, [28, 28]);
  });
}
```

### 12. 模型评估
**题目**: 创建一个函数，计算模型的准确率和混淆矩阵。

**参考答案**:
```javascript
function evaluateModel(model, testData, testLabels) {
  return tf.tidy(() => {
    const predictions = model.predict(testData);
    const predictedClasses = predictions.argMax(-1);
    const trueClasses = testLabels.argMax(-1);
    
    const accuracy = predictedClasses.equal(trueClasses).mean();
    const confusionMatrix = tf.confusionMatrix(trueClasses, predictedClasses, 10);
    
    return {
      accuracy: accuracy.dataSync()[0],
      confusionMatrix: confusionMatrix.arraySync()
    };
  });
}
```

## 综合编程题

### 13. 完整的线性回归项目
**题目**: 实现一个完整的线性回归项目。

**参考答案**:
```javascript
// 生成模拟数据
function generateData(numPoints = 100) {
  const x = tf.randomUniform([numPoints, 1], 0, 10);
  const noise = tf.randomNormal([numPoints, 1], 0, 0.5);
  const y = x.mul(2).add(1).add(noise);
  return { x, y };
}

// 创建模型
function createLinearModel() {
  const model = tf.sequential({
    layers: [tf.layers.dense({ inputShape: [1], units: 1 })]
  });
  model.compile({ optimizer: 'adam', loss: 'meanSquaredError' });
  return model;
}

// 训练模型
async function trainModel(model, x, y, epochs = 100) {
  return await model.fit(x, y, {
    epochs,
    batchSize: 32,
    validationSplit: 0.2
  });
}

// 主函数
async function runLinearRegression() {
  const { x, y } = generateData(200);
  const model = createLinearModel();
  await trainModel(model, x, y, 100);
  
  const testX = tf.tensor2d([[5], [10], [15]]);
  const predictions = model.predict(testX);
  predictions.print();
}
```

### 14. 图像分类项目
**题目**: 实现一个简单的图像分类项目，使用预训练模型。

**参考答案**:
```javascript
async function imageClassification() {
  // 加载预训练模型
  const model = await tf.loadLayersModel(
    'https://storage.googleapis.com/tfjs-models/tfhub/mobilenet_v2_100_224/model.json'
  );
  
  // 加载图像
  const img = new Image();
  img.src = 'path/to/image.jpg';
  
  img.onload = () => {
    const tensor = tf.browser.fromPixels(img)
      .resizeNearestNeighbor([224, 224])
      .toFloat()
      .div(255)
      .expandDims();
    
    const predictions = model.predict(tensor);
    predictions.print();
  };
}
```

### 15. 实时预测应用
**题目**: 创建一个实时预测应用，使用摄像头进行实时图像分类。

**参考答案**:
```javascript
async function realTimePrediction() {
  const video = document.getElementById('video');
  const canvas = document.getElementById('canvas');
  const ctx = canvas.getContext('2d');
  
  // 加载模型
  const model = await tf.loadLayersModel('path/to/model.json');
  
  // 启动摄像头
  const stream = await navigator.mediaDevices.getUserMedia({ video: true });
  video.srcObject = stream;
  
  // 实时预测
  setInterval(() => {
    ctx.drawImage(video, 0, 0, 224, 224);
    const imageData = ctx.getImageData(0, 0, 224, 224);
    
    const tensor = tf.browser.fromPixels(imageData)
      .toFloat()
      .div(255)
      .expandDims();
    
    const prediction = model.predict(tensor);
    const result = prediction.argMax(-1).dataSync()[0];
    
    console.log('Predicted class:', result);
  }, 100);
}
```

这些编程题涵盖了TensorFlow.js的各个方面，从基础操作到高级应用，可以帮助学习者逐步掌握TensorFlow.js的使用。 