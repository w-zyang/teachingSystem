import chardet

file_path = 'front(2)/front(1)/src/views/teacher/StudentAnalysis.vue'

# 读取文件并检测编码
with open(file_path, 'rb') as f:
    raw_data = f.read()

# 检测编码
detected = chardet.detect(raw_data)
print(f"Detected encoding: {detected}")

# 用检测到的编码读取
try:
    content = raw_data.decode(detected['encoding'])
    print(f"Successfully decoded with {detected['encoding']}")
except:
    # 如果失败，尝试 GBK
    try:
        content = raw_data.decode('gbk')
        print("Successfully decoded with GBK")
    except:
        content = raw_data.decode('utf-8', errors='replace')
        print("Decoded with UTF-8 (replacing errors)")

# 保存为 UTF-8
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"\nFile re-saved as UTF-8")
print(f"Total characters: {len(content)}")

# 检查几行
lines = content.split('\n')
print(f"\nLine 157 (first 100 chars): {lines[156][:100] if len(lines) > 156 else 'N/A'}")
print(f"Line 206 (first 100 chars): {lines[205][:100] if len(lines) > 205 else 'N/A'}")










