# Design Document: Startup Device Randomization

## Overview

本设计实现应用启动时自动随机生成设备信息的功能。通过创建一个 Spring Boot 启动初始化器，在应用启动完成后调用现有的 `DeviceGeneratorService` 生成随机设备信息，并将其注入到 `FQApiProperties` 配置类中。

这种方式的优势：
- 无需修改 application.yml 文件
- 每次启动自动获得新的设备指纹
- 可通过配置开关控制是否启用
- 复用现有的设备生成逻辑

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Startup                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              DeviceRandomizationInitializer                  │
│                  (ApplicationRunner)                         │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 1. Check randomize-on-startup flag                   │   │
│  │ 2. Generate random DeviceInfo                        │   │
│  │ 3. Update FQApiProperties                            │   │
│  │ 4. Log device information                            │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ DeviceGenerator │ │  FQApiProperties │ │     Logger      │
│    Service      │ │   (Updated)      │ │                 │
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

## Components and Interfaces

### 1. DeviceRandomizationInitializer

新建的启动初始化器组件，实现 `ApplicationRunner` 接口。

```java
@Component
@Order(1)  // 确保在其他初始化器之前执行
public class DeviceRandomizationInitializer implements ApplicationRunner {
    
    private final FQApiProperties fqApiProperties;
    private final DeviceGeneratorService deviceGeneratorService;
    
    @Override
    public void run(ApplicationArguments args) {
        // 1. 检查是否启用随机化
        // 2. 生成随机设备信息
        // 3. 更新配置属性
        // 4. 记录日志
    }
}
```

### 2. FQApiProperties 扩展

在现有配置类中添加随机化控制开关：

```java
@Data
@Component
@ConfigurationProperties(prefix = "fq.api")
public class FQApiProperties {
    // 现有字段...
    
    /**
     * 是否在启动时随机生成设备信息
     * 默认为 true
     */
    private boolean randomizeOnStartup = true;
    
    // 添加更新设备信息的方法
    public void updateFromDeviceInfo(DeviceInfo deviceInfo) {
        // 更新 userAgent, cookie, device 等字段
    }
}
```

### 3. DeviceGeneratorService 接口

复用现有服务，无需修改：

```java
public interface DeviceGeneratorService {
    DeviceInfo generateDeviceInfo(DeviceRegisterRequest request);
}
```

## Data Models

### DeviceInfo (现有)

```java
@Data
@Builder
public class DeviceInfo {
    private String deviceBrand;
    private String deviceType;
    private String deviceId;
    private String installId;
    private String cdid;
    private String resolution;
    private String dpi;
    private String hostAbi;
    private String romVersion;
    private String osVersion;
    private Integer osApi;
    private String userAgent;
    private String cookie;
    private String aid;
    private String versionCode;
    private String versionName;
    private String updateVersionCode;
}
```

### DeviceRegisterRequest (现有)

用于控制设备生成行为：

```java
@Data
public class DeviceRegisterRequest {
    private String deviceBrand;
    private String deviceType;
    private Boolean useRealAlgorithm;
    private Boolean useRealBrand = true;  // 默认使用真实品牌
    // ...
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Device Randomization on Startup

*For any* application startup with `randomize-on-startup` set to `true`, the `FQApiProperties.device` values after initialization SHALL differ from the default values defined in application.yml (specifically device-id and cdid which are randomly generated).

**Validates: Requirements 1.1, 2.3**

### Property 2: Device Info Injection Consistency

*For any* generated `DeviceInfo` object, after injection into `FQApiProperties`, the following fields SHALL match exactly:
- `FQApiProperties.device.deviceId` == `DeviceInfo.deviceId`
- `FQApiProperties.device.deviceBrand` == `DeviceInfo.deviceBrand`
- `FQApiProperties.device.deviceType` == `DeviceInfo.deviceType`
- `FQApiProperties.device.cdid` == `DeviceInfo.cdid`
- `FQApiProperties.device.installId` == `DeviceInfo.installId`

**Validates: Requirements 1.2**

### Property 3: Valid Brand-Model Pairs

*For any* generated `DeviceInfo`, the `deviceBrand` SHALL be one of the predefined brands (Xiaomi, HUAWEI, OPPO, vivo, OnePlus, Samsung), AND the `deviceType` SHALL be a valid model for that brand as defined in `DeviceGeneratorService.DEVICE_BRANDS`.

**Validates: Requirements 4.1, 4.2**

### Property 4: User-Agent and Cookie Consistency

*For any* generated `DeviceInfo`:
- The `userAgent` string SHALL contain the `deviceType` value
- The `cookie` string SHALL contain the `installId` value

**Validates: Requirements 4.3**

## Error Handling

### 设备生成失败

当 `DeviceGeneratorService.generateDeviceInfo()` 返回 `null` 或抛出异常时：

1. 记录 ERROR 级别日志，包含异常信息
2. 保留 `FQApiProperties` 中的默认配置（来自 application.yml）
3. 应用继续正常启动，不中断

```java
try {
    DeviceInfo deviceInfo = deviceGeneratorService.generateDeviceInfo(request);
    if (deviceInfo == null) {
        log.error("设备信息生成失败，使用默认配置");
        return;
    }
    // 更新配置...
} catch (Exception e) {
    log.error("设备随机化初始化失败，使用默认配置", e);
}
```

### 配置更新失败

如果更新 `FQApiProperties` 时发生异常：

1. 记录 ERROR 级别日志
2. 保留原有配置
3. 应用继续正常启动

## Testing Strategy

### Unit Tests

1. **DeviceRandomizationInitializer 单元测试**
   - 测试启用随机化时正确调用 DeviceGeneratorService
   - 测试禁用随机化时跳过设备生成
   - 测试设备生成失败时的回退行为
   - 测试日志输出内容

2. **FQApiProperties.updateFromDeviceInfo 单元测试**
   - 测试所有字段正确更新
   - 测试部分字段为 null 时的处理

### Property-Based Tests

使用 JUnit 5 + jqwik 进行属性测试：

1. **Property 1 测试**: 验证启动后设备信息与默认值不同
2. **Property 2 测试**: 验证注入后字段一致性
3. **Property 3 测试**: 验证品牌-型号有效性
4. **Property 4 测试**: 验证 User-Agent 和 Cookie 一致性

配置：每个属性测试运行 100 次迭代。

### Integration Tests

1. 完整启动流程测试，验证设备信息正确注入
2. 配置开关测试，验证 `randomize-on-startup=false` 时行为正确
