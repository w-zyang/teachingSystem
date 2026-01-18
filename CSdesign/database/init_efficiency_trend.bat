@echo off
chcp 65001 >nul
echo ====================================
echo 教学效率趋势表初始化脚本
echo ====================================
echo.

set MYSQL_HOST=10.76.221.124
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=123456
set MYSQL_DATABASE=web_experiment

echo 正在连接数据库...
echo 主机: %MYSQL_HOST%:%MYSQL_PORT%
echo 数据库: %MYSQL_DATABASE%
echo.

mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < teacher_efficiency_trend.sql

if %errorlevel% equ 0 (
    echo.
    echo ====================================
    echo 数据库初始化成功！
    echo ====================================
    echo.
    echo 已创建表: teacher_efficiency_trend
    echo 已插入测试数据:
    echo   - 教师ID=2 的6个月数据
    echo   - 教师ID=2 的7天数据
    echo   - 其他教师的部分数据
    echo.
) else (
    echo.
    echo ====================================
    echo 数据库初始化失败！
    echo ====================================
    echo.
    echo 请检查:
    echo 1. MySQL是否已安装并在PATH中
    echo 2. 数据库连接信息是否正确
    echo 3. SQL文件是否存在
    echo.
)

pause


