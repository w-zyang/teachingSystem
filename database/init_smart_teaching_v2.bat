@echo off
chcp 65001 >nul
echo ========================================
echo   智能备课助手 V2.0 数据库初始化
echo   精简版 - 避免功能冲突
echo ========================================
echo.

set /p password=请输入MySQL root密码: 

echo.
echo 正在导入数据库...
mysql -u root -p%password% web_experiment < ai_teaching_assistant_v2.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   ✅ 数据库初始化成功！
    echo ========================================
    echo.
    echo 已创建以下表：
    echo   1. courseware_update - 课件更新记录表
    echo   2. experiment_guide_generation - 实验指导书生成记录表
    echo   3. homework_grading - 作业批阅记录表
    echo.
    echo 已插入测试数据。
    echo.
) else (
    echo.
    echo ========================================
    echo   ❌ 数据库初始化失败！
    echo ========================================
    echo.
    echo 请检查：
    echo   1. MySQL服务是否启动
    echo   2. 密码是否正确
    echo   3. web_experiment数据库是否存在
    echo.
)

pause

