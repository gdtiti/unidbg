package com.anjia.unidbgserver.config;

import com.anjia.unidbgserver.dto.DeviceInfo;
import com.anjia.unidbgserver.dto.DeviceRegisterRequest;
import com.anjia.unidbgserver.service.DeviceGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 设备随机化初始化器
 * 在应用启动时自动生成随机设备信息并更新配置
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DeviceRandomizationInitializer implements ApplicationRunner {

    private final FQApiProperties fqApiProperties;
    private final DeviceGeneratorService deviceGeneratorService;

    @Override
    public void run(ApplicationArguments args) {
        // 检查是否启用随机化
        if (!fqApiProperties.isRandomizeOnStartup()) {
            log.info("设备随机化已禁用，使用默认配置");
            return;
        }

        try {
            // 创建设备生成请求，使用真实品牌
            DeviceRegisterRequest request = DeviceRegisterRequest.builder()
                    .useRealBrand(true)
                    .useRealAlgorithm(true)
                    .build();

            // 生成随机设备信息
            DeviceInfo deviceInfo = deviceGeneratorService.generateDeviceInfo(request);

            if (deviceInfo == null) {
                log.error("设备信息生成失败，使用默认配置");
                return;
            }

            // 更新配置
            fqApiProperties.updateFromDeviceInfo(deviceInfo);

            // 记录成功日志（不记录敏感信息）
            log.info("设备随机化完成 - 品牌: {}, 型号: {}, 设备ID: {}",
                    deviceInfo.getDeviceBrand(),
                    deviceInfo.getDeviceType(),
                    deviceInfo.getDeviceId());

        } catch (Exception e) {
            log.error("设备随机化初始化失败，使用默认配置", e);
        }
    }
}
