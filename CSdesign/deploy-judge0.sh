#!/bin/bash

echo "=========================================="
echo "Judge0 WSL2 自动部署脚本"
echo "=========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 步骤 1: 更新系统
echo -e "${YELLOW}[1/6] 更新系统包...${NC}"
sudo apt update
sudo apt upgrade -y

# 步骤 2: 安装 Docker
echo -e "${YELLOW}[2/6] 安装 Docker...${NC}"

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
echo -e "${YELLOW}启动 Docker 服务...${NC}"
sudo service docker start

# 将当前用户添加到 docker 组
sudo usermod -aG docker $USER

echo -e "${GREEN}✓ Docker 安装完成${NC}"

# 步骤 3: 下载 Judge0
echo -e "${YELLOW}[3/6] 下载 Judge0...${NC}"
cd ~
mkdir -p judge0
cd judge0

# 下载 Judge0 配置文件
wget https://github.com/judge0/judge0/releases/download/v1.13.0/judge0-v1.13.0.zip
unzip -o judge0-v1.13.0.zip
cd judge0-v1.13.0

echo -e "${GREEN}✓ Judge0 下载完成${NC}"

# 步骤 4: 配置 Judge0
echo -e "${YELLOW}[4/6] 配置 Judge0...${NC}"

# 生成随机密码
REDIS_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-25)
POSTGRES_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-25)

# 更新配置文件
sed -i "s/REDIS_PASSWORD=YourPasswordHere1234/REDIS_PASSWORD=$REDIS_PASSWORD/" judge0.conf
sed -i "s/POSTGRES_PASSWORD=YourPasswordHere1234/POSTGRES_PASSWORD=$POSTGRES_PASSWORD/" judge0.conf

echo -e "${GREEN}✓ Judge0 配置完成${NC}"
echo -e "Redis 密码: $REDIS_PASSWORD"
echo -e "PostgreSQL 密码: $POSTGRES_PASSWORD"

# 步骤 5: 启动 Judge0
echo -e "${YELLOW}[5/6] 启动 Judge0 服务...${NC}"
sudo docker compose up -d

echo -e "${GREEN}✓ Judge0 服务已启动${NC}"

# 步骤 6: 等待服务启动
echo -e "${YELLOW}[6/6] 等待服务完全启动（约 30 秒）...${NC}"
sleep 30

# 测试 Judge0
echo -e "${YELLOW}测试 Judge0 API...${NC}"
curl -s http://localhost:2358/about | head -n 5

echo ""
echo "=========================================="
echo -e "${GREEN}✓ Judge0 部署完成！${NC}"
echo "=========================================="
echo ""
echo "Judge0 API 地址: http://localhost:2358"
echo ""
echo "常用命令:"
echo "  查看服务状态: cd ~/judge0/judge0-v1.13.0 && docker compose ps"
echo "  查看日志: cd ~/judge0/judge0-v1.13.0 && docker compose logs -f"
echo "  重启服务: cd ~/judge0/judge0-v1.13.0 && docker compose restart"
echo "  停止服务: cd ~/judge0/judge0-v1.13.0 && docker compose stop"
echo ""
echo "下一步:"
echo "1. 退出 WSL2: exit"
echo "2. 重新进入 WSL2: wsl"
echo "3. 配置端口转发（在 Windows PowerShell 管理员模式下运行）:"
echo "   \$wslIP = (wsl hostname -I).Trim()"
echo "   netsh interface portproxy add v4tov4 listenport=2358 listenaddress=0.0.0.0 connectport=2358 connectaddress=\$wslIP"
echo ""
