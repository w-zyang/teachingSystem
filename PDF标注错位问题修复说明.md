# PDF标注错位问题修复说明

## 🐛 问题描述

### 现象
用户在PDF文档上创建标注后，标注显示的位置与实际选中的文本位置不一致，导致：
- ✗ 标注错位，不在正确的位置
- ✗ 标注文本被截断
- ✗ 破坏了文档的整体结构
- ✗ 影响阅读体验

### 示例
**期望标注：** `"目录 mv 移动/重命名文件 touch 创建空文件或更新时间戳"`  
**实际标注：** `"录 mv 移动/重命名文件 touch 创建空"` ❌

**问题：**
1. 文本被截断（缺少开头的"目"和结尾的"文件或更新时间戳"）
2. 标注位置不准确
3. 跨节点标注时边界计算错误

## 🔍 根本原因

### 1. 文本规范化导致的问题

**之前的逻辑：**
```javascript
// 将所有空白字符规范化为单个空格
const normalizedText = selectedText.value
  .replace(/\s+/g, ' ')  // ❌ 问题：丢失了原始格式
  .trim()                // ❌ 问题：去掉了首尾空格
```

**问题：**
- HTML中的换行符、制表符、多个空格被替换为单个空格
- 原始文本和规范化文本的长度不一致
- 映射回原始位置时出现偏差

### 2. 复杂的位置映射逻辑

**之前的映射逻辑：**
```javascript
// 尝试将规范化文本的位置映射回原始文本
while (origIdx < fullText.length) {
  const char = fullText[origIdx]
  if (!/\s/.test(char)) {
    // 非空白字符处理
    normIdx++
  } else {
    // 空白字符处理 - 复杂的判断逻辑
    if (prevNonSpace && nextNonSpace) {
      normIdx++
    }
  }
  origIdx++
}
```

**问题：**
- 映射逻辑过于复杂，容易出错
- 边界情况处理不当
- 跨节点时计算不准确

## ✅ 解决方案

### 核心思路
**放弃文本规范化，使用原始文本进行精确匹配**

### 修改内容

#### 1. 保存原始文本（不做任何修改）

**修改前：**
```javascript
const normalizedText = selectedText.value
  .replace(/\s+/g, ' ')
  .trim()

const annotation = {
  selectedText: normalizedText,  // ❌ 保存规范化文本
  position: JSON.stringify({ text: normalizedText })
}
```

**修改后：**
```javascript
const originalText = selectedText.value  // ✅ 保存原始文本

const annotation = {
  selectedText: originalText,  // ✅ 完全保留原始格式
  position: JSON.stringify({ 
    simple: true, 
    text: originalText  // ✅ 原始文本，包含所有空白字符
  })
}
```

#### 2. 简化标注应用逻辑

**修改前：**
```javascript
// 1. 规范化搜索文本
const normalizedSearchText = searchText.replace(/\s+/g, ' ').trim()

// 2. 规范化文档文本
const normalizedFullText = fullText.replace(/\s+/g, ' ').trim()

// 3. 在规范化文本中查找
const searchIndex = normalizedFullText.indexOf(normalizedSearchText)

// 4. 复杂的位置映射
// ... 100+ 行的映射逻辑
```

**修改后：**
```javascript
// 1. 直接使用原始文本
const searchText = posInfo.text  // ✅ 原始文本

// 2. 构建完整文本（保留原始格式）
let fullText = ''
textNodes.forEach(node => {
  fullText += node.textContent  // ✅ 不做任何修改
})

// 3. 直接查找
const searchIndex = fullText.indexOf(searchText)  // ✅ 精确匹配

// 4. 直接使用索引（无需映射）
const startInfo = nodeMap[searchIndex]
const endInfo = nodeMap[searchIndex + searchText.length - 1]
```

## 📊 对比分析

| 方面 | 修改前 | 修改后 |
|------|--------|--------|
| **文本处理** | 规范化（替换空白字符） | 保留原始格式 |
| **匹配方式** | 规范化文本匹配 + 位置映射 | 原始文本直接匹配 |
| **代码复杂度** | 高（150+ 行） | 低（80 行） |
| **准确性** | 低（容易出错） | 高（精确匹配） |
| **性能** | 较慢（复杂计算） | 较快（直接查找） |
| **维护性** | 差（逻辑复杂） | 好（逻辑简单） |

## 🎯 修复效果

### 修复前
```
期望标注: "目录 mv 移动/重命名文件 touch 创建空文件或更新时间戳"
实际标注: "录 mv 移动/重命名文件 touch 创建空"  ❌ 错位
```

### 修复后
```
期望标注: "目录 mv 移动/重命名文件 touch 创建空文件或更新时间戳"
实际标注: "目录 mv 移动/重命名文件 touch 创建空文件或更新时间戳"  ✅ 精确
```

## 🔧 技术细节

### 1. 文本节点映射

```javascript
// 构建字符到节点的映射
const nodeMap = []
textNodes.forEach(textNode => {
  const nodeText = textNode.textContent
  for (let i = 0; i < nodeText.length; i++) {
    nodeMap.push({
      node: textNode,    // 该字符所在的文本节点
      offset: i          // 该字符在节点中的偏移量
    })
  }
})

// 使用映射快速定位
const startInfo = nodeMap[searchIndex]  // 起始字符的节点信息
const endInfo = nodeMap[searchIndex + searchText.length - 1]  // 结束字符的节点信息
```

### 2. 单节点标注

```javascript
if (startInfo.node === endInfo.node) {
  const nodeText = startInfo.node.textContent
  
  // 分割文本
  const beforeText = nodeText.substring(0, startInfo.offset)
  const matchedText = nodeText.substring(startInfo.offset, endInfo.offset + 1)
  const afterText = nodeText.substring(endInfo.offset + 1)
  
  // 插入标注
  wrapper.textContent = matchedText
  parent.insertBefore(document.createTextNode(beforeText), startInfo.node)
  parent.insertBefore(wrapper, startInfo.node)
  parent.insertBefore(document.createTextNode(afterText), startInfo.node)
  parent.removeChild(startInfo.node)
}
```

### 3. 跨节点标注

```javascript
// 收集所有涉及的节点
const nodesToWrap = []
for (const textNode of textNodes) {
  if (textNode === startInfo.node) collecting = true
  if (collecting) nodesToWrap.push(textNode)
  if (textNode === endInfo.node) break
}

// 处理第一个节点（部分文本）
const firstMatched = firstText.substring(startInfo.offset)
wrapper.appendChild(document.createTextNode(firstMatched))

// 处理中间节点（完整文本）
for (let i = 1; i < nodesToWrap.length - 1; i++) {
  wrapper.appendChild(document.createTextNode(nodesToWrap[i].textContent))
}

// 处理最后一个节点（部分文本）
const lastMatched = lastText.substring(0, endInfo.offset + 1)
wrapper.appendChild(document.createTextNode(lastMatched))
```

## 📝 日志输出

### 修复后的日志
```
📝 原始选中文本: "目录 mv 移动/重命名文件 touch 创建空文件或更新时间戳"
📝 文本长度: 32
💾 保存标注位置信息: {simple: true, text: "目录 mv 移动/重命名文件 touch 创建空文件或更新时间戳"}

🔧 处理标注 #1/1
   ID: 224
   类型: highlight
   颜色: #FFFF00
   文本: "目录 mv 移动/重命名文件 touch 创建空文件或更新时间戳"
   📍 使用文本搜索模式
   🔍 搜索文本: "目录 mv 移动/重命名文件 touch 创建空文件或更新时间戳"
   📏 搜索文本长度: 32
   📄 完整文本长度: 4850
   ✅ 找到文本位置: 245
   📍 起始节点偏移: 12, 结束节点偏移: 43
   ✅ 文本搜索成功应用标注（单节点）
```

## ⚠️ 注意事项

### 1. 文档修改问题
如果文档内容被修改，旧的标注可能无法找到：
```javascript
if (searchIndex === -1) {
  console.warn(`❌ 文本搜索未找到: "${searchText}"`)
  console.warn(`💡 提示: 文档可能已被修改，或文本包含特殊字符`)
  return  // 跳过该标注
}
```

### 2. 特殊字符处理
原始文本包含所有字符，包括：
- 换行符 `\n`
- 制表符 `\t`
- 多个连续空格
- 特殊Unicode字符

### 3. 性能考虑
- 对于大文档（>10000字符），文本搜索可能较慢
- 建议限制单次标注的文本长度（<500字符）
- 考虑使用索引优化搜索性能

## 🧪 测试建议

### 测试场景

#### 1. 单行文本标注
```
选中: "ls 列出目录内容"
期望: 精确标注这段文本
```

#### 2. 跨行文本标注
```
选中: "mkdir 创建目录
mkdir -p 递归创建目录"
期望: 标注包含换行符的文本
```

#### 3. 包含多个空格的文本
```
选中: "ls          列出目录内容"
期望: 保留所有空格
```

#### 4. 包含特殊字符的文本
```
选中: "chmod +x file.sh"
期望: 正确处理特殊字符
```

### 验证方法

1. **创建标注**
   - 选中文本
   - 点击标注按钮
   - 检查标注是否在正确位置

2. **刷新页面**
   - 刷新浏览器
   - 检查标注是否仍在正确位置

3. **检查数据库**
```sql
SELECT 
  id,
  type,
  selected_text,
  LENGTH(selected_text) as text_length,
  position
FROM student_annotation
WHERE resource_id = 38
ORDER BY id DESC
LIMIT 5;
```

## 🚀 后续优化建议

### 功能增强
- [ ] 添加标注预览功能
- [ ] 支持标注文本编辑
- [ ] 添加标注版本控制
- [ ] 支持标注导出/导入

### 性能优化
- [ ] 使用索引加速文本搜索
- [ ] 缓存文本节点映射
- [ ] 延迟加载大量标注
- [ ] 虚拟滚动优化

### 用户体验
- [ ] 添加标注位置指示器
- [ ] 支持标注拖拽调整
- [ ] 添加标注冲突检测
- [ ] 优化标注颜色选择

## 📞 问题排查

### 如果标注仍然错位

1. **清除浏览器缓存**
```javascript
// 在浏览器控制台执行
localStorage.clear()
sessionStorage.clear()
location.reload()
```

2. **检查文档内容**
```javascript
// 检查文档是否被修改
console.log('文档内容:', documentText.value)
console.log('文档长度:', documentText.value.length)
```

3. **检查标注数据**
```javascript
// 检查标注的原始文本
annotations.value.forEach(ann => {
  console.log(`标注 #${ann.id}:`, ann.selectedText)
  console.log(`位置信息:`, JSON.parse(ann.position))
})
```

4. **重新创建标注**
   - 删除旧的标注
   - 重新选中文本
   - 创建新标注

## ✅ 验证清单

- [x] 移除文本规范化逻辑
- [x] 保存原始选中文本
- [x] 简化标注应用逻辑
- [x] 使用直接文本匹配
- [x] 优化日志输出
- [x] 测试单节点标注
- [x] 测试跨节点标注
- [x] 测试包含特殊字符的文本
- [x] 验证标注位置准确性

---

**修复时间：** 2026-01-18  
**版本：** v2.0  
**状态：** ✅ 已修复
