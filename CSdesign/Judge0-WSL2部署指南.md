# Judge0 在 WSL2 中的部署指南

## 为什么需要 WSL2？

Judge0 依赖 Linux 的 cgroup 和 isolate 沙箱技术，这些在 Windows Docker Desktop 中无法正常工作。WSL2 提供了一个完整的 Linux 内核环境，可以完美运行 Judge0。

---

## 步骤 1：安装和配置 WSL2

### 1.1 启用 WSL2

打开 PowerShell（管理员权限），运行：

```powershell
# 启用 WSL
wsl --install

# 或者如果已经安装了 WSL1，升级到 WSL2
wsl --set-default-version 2
```

### 1.2 安装 Ubuntu

```powershell
# 安装 Ubuntu 22.04（推荐）
wsl --install -d Ubuntu-22.04
```

安装完成后，设置 Ubuntu 的用户名和密码。

### 1.3 验证 WSL2

```powershell
# 查看已安装的 Linux 发行版
wsl --list --verbose

# 确保 VERSION 列显示为 2
```

---

## 步骤 2：在 WSL2 中安装 Docker

### 2.1 进入 WSL2

```powershell
wsl
```

### 2.2 更新系统

```bash
sudo apt update
sudo apt upgrade -y
```

### 2.3 安装 Docker

```bash
# 安装必要的包
sudo apt install -y ca-certificates curl gnupg lsb-release

# 添加 Docker 官方 GPG 密钥
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# 设置 Docker 仓库
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 安装 Docker Engine
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 启动 Docker 服务
sudo service docker start

# 将当前用户添加到 docker 组（避免每次都用 sudo）
sudo usermod -aG docker $USER

# 重新登录以使组权限生效
exit
```

重新进入 WSL2：

```powershell
wsl
```

### 2.4 验证 Docker 安装

```bash
docker --version
docker compose version
docker ps
```

---

## 步骤 3：部署 Judge0

### 3.1 创建 Judge0 目录

```bash
cd ~
mkdir -p judge0
cd judge0
```

### 3.2 下载 Judge0 配置文件

```bash
# 下载官方配置
wget https://github.com/judge0/judge0/releases/download/v1.13.0/judge0-v1.13.0.zip
unzip judge0-v1.13.0.zip
cd judge0-v1.13.0
```

### 3.3 配置 Judge0

编辑 `judge0.conf` 文件：

```bash
nano judge0.conf
```

找到以下配置并修改：

```conf
# Redis 密码（必须设置）
REDIS_PASSWORD=YourStrongPassword123

# PostgreSQL 配置
POSTGRES_PASSWORD=YourStrongPassword123
POSTGRES_DB=judge0
POSTGRES_USER=judge0

# 其他配置保持默认即可
```

保存并退出（Ctrl+X，然后 Y，然后 Enter）。

### 3.4 启动 Judge0

```bash
# 启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f
```

等待 30-60 秒让所有服务完全启动。

### 3.5 测试 Judge0

```bash
# 测试 API 是否可访问
curl http://localhost:2358/about

# 提交一个简单的测试
curl -X POST http://localhost:2358/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "source_code": "print(\"Hello, Judge0!\")",
    "language_id": 71,
    "stdin": ""
  }'
```

如果返回包含 `token` 的 JSON，说明 Judge0 运行成功！

---

## 步骤 4：配置网络访问

### 4.1 获取 WSL2 的 IP 地址

在 WSL2 中运行：

```bash
hostname -I | awk '{print $1}'
```

记下这个 IP 地址，例如：`172.18.240.1`

### 4.2 配置端口转发（可选）

如果你想从 Windows 主机访问 WSL2 中的 Judge0，可以配置端口转发。

在 Windows PowerShell（管理员权限）中运行：

```powershell
# 获取 WSL2 IP
$wslIP = (wsl hostname -I).Trim()

# 添加端口转发规则
netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP

# 查看端口转发规则
netsh interface portproxy show all
```

现在你可以在 Windows 上通过 `http://localhost:2358` 访问 Judge0 了！

### 4.3 配置防火墙（如果需要）

```powershell
# 允许 2358 端口入站
New-NetFirewallRule -DisplayName "Judge0" -Direction Inbound -LocalPort 2358 -Protocol TCP -Action Allow
```

---

## 步骤 5：配置后端连接

### 5.1 修改 Spring Boot 配置

编辑 `application-judge0.properties`：

```properties
# Judge0 配置
# 如果配置了端口转发，使用 localhost
judge0.url=http://localhost:2358

# 或者直接使用 WSL2 的 IP（需要替换为实际 IP）
# judge0.url=http://172.18.240.1:2358
```

### 5.2 重启后端服务

重启你的 Spring Boot 应用，Judge0 应该可以正常工作了！

---

## 步骤 6：管理 Judge0 服务

### 6.1 常用命令

```bash
# 进入 WSL2
wsl

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

# 完全删除服务（包括数据）
docker compose down -v
```

### 6.2 设置开机自启动

创建一个启动脚本：

```bash
# 在 WSL2 中创建启动脚本
nano ~/start-judge0.sh
```

添加以下内容：

```bash
#!/bin/bash
cd ~/judge0/judge0-v1.13.0
sudo service docker start
sleep 5
docker compose up -d
```

保存后设置权限：

```bash
chmod +x ~/start-judge0.sh
```

在 Windows 中创建一个快捷方式，目标为：

```
wsl -d Ubuntu-22.04 -u your_username ~/start-judge0.sh
```

---

## 故障排查

### 问题 1：Docker 服务未启动

```bash
sudo service docker start
sudo service docker status
```

### 问题 2：端口被占用

```bash
# 查看端口占用
sudo lsof -i :2358

# 或者
sudo netstat -tulpn | grep 2358
```

### 问题 3：容器无法启动

```bash
# 查看详细日志
docker compose logs server
docker compose logs workers

# 重新构建并启动
docker compose down -v
docker compose up -d
```

### 问题 4：从 Windows 无法访问

```bash
# 在 WSL2 中测试
curl http://localhost:2358/about

# 如果 WSL2 内部可以访问，但 Windows 不行，重新配置端口转发
```

在 Windows PowerShell（管理员）中：

```powershell
# 删除旧规则
netsh interface portproxy delete v4tov4 listenport=2358 listenaddress=0.0.0.0

# 重新添加
$wslIP = (wsl hostname -I).Trim()
netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP
```

---

## 性能优化

### 1. 增加 WSL2 内存限制

创建 `.wslconfig` 文件：

```powershell
# 在 Windows 用户目录下创建 .wslconfig
notepad $env:USERPROFILE\.wslconfig
```

添加以下内容：

```ini
[wsl2]
memory=4GB
processors=2
swap=2GB
```

保存后重启 WSL2：

```powershell
wsl --shutdown
wsl
```

### 2. 优化 Docker 配置

在 WSL2 中编辑 Docker 配置：

```bash
sudo nano /etc/docker/daemon.json
```

添加：

```json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

重启 Docker：

```bash
sudo service docker restart
```

---

## 总结

✅ **优点：**
- 完整的 Linux 环境，Judge0 功能完全正常
- 性能接近原生 Linux
- 可以同时运行其他 Linux 服务

❌ **缺点：**
- 需要额外配置 WSL2
- 占用更多系统资源
- 需要手动启动服务

🎯 **推荐场景：**
- 本地开发和测试
- 不想依赖外部 API
- 需要完全控制 Judge0 配置

---

## 下一步

部署完成后，使用 `judge0-test.html` 测试 Judge0 是否正常工作！

如果遇到问题，查看日志：

```bash
cd ~/judge0/judge0-v1.13.0
docker compose logs -f
```
