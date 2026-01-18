# JavaScript 基础知识

## 变量和数据类型

### 变量声明
```javascript
// 使用 let 声明变量（推荐）
let name = "张三";
let age = 25;

// 使用 const 声明常量
const PI = 3.14159;

// 使用 var 声明变量（不推荐）
var oldWay = "旧方式";
```

### 基本数据类型
1. **String（字符串）**
   ```javascript
   let str1 = "双引号字符串";
   let str2 = '单引号字符串';
   let str3 = `模板字符串 ${name}`;
   ```

2. **Number（数字）**
   ```javascript
   let integer = 42;
   let float = 3.14;
   let negative = -10;
   ```

3. **Boolean（布尔值）**
   ```javascript
   let isTrue = true;
   let isFalse = false;
   ```

4. **Undefined（未定义）**
   ```javascript
   let notDefined;
   console.log(notDefined); // undefined
   ```

5. **Null（空值）**
   ```javascript
   let empty = null;
   ```

6. **Symbol（符号）**
   ```javascript
   let symbol = Symbol('description');
   ```

## 函数

### 函数声明
```javascript
// 函数声明
function greet(name) {
    return `你好，${name}！`;
}

// 函数表达式
const greet2 = function(name) {
    return `你好，${name}！`;
};

// 箭头函数
const greet3 = (name) => {
    return `你好，${name}！`;
};

// 简写箭头函数
const greet4 = name => `你好，${name}！`;
```

### 函数参数
```javascript
// 默认参数
function createUser(name, age = 18) {
    return { name, age };
}

// 剩余参数
function sum(...numbers) {
    return numbers.reduce((total, num) => total + num, 0);
}
```

## 数组

### 数组操作
```javascript
// 创建数组
let fruits = ['苹果', '香蕉', '橙子'];

// 访问元素
console.log(fruits[0]); // 苹果

// 添加元素
fruits.push('葡萄'); // 末尾添加
fruits.unshift('草莓'); // 开头添加

// 删除元素
fruits.pop(); // 删除末尾元素
fruits.shift(); // 删除开头元素

// 数组方法
fruits.forEach(fruit => console.log(fruit));
let upperFruits = fruits.map(fruit => fruit.toUpperCase());
let filteredFruits = fruits.filter(fruit => fruit.length > 2);
```

## 对象

### 对象创建和操作
```javascript
// 对象字面量
let person = {
    name: '李四',
    age: 30,
    greet() {
        return `你好，我是${this.name}`;
    }
};

// 访问属性
console.log(person.name);
console.log(person['age']);

// 添加属性
person.city = '北京';
person['job'] = '程序员';

// 删除属性
delete person.age;
```

## 控制流

### 条件语句
```javascript
// if-else
if (age >= 18) {
    console.log('成年人');
} else if (age >= 12) {
    console.log('青少年');
} else {
    console.log('儿童');
}

// 三元运算符
let status = age >= 18 ? '成年' : '未成年';

// switch语句
switch (grade) {
    case 'A':
        console.log('优秀');
        break;
    case 'B':
        console.log('良好');
        break;
    default:
        console.log('需要努力');
}
```

### 循环
```javascript
// for循环
for (let i = 0; i < 5; i++) {
    console.log(i);
}

// for...of循环（遍历可迭代对象）
for (let fruit of fruits) {
    console.log(fruit);
}

// for...in循环（遍历对象属性）
for (let key in person) {
    console.log(`${key}: ${person[key]}`);
}

// while循环
let i = 0;
while (i < 5) {
    console.log(i);
    i++;
}
```

## 错误处理

### try-catch
```javascript
try {
    // 可能出错的代码
    let result = someFunction();
} catch (error) {
    // 处理错误
    console.error('发生错误:', error.message);
} finally {
    // 总是执行的代码
    console.log('清理工作');
}
```

## 异步编程

### Promise
```javascript
// 创建Promise
const fetchData = () => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            const data = { id: 1, name: '测试数据' };
            resolve(data);
        }, 1000);
    });
};

// 使用Promise
fetchData()
    .then(data => console.log(data))
    .catch(error => console.error(error));
```

### async/await
```javascript
async function getData() {
    try {
        const data = await fetchData();
        console.log(data);
    } catch (error) {
        console.error(error);
    }
}
```

## 模块化

### ES6模块
```javascript
// math.js
export const add = (a, b) => a + b;
export const subtract = (a, b) => a - b;

export default class Calculator {
    multiply(a, b) {
        return a * b;
    }
}

// main.js
import Calculator, { add, subtract } from './math.js';
```

## 最佳实践

1. **使用严格模式**
   ```javascript
   'use strict';
   ```

2. **避免全局变量**
   ```javascript
   // 使用模块或闭包
   (function() {
       let privateVar = '私有变量';
   })();
   ```

3. **使用有意义的变量名**
   ```javascript
   // 好的命名
   let userName = '张三';
   let isLoggedIn = true;
   
   // 避免的命名
   let a = '张三';
   let flag = true;
   ```

4. **代码格式化**
   - 使用一致的缩进
   - 添加适当的空格
   - 使用分号结尾

5. **注释**
   ```javascript
   /**
    * 计算两个数的和
    * @param {number} a - 第一个数
    * @param {number} b - 第二个数
    * @returns {number} 两数之和
    */
   function add(a, b) {
       return a + b;
   }
   ``` 