file_path = 'front(2)/front(1)/src/views/teacher/StudentAnalysis.vue'

# 用二进制读取
with open(file_path, 'rb') as f:
    raw_bytes = f.read()

# 尝试用 GBK 解码（Windows 中文编码）
try:
    content = raw_bytes.decode('gbk')
    print("Decoded with GBK successfully")
except:
    try:
        content = raw_bytes.decode('gb18030')
        print("Decoded with GB18030 successfully")
    except:
        # 如果都失败，用 UTF-8 并忽略错误
        content = raw_bytes.decode('utf-8', errors='ignore')
        print("Decoded with UTF-8 (ignoring errors)")

# 保存为 UTF-8
with open(file_path, 'w', encoding='utf-8', newline='\n') as f:
    f.write(content)

print(f"File saved as UTF-8, size: {len(content)} characters")

# 验证几行
lines = content.split('\n')
if len(lines) > 157:
    print(f"\nLine 157: {lines[156][:80]}")
    print(f"Line 158: {lines[157][:80]}")


