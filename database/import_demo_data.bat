@echo off
chcp 65001 >nul
echo =============================================
echo 智能教学管理系统 - 演示数据导入工具
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
echo 正在导入演示数据...
echo.

:: 导入演示数据
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < demo_data.sql

if %errorlevel% equ 0 (
    echo.
    echo =============================================
    echo ✅ 演示数据导入成功！
    echo =============================================
    echo.
    echo 📊 数据统计：
    echo    - 用户总数: 36 (管理员:1, 教师:5, 学生:30)
    echo    - 课程总数: 8门
    echo    - 章节总数: 15个
    echo    - 课时总数: 10个
    echo    - 考试总数: 5场
    echo    - 练习总数: 5个
    echo    - 课程资源: 5个
    echo    - 课程通知: 5条
    echo.
    echo 🔑 测试账号：
    echo    管理员: admin / 123456
    echo    教师: teacher1~5 / 123456
    echo    学生: student1~30 / 123456
    echo.
    echo 🚀 下一步：
    echo    1. 启动后端: cd ..\back ^&^& mvn spring-boot:run
    echo    2. 启动前端: cd ..\front(2)\front(1) ^&^& npm run dev
    echo    3. 访问系统: http://localhost:5173
    echo.
) else (
    echo.
    echo =============================================
    echo ❌ 演示数据导入失败！
    echo =============================================
    echo.
    echo 可能的原因：
    echo    1. MySQL密码错误
    echo    2. 数据库不存在（请先运行 init_database.bat）
    echo    3. MySQL服务未启动
    echo.
    echo 解决方法：
    echo    1. 检查MySQL密码是否正确
    echo    2. 确保已创建 web_experiment 数据库
    echo    3. 启动MySQL服务
    echo.
)

pause


