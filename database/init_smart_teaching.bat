@echo off
chcp 65001 >nul
echo =============================================
echo    智能备课助手 - 数据库初始化
echo =============================================
echo.

set /p password=请输入MySQL root密码: 

echo.
echo 正在初始化数据库表...
echo.

mysql -u root -p%password% web_experiment < ai_teaching_assistant.sql

if %errorlevel% equ 0 (
    echo.
    echo =============================================
    echo ✅ 数据库初始化成功！
    echo =============================================
    echo.
    echo 已创建以下数据表：
    echo   1. ai_ppt_generation - AI生成PPT记录表
    echo   2. courseware_update - 课件更新记录表
    echo   3. teaching_resource_package - 教学资源包表
    echo   4. homework_grading - 作业批阅记录表
    echo   5. ai_teacher_tutoring - AI教师辅导记录表
    echo.
    echo 已插入测试数据：
    echo   - 2条PPT生成记录
    echo   - 2条教学资源包记录
    echo.
) else (
    echo.
    echo =============================================
    echo ❌ 数据库初始化失败！
    echo =============================================
    echo.
    echo 可能的原因：
    echo    1. MySQL密码错误
    echo    2. 数据库不存在
    echo    3. MySQL服务未启动
    echo.
)

pause

