import codecs

file_path = 'front(2)/front(1)/src/views/teacher/StudentAnalysis.vue'

# 尝试用不同的编码读取
encodings = ['utf-8', 'gbk', 'gb2312', 'gb18030']

content = None
for enc in encodings:
    try:
        with open(file_path, 'r', encoding=enc) as f:
            content = f.read()
        print(f"Successfully read with encoding: {enc}")
        break
    except:
        continue

if content is None:
    print("Failed to read file with any encoding")
    exit(1)

# 保存为 UTF-8
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("File re-encoded to UTF-8")

# 验证
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
    print(f"\nLine 157: {lines[156].strip()}")
    print(f"Line 158: {lines[157].strip()}")


