@echo off
chcp 65001 >nul
echo =============================================
echo 智能教学管理系统 - 添加展示数据
echo =============================================
echo.

:: 设置数据库连接信息
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=web_experiment
set DB_USER=root

:: 提示输入密码
set /p DB_PASSWORD=请输入MySQL root密码: 

echo.
echo 正在添加展示数据...
echo.

:: 导入展示数据
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < add_display_data.sql

if %errorlevel% equ 0 (
    echo.
    echo =============================================
    echo ✅ 展示数据添加成功！
    echo =============================================
    echo.
    echo 📊 已添加的数据：
    echo    - 教学效率趋势数据（月度+周度）
    echo    - 更多学生选课记录
    echo    - 学生学习进度数据
    echo    - 学生考试成绩记录
    echo    - 学生练习完成记录
    echo    - 课程通知信息
    echo    - 错题记录数据
    echo    - 学生笔记数据
    echo.
    echo 🎯 现在可以：
    echo    1. 刷新教师工作台 - 查看折线图
    echo    2. 查看学生数据 - 学习进度、成绩等
    echo    3. 查看课程通知 - 最新通知信息
    echo    4. 测试所有功能 - 完整的演示数据
    echo.
    echo 💡 提示：
    echo    - 折线图会显示最近6个月的教学效率趋势
    echo    - 学生工作台会显示学习进度和成绩
    echo    - 所有数据都是基于您现有的数据库结构
    echo.
) else (
    echo.
    echo =============================================
    echo ❌ 展示数据添加失败！
    echo =============================================
    echo.
    echo 可能的原因：
    echo    1. MySQL密码错误
    echo    2. 数据库不存在
    echo    3. MySQL服务未启动
    echo.
    echo 解决方法：
    echo    1. 检查MySQL密码是否正确
    echo    2. 确保 web_experiment 数据库已创建
    echo    3. 启动MySQL服务
    echo.
)

pause

