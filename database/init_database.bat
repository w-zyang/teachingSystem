@echo off
chcp 65001 >nul
echo ========================================
echo 智能学习管理系统 - 数据库初始化脚本
echo ========================================
echo.

REM 设置MySQL连接参数
set MYSQL_USER=root
set MYSQL_PASSWORD=123456
set MYSQL_HOST=localhost
set MYSQL_PORT=3306

REM 获取脚本所在目录
set SCRIPT_DIR=%~dp0
cd /d %SCRIPT_DIR%

echo [检查] MySQL服务状态...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] MySQL连接失败！
    echo.
    echo 请检查：
    echo 1. MySQL服务是否已启动
    echo    启动命令: net start mysql
    echo 2. 用户名和密码是否正确
    echo    当前配置: 用户名=%MYSQL_USER%, 密码=%MYSQL_PASSWORD%
    echo 3. MySQL是否在运行
    echo.
    echo 如需修改配置，请编辑本脚本中的 MYSQL_USER 和 MYSQL_PASSWORD 变量
    pause
    exit /b 1
)
echo [成功] MySQL连接正常
echo.

echo ========================================
echo 请选择初始化模式：
echo ========================================
echo 1. 完整数据库（包含所有测试数据）
echo     - 1个管理员
echo     - 15名教师
echo     - 100名学生
echo     - 20门课程
echo     - 完整的选课、考试、练习数据
echo     - 智能学习功能数据
echo.
echo 2. 简化数据库（基础数据）
echo     - 1个管理员
echo     - 1名教师
echo     - 1名学生
echo     - 基础表结构
echo.
set /p MODE="请选择模式 (1/2，默认1): "
if "%MODE%"=="" set MODE=1
if "%MODE%"=="2" goto SIMPLE_MODE

REM ========================================
REM 完整数据库初始化
REM ========================================
echo.
echo ========================================
echo 开始初始化完整数据库...
echo ========================================
echo.

echo [1/7] 执行基础表结构和用户数据...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "back\src\main\resources\sql\comprehensive_database_rebuild_part1.sql"
if %errorlevel% neq 0 (
    echo [错误] 第1步执行失败！
    pause
    exit /b 1
)
echo [成功] 基础表和用户数据创建完成

echo [2/7] 执行学生用户数据（100名学生）...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "back\src\main\resources\sql\comprehensive_database_rebuild_part2.sql"
if %errorlevel% neq 0 (
    echo [错误] 第2步执行失败！
    pause
    exit /b 1
)
echo [成功] 学生数据创建完成

echo [3/7] 执行课程、考试、练习数据...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "back\src\main\resources\sql\comprehensive_database_rebuild_part3.sql"
if %errorlevel% neq 0 (
    echo [错误] 第3步执行失败！
    pause
    exit /b 1
)
echo [成功] 课程和考试数据创建完成

echo [4/7] 执行选课、学习进度、资源数据...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "back\src\main\resources\sql\comprehensive_database_rebuild_part4.sql"
if %errorlevel% neq 0 (
    echo [错误] 第4步执行失败！
    pause
    exit /b 1
)
echo [成功] 选课和学习数据创建完成

echo [5/7] 执行智能学习功能数据...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "intelligent_features_database.sql"
if %errorlevel% neq 0 (
    echo [错误] 第5步执行失败！
    pause
    exit /b 1
)
echo [成功] 智能学习功能数据创建完成

echo [6/7] 执行课堂重点整理表...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "class_summary_table.sql"
if %errorlevel% neq 0 (
    echo [错误] 第6步执行失败！
    pause
    exit /b 1
)
echo [成功] 课堂重点整理表创建完成

echo [7/7] 执行学生笔记和标注表...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "student_notes_annotations.sql"
if %errorlevel% neq 0 (
    echo [错误] 第7步执行失败！
    pause
    exit /b 1
)
echo [成功] 笔记和标注表创建完成

goto VERIFY

REM ========================================
REM 简化数据库初始化
REM ========================================
:SIMPLE_MODE
echo.
echo ========================================
echo 开始初始化简化数据库...
echo ========================================
echo.

echo [1/1] 执行简化数据库脚本...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "simple_init.sql"
if %errorlevel% neq 0 (
    echo [错误] 数据库初始化失败！
    pause
    exit /b 1
)
echo [成功] 简化数据库创建完成

REM ========================================
REM 验证数据库
REM ========================================
:VERIFY
echo.
echo ========================================
echo 验证数据库初始化结果...
echo ========================================
echo.

mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "USE web_experiment; SELECT COUNT(*) as total_users FROM user; SELECT role, COUNT(*) as count FROM user GROUP BY role;" 2>nul
if %errorlevel% neq 0 (
    echo [警告] 无法验证数据库，但初始化可能已成功
) else (
    echo [成功] 数据库验证通过
)

echo.
echo ========================================
echo 数据库初始化完成！
echo ========================================
echo.
echo 测试账号信息：
echo   管理员: admin / 123456
echo   教师: teacher1 / 123456
echo   学生: student1 / 123456
echo.
echo 下一步：
echo   1. 启动后端服务: cd back ^&^& mvn spring-boot:run
echo   2. 启动前端服务: cd front(2)/front(1 ^&^& npm run dev
echo   3. 访问系统: http://localhost:5173
echo.
pause
