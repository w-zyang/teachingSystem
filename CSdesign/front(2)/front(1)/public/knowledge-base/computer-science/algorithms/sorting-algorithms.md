# 排序算法详解

## 概述

排序算法是计算机科学中最基本和重要的算法之一。它们用于将一组数据按照特定的顺序重新排列。

## 常见排序算法

### 1. 冒泡排序 (Bubble Sort)

**原理**: 重复遍历要排序的数组，比较相邻的两个元素，如果顺序错误就交换它们。

**时间复杂度**: O(n²)
**空间复杂度**: O(1)
**稳定性**: 稳定

```javascript
function bubbleSort(arr) {
    const n = arr.length;
    for (let i = 0; i < n - 1; i++) {
        for (let j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                // 交换元素
                [arr[j], arr[j + 1]] = [arr[j + 1], arr[j]];
            }
        }
    }
    return arr;
}

// 示例
const arr = [64, 34, 25, 12, 22, 11, 90];
console.log(bubbleSort([...arr])); // [11, 12, 22, 25, 34, 64, 90]
```

### 2. 选择排序 (Selection Sort)

**原理**: 每次从未排序区间选择最小的元素，放到已排序区间的末尾。

**时间复杂度**: O(n²)
**空间复杂度**: O(1)
**稳定性**: 不稳定

```javascript
function selectionSort(arr) {
    const n = arr.length;
    for (let i = 0; i < n - 1; i++) {
        let minIndex = i;
        for (let j = i + 1; j < n; j++) {
            if (arr[j] < arr[minIndex]) {
                minIndex = j;
            }
        }
        if (minIndex !== i) {
            [arr[i], arr[minIndex]] = [arr[minIndex], arr[i]];
        }
    }
    return arr;
}
```

### 3. 插入排序 (Insertion Sort)

**原理**: 将数组分为已排序和未排序两部分，逐个将未排序部分的元素插入到已排序部分的正确位置。

**时间复杂度**: O(n²)
**空间复杂度**: O(1)
**稳定性**: 稳定

```javascript
function insertionSort(arr) {
    const n = arr.length;
    for (let i = 1; i < n; i++) {
        let key = arr[i];
        let j = i - 1;
        
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
    return arr;
}
```

### 4. 快速排序 (Quick Sort)

**原理**: 选择一个基准元素，将数组分为两部分，左边都小于基准，右边都大于基准，然后递归排序。

**时间复杂度**: 平均 O(n log n)，最坏 O(n²)
**空间复杂度**: O(log n)
**稳定性**: 不稳定

```javascript
function quickSort(arr) {
    if (arr.length <= 1) {
        return arr;
    }
    
    const pivot = arr[Math.floor(arr.length / 2)];
    const left = arr.filter(x => x < pivot);
    const middle = arr.filter(x => x === pivot);
    const right = arr.filter(x => x > pivot);
    
    return [...quickSort(left), ...middle, ...quickSort(right)];
}

// 原地快速排序（更高效）
function quickSortInPlace(arr, left = 0, right = arr.length - 1) {
    if (left < right) {
        const pivotIndex = partition(arr, left, right);
        quickSortInPlace(arr, left, pivotIndex - 1);
        quickSortInPlace(arr, pivotIndex + 1, right);
    }
    return arr;
}

function partition(arr, left, right) {
    const pivot = arr[right];
    let i = left - 1;
    
    for (let j = left; j < right; j++) {
        if (arr[j] <= pivot) {
            i++;
            [arr[i], arr[j]] = [arr[j], arr[i]];
        }
    }
    
    [arr[i + 1], arr[right]] = [arr[right], arr[i + 1]];
    return i + 1;
}
```

### 5. 归并排序 (Merge Sort)

**原理**: 将数组分成两半，递归排序每一半，然后合并两个有序数组。

**时间复杂度**: O(n log n)
**空间复杂度**: O(n)
**稳定性**: 稳定

```javascript
function mergeSort(arr) {
    if (arr.length <= 1) {
        return arr;
    }
    
    const mid = Math.floor(arr.length / 2);
    const left = mergeSort(arr.slice(0, mid));
    const right = mergeSort(arr.slice(mid));
    
    return merge(left, right);
}

function merge(left, right) {
    const result = [];
    let i = 0, j = 0;
    
    while (i < left.length && j < right.length) {
        if (left[i] <= right[j]) {
            result.push(left[i]);
            i++;
        } else {
            result.push(right[j]);
            j++;
        }
    }
    
    return result.concat(left.slice(i), right.slice(j));
}
```

### 6. 堆排序 (Heap Sort)

**原理**: 利用堆这种数据结构所设计的排序算法。

**时间复杂度**: O(n log n)
**空间复杂度**: O(1)
**稳定性**: 不稳定

```javascript
function heapSort(arr) {
    const n = arr.length;
    
    // 构建最大堆
    for (let i = Math.floor(n / 2) - 1; i >= 0; i--) {
        heapify(arr, n, i);
    }
    
    // 逐个提取堆顶元素
    for (let i = n - 1; i > 0; i--) {
        [arr[0], arr[i]] = [arr[i], arr[0]];
        heapify(arr, i, 0);
    }
    
    return arr;
}

function heapify(arr, n, i) {
    let largest = i;
    const left = 2 * i + 1;
    const right = 2 * i + 2;
    
    if (left < n && arr[left] > arr[largest]) {
        largest = left;
    }
    
    if (right < n && arr[right] > arr[largest]) {
        largest = right;
    }
    
    if (largest !== i) {
        [arr[i], arr[largest]] = [arr[largest], arr[i]];
        heapify(arr, n, largest);
    }
}
```

## 算法比较

| 算法 | 平均时间复杂度 | 最坏时间复杂度 | 空间复杂度 | 稳定性 |
|------|----------------|----------------|------------|--------|
| 冒泡排序 | O(n²) | O(n²) | O(1) | 稳定 |
| 选择排序 | O(n²) | O(n²) | O(1) | 不稳定 |
| 插入排序 | O(n²) | O(n²) | O(1) | 稳定 |
| 快速排序 | O(n log n) | O(n²) | O(log n) | 不稳定 |
| 归并排序 | O(n log n) | O(n log n) | O(n) | 稳定 |
| 堆排序 | O(n log n) | O(n log n) | O(1) | 不稳定 |

## 选择建议

1. **小数据量 (n < 50)**: 插入排序
2. **中等数据量**: 快速排序
3. **大数据量**: 归并排序
4. **内存受限**: 堆排序
5. **要求稳定**: 归并排序或插入排序

## 实际应用

```javascript
// 实际项目中的排序使用
const students = [
    { name: '张三', score: 85 },
    { name: '李四', score: 92 },
    { name: '王五', score: 78 }
];

// 按分数排序
students.sort((a, b) => b.score - a.score);

// 按姓名排序
students.sort((a, b) => a.name.localeCompare(b.name));
```

## 性能测试

```javascript
function performanceTest() {
    const sizes = [100, 1000, 10000];
    const algorithms = [
        { name: '冒泡排序', fn: bubbleSort },
        { name: '选择排序', fn: selectionSort },
        { name: '插入排序', fn: insertionSort },
        { name: '快速排序', fn: quickSort },
        { name: '归并排序', fn: mergeSort },
        { name: '堆排序', fn: heapSort }
    ];
    
    algorithms.forEach(alg => {
        console.log(`\n${alg.name}:`);
        sizes.forEach(size => {
            const arr = Array.from({length: size}, () => Math.floor(Math.random() * 1000));
            const start = performance.now();
            alg.fn([...arr]);
            const end = performance.now();
            console.log(`  大小 ${size}: ${(end - start).toFixed(2)}ms`);
        });
    });
} 