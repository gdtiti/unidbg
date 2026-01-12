# Requirements Document

## Introduction

本功能实现应用启动时自动随机生成设备信息，替代当前从 application.yml 读取固定设备配置的方式。每次应用启动都会生成新的设备指纹，用于 API 请求，以降低被识别和限制的风险。

## Glossary

- **Device_Info**: 设备信息对象，包含设备品牌、型号、ID、分辨率等完整的设备指纹信息
- **FQApiProperties**: Spring Boot 配置属性类，用于管理 FQ API 的设备参数和请求配置
- **DeviceGeneratorService**: 现有的设备生成服务，能够随机生成完整的设备信息
- **Startup_Initializer**: 应用启动初始化器，在 Spring Boot 应用启动完成后执行初始化逻辑

## Requirements

### Requirement 1: 启动时自动生成随机设备信息

**User Story:** As a system operator, I want the application to automatically generate random device information on startup, so that each instance uses a unique device fingerprint without manual configuration.

#### Acceptance Criteria

1. WHEN the application starts, THE Startup_Initializer SHALL invoke DeviceGeneratorService to generate random Device_Info
2. WHEN Device_Info is generated successfully, THE Startup_Initializer SHALL update FQApiProperties with the new device information
3. WHEN Device_Info generation fails, THE Startup_Initializer SHALL log an error and fall back to the default configuration from application.yml
4. THE Startup_Initializer SHALL complete device initialization before any API requests are processed

### Requirement 2: 配置控制随机化行为

**User Story:** As a developer, I want to control whether device randomization is enabled, so that I can disable it during development or testing.

#### Acceptance Criteria

1. THE FQApiProperties SHALL include a boolean property `randomize-on-startup` with default value `true`
2. WHEN `randomize-on-startup` is set to `false`, THE Startup_Initializer SHALL skip device randomization and use configuration from application.yml
3. WHEN `randomize-on-startup` is set to `true`, THE Startup_Initializer SHALL generate and apply random device information

### Requirement 3: 启动日志记录设备信息

**User Story:** As a system operator, I want to see the generated device information in startup logs, so that I can verify and troubleshoot device configuration.

#### Acceptance Criteria

1. WHEN random Device_Info is generated and applied, THE Startup_Initializer SHALL log the device brand, device type, and device ID at INFO level
2. WHEN device randomization is skipped, THE Startup_Initializer SHALL log a message indicating that default configuration is being used
3. THE Startup_Initializer SHALL NOT log sensitive information such as full cookies or complete user-agent strings at INFO level

### Requirement 4: 使用真实设备品牌和型号

**User Story:** As a system operator, I want the generated device information to use realistic device brands and models, so that the device fingerprint appears authentic.

#### Acceptance Criteria

1. WHEN generating random Device_Info, THE DeviceGeneratorService SHALL select from a predefined list of real device brands (Xiaomi, HUAWEI, OPPO, vivo, OnePlus, Samsung)
2. WHEN a brand is selected, THE DeviceGeneratorService SHALL select a corresponding real device model for that brand
3. THE DeviceGeneratorService SHALL generate consistent User-Agent and Cookie values based on the selected device information
