# WSL2 Judge0 快速部署指南

## 🚀 当前状态

✅ WSL2 已安装（Ubuntu）  
✅ 用户已创建：wangziyang  
✅ 部署脚本已准备好  
✅ 后端配置已更新  

---

## 📋 部署步骤（只需 3 步）

### 步骤 1：在 WSL2 中运行部署脚本

在你的 WSL2 终端中（`wangziyang@wzy:/mnt/c/Users/18037$`），复制粘贴以下命令：

```bash
# 复制脚本到 WSL2
cp "/mnt/c/Users/18037/Downloads/CSdesign (1)/CSdesign/deploy-judge0.sh" ~/deploy-judge0.sh

# 设置执行权限
chmod +x ~/deploy-judge0.sh

# 运行部署脚本
~/deploy-judge0.sh
```

**预计时间：5-10 分钟**

脚本会自动完成：
- 更新系统
- 安装 Docker
- 下载并配置 Judge0
- 启动所有服务

---

### 步骤 2：配置端口转发

部署完成后，**退出 WSL2**：

```bash
exit
```

然后**以管理员身份打开 PowerShell**，运行：

```powershell
# 获取 WSL2 IP 地址
$wslIP = (wsl hostname -I).Trim()
Write-Host "WSL2 IP: $wslIP"

# 添加端口转发
netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP

# 验证配置
netsh interface portproxy show all

Write-Host "✓ 端口转发配置完成！"
```

---

### 步骤 3：测试 Judge0

在浏览器中打开：

```
http://localhost:2358/about
```

如果看到 JSON 响应，说明部署成功！✅

或者在 PowerShell 中测试：

```powershell
curl http://localhost:2358/about
```

---

## 🎯 启动后端并测试

1. **启动 Spring Boot 后端**（在 IDEA 中运行）

2. **打开测试页面**：
   ```
   CSdesign/judge0-test.html
   ```

3. **点击"测试运行代码"按钮**

4. **查看结果**：
   - 如果显示 "Hello, World!"，说明一切正常！✅

---

## 🔧 常用命令

### 在 WSL2 中管理 Judge0

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

# 完全删除并重新部署
docker compose down -v
docker compose up -d
```

### 在 Windows 中管理端口转发

```powershell
# 查看所有端口转发规则
netsh interface portproxy show all

# 删除端口转发规则
netsh interface portproxy delete v4tov4 listenport=2358 listenaddress=0.0.0.0

# 重新添加端口转发（WSL2 重启后需要）
$wslIP = (wsl hostname -I).Trim()
netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP
```

---

## ⚠️ 常见问题

### Q1: WSL2 重启后无法访问 Judge0

**原因**：WSL2 的 IP 地址会变化

**解决方案**：重新配置端口转发

```powershell
# 删除旧规则
netsh interface portproxy delete v4tov4 listenport=2358 listenaddress=0.0.0.0

# 添加新规则
$wslIP = (wsl hostname -I).Trim()
netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=$wslIP
```

### Q2: Docker 服务未启动

**解决方案**：

```bash
# 在 WSL2 中启动 Docker
sudo service docker start

# 检查状态
sudo service docker status
```

### Q3: 端口 2358 被占用

**解决方案**：

```powershell
# 查看占用端口的进程
netstat -ano | findstr :2358

# 结束进程（替换 PID）
taskkill /PID <进程ID> /F
```

### Q4: 权限错误

**解决方案**：

```bash
# 在 WSL2 中，将用户添加到 docker 组
sudo usermod -aG docker $USER

# 退出并重新进入 WSL2
exit
wsl
```

---

## 🎓 Judge0 服务架构

Judge0 在 WSL2 中运行以下容器：

| 容器 | 端口 | 说明 |
|------|------|------|
| judge0-server | 2358 | API 服务器 |
| judge0-workers | - | 代码执行工作器 |
| judge0-db | 5432 | PostgreSQL 数据库 |
| judge0-redis | 6379 | Redis 缓存 |

---

## 📊 性能优化

### 增加工作器数量（提高并发）

编辑 `~/judge0/judge0-v1.13.0/docker-compose.yml`：

```yaml
judge0-workers:
  # ...
  deploy:
    replicas: 4  # 默认是 2，可以增加到 4 或更多
```

重启服务：

```bash
cd ~/judge0/judge0-v1.13.0
docker compose up -d --scale judge0-workers=4
```

---

## 🔒 安全建议

1. **不要暴露到公网**：Judge0 默认没有认证，仅用于本地开发

2. **生产环境**：
   - 添加 Nginx 反向代理
   - 配置 HTTPS
   - 添加认证机制
   - 限制请求频率

3. **防火墙**：
   ```powershell
   # 仅允许本地访问
   New-NetFirewallRule -DisplayName "Judge0 Local" -Direction Inbound -LocalPort 2358 -Protocol TCP -Action Allow -RemoteAddress LocalSubnet
   ```

---

## 📈 监控和日志

### 查看实时日志

```bash
cd ~/judge0/judge0-v1.13.0

# 所有服务的日志
docker compose logs -f

# 仅查看 API 服务器日志
docker compose logs -f judge0-server

# 仅查看工作器日志
docker compose logs -f judge0-workers
```

### 查看资源使用

```bash
# 查看容器资源使用情况
docker stats
```

---

## 🎉 部署完成检查清单

- [ ] WSL2 中 Judge0 服务正常运行
- [ ] `http://localhost:2358/about` 可以访问
- [ ] 端口转发配置成功
- [ ] 后端配置文件已更新
- [ ] 测试页面可以成功执行代码
- [ ] 可以看到代码执行结果

全部完成后，你就可以开始使用 Judge0 进行代码评测了！🚀

---

## 📚 相关文档

- [Judge0-WSL2部署指南.md](./Judge0-WSL2部署指南.md) - 详细的部署文档
- [Judge0官方API使用指南.md](./Judge0官方API使用指南.md) - API 使用说明
- Judge0 官方文档：https://ce.judge0.com/

---

## 🆘 需要帮助？

如果遇到问题：

1. 检查 Docker 服务是否运行：`sudo service docker status`
2. 查看容器状态：`docker compose ps`
3. 查看日志：`docker compose logs -f`
4. 重启服务：`docker compose restart`

祝你部署顺利！✨
