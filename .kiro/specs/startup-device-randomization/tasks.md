# Implementation Plan: Startup Device Randomization

## Overview

实现应用启动时自动随机生成设备信息的功能。主要工作包括扩展配置类、创建启动初始化器、添加测试。

## Tasks

- [x] 1. 扩展 FQApiProperties 配置类
  - [x] 1.1 添加 randomize-on-startup 配置属性
    - 在 FQApiProperties 类中添加 `private boolean randomizeOnStartup = true;`
    - 添加对应的 getter/setter（Lombok @Data 自动生成）
    - _Requirements: 2.1_
  - [x] 1.2 添加 updateFromDeviceInfo 方法
    - 创建方法接收 DeviceInfo 参数
    - 更新 userAgent、cookie 和 device 内部类的所有字段
    - _Requirements: 1.2_

- [x] 2. 创建 DeviceRandomizationInitializer 启动初始化器
  - [x] 2.1 创建初始化器类结构
    - 创建 `src/main/java/com/anjia/unidbgserver/config/DeviceRandomizationInitializer.java`
    - 实现 ApplicationRunner 接口
    - 注入 FQApiProperties 和 DeviceGeneratorService
    - 使用 @Order(1) 确保优先执行
    - _Requirements: 1.4_
  - [x] 2.2 实现 run 方法核心逻辑
    - 检查 randomizeOnStartup 配置
    - 调用 DeviceGeneratorService.generateDeviceInfo() 生成设备信息
    - 调用 FQApiProperties.updateFromDeviceInfo() 更新配置
    - _Requirements: 1.1, 2.2, 2.3_
  - [x] 2.3 实现错误处理和日志记录
    - 捕获设备生成异常，记录 ERROR 日志并回退到默认配置
    - 成功时记录 INFO 日志（设备品牌、型号、ID）
    - 跳过随机化时记录 INFO 日志说明使用默认配置
    - _Requirements: 1.3, 3.1, 3.2, 3.3_

- [x] 3. 更新 application.yml 配置文件
  - [x] 3.1 添加 randomize-on-startup 配置项
    - 在 fq.api 节点下添加 `randomize-on-startup: true`
    - 添加配置说明注释
    - _Requirements: 2.1_

- [x] 4. Checkpoint - 验证基础功能
  - 确保应用能正常启动
  - 检查启动日志中是否显示随机生成的设备信息
  - 验证设置 randomize-on-startup: false 时使用默认配置

- [x] 5. 添加单元测试

  - [x] 5.1 创建 DeviceRandomizationInitializer 单元测试

    - 测试启用随机化时正确调用 DeviceGeneratorService
    - 测试禁用随机化时跳过设备生成
    - 测试设备生成失败时的回退行为
    - _Requirements: 1.1, 1.3, 2.2, 2.3_
  - [x] 5.2 创建 FQApiProperties.updateFromDeviceInfo 单元测试

    - 测试所有字段正确更新
    - 测试部分字段为 null 时的处理
    - _Requirements: 1.2_

- [x] 6. 添加属性测试

  - [x] 6.1 添加 jqwik 依赖到 pom.xml

    - 添加 net.jqwik:jqwik 测试依赖
    - _Requirements: Testing Strategy_
  - [x] 6.2 编写 Property 3 测试：Valid Brand-Model Pairs

    - **Property 3: Valid Brand-Model Pairs**
    - *For any* generated DeviceInfo, deviceBrand SHALL be one of predefined brands AND deviceType SHALL be valid for that brand
    - **Validates: Requirements 4.1, 4.2**
  - [x] 6.3 编写 Property 4 测试：User-Agent and Cookie Consistency

    - **Property 4: User-Agent and Cookie Consistency**
    - *For any* generated DeviceInfo, userAgent SHALL contain deviceType AND cookie SHALL contain installId
    - **Validates: Requirements 4.3**

- [x] 7. Final Checkpoint - 确保所有测试通过
  - 运行所有单元测试和属性测试 ✅
  - 确保代码编译无错误 ✅
  - 验证功能完整性 ✅

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- 本功能复用现有的 DeviceGeneratorService，无需修改其逻辑
- 设备信息在内存中更新，不会修改 application.yml 文件
- Property 1 和 Property 2 需要集成测试环境，暂不在单元测试中实现
