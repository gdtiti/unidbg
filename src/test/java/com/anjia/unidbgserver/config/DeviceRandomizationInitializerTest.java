package com.anjia.unidbgserver.config;

import com.anjia.unidbgserver.dto.DeviceInfo;
import com.anjia.unidbgserver.dto.DeviceRegisterRequest;
import com.anjia.unidbgserver.service.DeviceGeneratorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.ApplicationArguments;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DeviceRandomizationInitializer 单元测试
 * 测试启动时设备随机化初始化器的行为
 * 
 * _Requirements: 1.1, 1.3, 2.2, 2.3_
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceRandomizationInitializerTest {

    @Mock
    private DeviceGeneratorService deviceGeneratorService;

    @Mock
    private ApplicationArguments applicationArguments;

    private FQApiProperties fqApiProperties;
    private DeviceRandomizationInitializer initializer;

    @Before
    public void setUp() {
        fqApiProperties = new FQApiProperties();
        initializer = new DeviceRandomizationInitializer(fqApiProperties, deviceGeneratorService);
    }

    /**
     * 测试启用随机化时正确调用 DeviceGeneratorService
     * _Requirements: 1.1, 2.3_
     */
    @Test
    public void testRandomizationEnabled_CallsDeviceGeneratorService() {
        // Given
        fqApiProperties.setRandomizeOnStartup(true);
        DeviceInfo mockDeviceInfo = createMockDeviceInfo();
        when(deviceGeneratorService.generateDeviceInfo(any(DeviceRegisterRequest.class)))
                .thenReturn(mockDeviceInfo);

        // When
        initializer.run(applicationArguments);

        // Then
        verify(deviceGeneratorService, times(1)).generateDeviceInfo(any(DeviceRegisterRequest.class));
    }

    /**
     * 测试启用随机化时正确更新 FQApiProperties
     * _Requirements: 1.2, 2.3_
     */
    @Test
    public void testRandomizationEnabled_UpdatesFQApiProperties() {
        // Given
        fqApiProperties.setRandomizeOnStartup(true);
        DeviceInfo mockDeviceInfo = createMockDeviceInfo();
        when(deviceGeneratorService.generateDeviceInfo(any(DeviceRegisterRequest.class)))
                .thenReturn(mockDeviceInfo);

        // When
        initializer.run(applicationArguments);

        // Then
        assertEquals("Xiaomi", fqApiProperties.getDevice().getDeviceBrand());
        assertEquals("RedmiK50", fqApiProperties.getDevice().getDeviceType());
        assertEquals("test-device-id", fqApiProperties.getDevice().getDeviceId());
        assertEquals("test-install-id", fqApiProperties.getDevice().getInstallId());
        assertEquals("test-cdid", fqApiProperties.getDevice().getCdid());
    }

    /**
     * 测试禁用随机化时跳过设备生成
     * _Requirements: 2.2_
     */
    @Test
    public void testRandomizationDisabled_SkipsDeviceGeneration() {
        // Given
        fqApiProperties.setRandomizeOnStartup(false);
        String originalDeviceId = fqApiProperties.getDevice().getDeviceId();

        // When
        initializer.run(applicationArguments);

        // Then
        verify(deviceGeneratorService, never()).generateDeviceInfo(any());
        assertEquals(originalDeviceId, fqApiProperties.getDevice().getDeviceId());
    }

    /**
     * 测试设备生成返回 null 时的回退行为
     * _Requirements: 1.3_
     */
    @Test
    public void testDeviceGenerationReturnsNull_FallsBackToDefault() {
        // Given
        fqApiProperties.setRandomizeOnStartup(true);
        String originalDeviceId = fqApiProperties.getDevice().getDeviceId();
        when(deviceGeneratorService.generateDeviceInfo(any(DeviceRegisterRequest.class)))
                .thenReturn(null);

        // When
        initializer.run(applicationArguments);

        // Then
        assertEquals(originalDeviceId, fqApiProperties.getDevice().getDeviceId());
    }

    /**
     * 测试设备生成抛出异常时的回退行为
     * _Requirements: 1.3_
     */
    @Test
    public void testDeviceGenerationThrowsException_FallsBackToDefault() {
        // Given
        fqApiProperties.setRandomizeOnStartup(true);
        String originalDeviceId = fqApiProperties.getDevice().getDeviceId();
        when(deviceGeneratorService.generateDeviceInfo(any(DeviceRegisterRequest.class)))
                .thenThrow(new RuntimeException("Test exception"));

        // When
        initializer.run(applicationArguments);

        // Then
        assertEquals(originalDeviceId, fqApiProperties.getDevice().getDeviceId());
    }

    /**
     * 测试生成请求使用真实品牌标志
     * _Requirements: 4.1, 4.2_
     */
    @Test
    public void testDeviceGenerationRequest_UsesRealBrandFlag() {
        // Given
        fqApiProperties.setRandomizeOnStartup(true);
        DeviceInfo mockDeviceInfo = createMockDeviceInfo();
        ArgumentCaptor<DeviceRegisterRequest> requestCaptor = 
                ArgumentCaptor.forClass(DeviceRegisterRequest.class);
        when(deviceGeneratorService.generateDeviceInfo(any(DeviceRegisterRequest.class)))
                .thenReturn(mockDeviceInfo);

        // When
        initializer.run(applicationArguments);

        // Then
        verify(deviceGeneratorService).generateDeviceInfo(requestCaptor.capture());
        DeviceRegisterRequest capturedRequest = requestCaptor.getValue();
        assertTrue(capturedRequest.getUseRealBrand());
        assertTrue(capturedRequest.getUseRealAlgorithm());
    }

    /**
     * 创建模拟的 DeviceInfo 对象
     */
    private DeviceInfo createMockDeviceInfo() {
        return DeviceInfo.builder()
                .deviceBrand("Xiaomi")
                .deviceType("RedmiK50")
                .deviceId("test-device-id")
                .installId("test-install-id")
                .cdid("test-cdid")
                .resolution("2400*1080")
                .dpi("480")
                .hostAbi("arm64-v8a")
                .romVersion("V417IR+release-keys")
                .osVersion("12")
                .osApi(32)
                .userAgent("com.dragon.read.oversea.gp/68132 (Linux; U; Android 12; zh_CN; RedmiK50; Build/V417IR;tt-ok/3.12.13.4-tiktok)")
                .cookie("store-region=cn-zj; store-region-src=did; install_id=test-install-id;")
                .aid("1967")
                .versionCode("68132")
                .versionName("6.8.1.32")
                .updateVersionCode("68132")
                .build();
    }
}
