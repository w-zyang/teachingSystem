# Judge0 WSL2 手动部署流程

## 📋 完整手动操作步骤

### 前置条件

✅ WSL2 已安装  
✅ Ubuntu 已安装并启动  
✅ 当前用户：wangziyang  

---

## 第一部分：在 WSL2 中部署 Judge0

### 步骤 1：更新系统

在 WSL2 终端中运行：

```bash
sudo apt update
sudo apt upgrade -y
```

**说明**：更新系统包，确保所有软件都是最新版本  
**预计时间**：2-3 分钟

---

### 步骤 2：安装 Docker

#### 2.1 安装必要的依赖包

```bash
sudo apt install -y ca-certificates curl gnupg lsb-release
```

#### 2.2 添加 Docker 官方 GPG 密钥

```bash
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
```

#### 2.3 设置 Docker 仓库

```bash
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

#### 2.4 安装 Docker Engine

```bash
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
```

**预计时间**：3-5 分钟

#### 2.5 启动 Docker 服务

```bash
sudo service docker start
```

#### 2.6 验证 Docker 安装

```bash
sudo docker --version
sudo docker compose version
```

**预期输出**：
```
Docker version 24.x.x
Docker Compose version v2.x.x
```

#### 2.7 将当前用户添加到 docker 组（避免每次都用 sudo）

```bash
sudo usermod -aG docker $USER
```

**注意**：添加后需要退出并重新登录 WSL2 才能生效

```bash
exit
```

然后重新进入 WSL2：

```bash
wsl
```

---

### 步骤 3：下载 Judge0

#### 3.1 创建工作目录

```bash
cd ~
mkdir -p judge0
cd judge0
```

#### 3.2 下载 Judge0 官方发布包

```bash
wget https://github.com/judge0/judge0/releases/download/v1.13.0/judge0-v1.13.0.zip
```

**如果 wget 未安装**，先安装：

```bash
sudo apt install -y wget unzip
```

#### 3.3 解压文件

```bash
unzip judge0-v1.13.0.zip
cd judge0-v1.13.0
```

#### 3.4 查看文件结构

```bash
ls -la
```

**预期输出**：
```
docker-compose.yml
judge0.conf
...
```

**预计时间**：1-2 分钟

---

### 步骤 4：配置 Judge0

#### 4.1 生成随机密码

```bash
# 生成 Redis 密码
REDIS_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-25)
echo "Redis 密码: $REDIS_PASSWORD"

# 生成 PostgreSQL 密码
POSTGRES_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-25)
echo "PostgreSQL 密码: $POSTGRES_PASSWORD"
```

**记下这两个密码**（虽然一般不需要手动使用）

#### 4.2 编辑配置文件

```bash
nano judge0.conf
```

**或者使用 vim**：

```bash
vim judge0.conf
```

**找到以下两行并替换密码**：

```bash
# 原始内容：
REDIS_PASSWORD=YourPasswordHere1234
POSTGRES_PASSWORD=YourPasswordHere1234

# 替换为（使用上面生成的密码）：
REDIS_PASSWORD=你生成的Redis密码
POSTGRES_PASSWORD=你生成的PostgreSQL密码
```

**保存并退出**：
- nano：按 `Ctrl+X`，然后按 `Y`，再按 `Enter`
- vim：按 `Esc`，输入 `:wq`，按 `Enter`

#### 4.3 或者使用 sed 命令自动替换

```bash
sed -i "s/REDIS_PASSWORD=YourPasswordHere1234/REDIS_PASSWORD=$REDIS_PASSWORD/" judge0.conf
sed -i "s/POSTGRES_PASSWORD=YourPasswordHere1234/POSTGRES_PASSWORD=$POSTGRES_PASSWORD/" judge0.conf
```

#### 4.4 验证配置

```bash
grep "PASSWORD" judge0.conf
```

**预期输出**：应该看到你设置的密码

---

### 步骤 5：启动 Judge0

#### 5.1 启动所有服务

```bash
docker compose up -d
```

**说明**：
- `up`：启动服务
- `-d`：后台运行（detached mode）

**预计时间**：3-5 分钟（首次启动需要下载镜像）

#### 5.2 查看服务状态

```bash
docker compose ps
```

**预期输出**：所有服务的 STATUS 应该是 `Up` 或 `healthy`

```
NAME                    STATUS
judge0-server           Up (healthy)
judge0-workers          Up
judge0-db               Up (healthy)
judge0-redis            Up (healthy)
```

#### 5.3 查看日志（可选）

```bash
# 查看所有服务日志
docker compose logs

# 实时查看日志
docker compose logs -f

# 仅查看 server 日志
docker compose logs judge0-server
```

**按 `Ctrl+C` 退出日志查看**

---

### 步骤 6：测试 Judge0 API

#### 6.1 等待服务完全启动

```bash
sleep 30
```

**说明**：等待 30 秒让所有服务完全启动

#### 6.2 测试 API

```bash
curl http://localhost:2358/about
```

**预期输出**：应该看到 JSON 格式的响应，包含 Judge0 版本信息

```json
{
  "version": "1.13.0",
  ...
}
```

#### 6.3 测试语言列表

```bash
curl http://localhost:2358/languages
```

**预期输出**：应该看到支持的编程语言列表

---

## 第二部分：配置 Windows 端口转发

### 步骤 7：退出 WSL2

在 WSL2 终端中：

```bash
exit
```

---

### 步骤 8：以管理员身份打开 PowerShell

1. 按 `Win + X`
2. 选择 **"Windows PowerShell (管理员)"** 或 **"终端 (管理员)"**
3. 如果弹出 UAC 提示，点击 **"是"**

---

### 步骤 9：配置端口转发

#### 9.1 获取 WSL2 的 IP 地址

在 PowerShell 中运行：

```powershell
wsl hostname -I
```

**预期输出**：类似 `172.18.240.1` 的 IP 地址

#### 9.2 保存 IP 到变量

```powershell
$wslIP = (wsl hostname -I).Trim()
Write-Host "WSL2 IP 地址: $wslIP"
```

#### 9.3 添加端口转发规则

```powershell
netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP
```

**说明**：
- `listenport=2358`：Windows 监听的端口
- `listenaddress=0.0.0.0`：监听所有网络接口
- `connectport=2358`：WSL2 中 Judge0 的端口
- `connectaddress=$wslIP`：WSL2 的 IP 地址

#### 9.4 验证端口转发配置

```powershell
netsh interface portproxy show all
```

**预期输出**：

```
侦听 ipv4:                 连接到 ipv4:

地址            端口        地址            端口
--------------- ----------  --------------- ----------
0.0.0.0         2358        172.18.240.1    2358
```

---

### 步骤 10：测试从 Windows 访问 Judge0

#### 10.1 在 PowerShell 中测试

```powershell
curl http://localhost:2358/about
```

**或者使用 Invoke-WebRequest**：

```powershell
Invoke-WebRequest -Uri http://localhost:2358/about
```

#### 10.2 在浏览器中测试

打开浏览器，访问：

```
http://localhost:2358/about
```

**预期结果**：应该看到 JSON 响应

---

## 第三部分：配置并测试后端

### 步骤 11：确认后端配置

打开文件：`back/src/main/resources/application-judge0.properties`

**确认内容为**：

```properties
# Judge0 配置
# 使用本地 WSL2 中的 Judge0
judge0.url=http://localhost:2358
judge0.api.key=
```

**✅ 已经配置好了，无需修改**

---

### 步骤 12：启动 Spring Boot 后端

#### 12.1 在 IDEA 中启动

1. 打开 IDEA
2. 找到主类（通常是 `Application.java` 或 `ExperimentApplication.java`）
3. 点击绿色运行按钮 ▶️

#### 12.2 等待启动完成

查看控制台输出，等待看到：

```
Started ExperimentApplication in X.XXX seconds
```

---

### 步骤 13：测试代码执行

#### 13.1 打开测试页面

在浏览器中打开：

```
file:///C:/Users/18037/Downloads/CSdesign%20(1)/CSdesign/judge0-test.html
```

**或者直接双击打开** `judge0-test.html` 文件

#### 13.2 点击测试按钮

点击页面上的 **"测试运行代码"** 按钮

#### 13.3 查看结果

**预期结果**：

```
提交成功！Token: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
正在获取结果...
执行成功！
输出: Hello, World!
```

---

## 🎉 部署完成！

如果所有步骤都成功，恭喜你！Judge0 已经成功部署并运行了！

---

## 📝 常用管理命令

### WSL2 中管理 Judge0

```bash
# 进入 Judge0 目录
cd ~/judge0/judge0-v1.13.0

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f

# 重启服务
docker compose restart

# 停止服务
docker compose stop

# 启动服务
docker compose start

# 停止并删除所有容器
docker compose down

# 停止并删除所有容器和数据卷
docker compose down -v
```

### Windows 中管理端口转发

```powershell
# 查看所有端口转发规则
netsh interface portproxy show all

# 删除端口转发规则
netsh interface portproxy delete v4tov4 listenport=2358 listenaddress=0.0.0.0

# 重新添加端口转发（WSL2 重启后 IP 可能变化）
$wslIP = (wsl hostname -I).Trim()
netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP
```

### 启动和停止 WSL2

```powershell
# 启动 WSL2
wsl

# 关闭 WSL2
wsl --shutdown

# 重启 WSL2（先关闭再启动）
wsl --shutdown
wsl
```

---

## ⚠️ 重要提示

### 1. WSL2 重启后需要重新配置端口转发

每次 WSL2 重启后，IP 地址可能会变化，需要重新配置端口转发：

```powershell
# 删除旧规则
netsh interface portproxy delete v4tov4 listenport=2358 listenaddress=0.0.0.0

# 添加新规则
$wslIP = (wsl hostname -I).Trim()
netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP
```

### 2. Docker 服务需要手动启动

每次进入 WSL2 后，可能需要启动 Docker：

```bash
sudo service docker start
```

### 3. 自动启动 Docker（可选）

编辑 `~/.bashrc`：

```bash
nano ~/.bashrc
```

在文件末尾添加：

```bash
# 自动启动 Docker
if ! service docker status > /dev/null 2>&1; then
    sudo service docker start > /dev/null 2>&1
fi
```

保存后重新加载：

```bash
source ~/.bashrc
```

---

## 🔍 故障排除

### 问题 1：无法访问 http://localhost:2358

**检查步骤**：

1. 确认 WSL2 中 Judge0 正在运行：
   ```bash
   wsl
   cd ~/judge0/judge0-v1.13.0
   docker compose ps
   ```

2. 确认端口转发配置正确：
   ```powershell
   netsh interface portproxy show all
   ```

3. 重新配置端口转发：
   ```powershell
   netsh interface portproxy delete v4tov4 listenport=2358 listenaddress=0.0.0.0
   $wslIP = (wsl hostname -I).Trim()
   netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP
   ```

### 问题 2：Docker 命令需要 sudo

**解决方案**：

```bash
# 将用户添加到 docker 组
sudo usermod -aG docker $USER

# 退出并重新登录
exit
wsl
```

### 问题 3：容器启动失败

**查看日志**：

```bash
cd ~/judge0/judge0-v1.13.0
docker compose logs
```

**重新部署**：

```bash
docker compose down -v
docker compose up -d
```

### 问题 4：端口被占用

**查找占用端口的进程**：

```powershell
netstat -ano | findstr :2358
```

**结束进程**：

```powershell
taskkill /PID <进程ID> /F
```

---

## 📊 验证清单

部署完成后，请确认以下所有项目：

- [ ] WSL2 中 Docker 服务正常运行
- [ ] Judge0 所有容器状态为 `Up` 或 `healthy`
- [ ] 在 WSL2 中可以访问 `http://localhost:2358/about`
- [ ] Windows 端口转发配置成功
- [ ] 在 Windows 中可以访问 `http://localhost:2358/about`
- [ ] Spring Boot 后端启动成功
- [ ] 测试页面可以成功执行代码
- [ ] 可以看到代码执行结果

全部打勾后，部署就完全成功了！✅

---

## 🎯 下一步

现在你可以：

1. ✅ 在你的应用中集成 Judge0
2. ✅ 创建题目和测试用例
3. ✅ 实现在线代码评测功能
4. ✅ 构建完整的 OJ 系统

祝你开发顺利！🚀
