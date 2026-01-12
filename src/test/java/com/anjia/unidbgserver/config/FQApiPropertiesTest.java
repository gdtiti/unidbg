package com.anjia.unidbgserver.config;

import com.anjia.unidbgserver.dto.DeviceInfo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * FQApiProperties.updateFromDeviceInfo 单元测试
 * 测试设备信息更新方法的行为
 * 
 * _Requirements: 1.2_
 */
public class FQApiPropertiesTest {

    private FQApiProperties fqApiProperties;

    @Before
    public void setUp() {
        fqApiProperties = new FQApiProperties();
    }

    /**
     * 测试所有字段正确更新
     * _Requirements: 1.2_
     */
    @Test
    public void testUpdateFromDeviceInfo_AllFieldsUpdated() {
        // Given
        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceBrand("Xiaomi")
                .deviceType("RedmiK50")
                .deviceId("new-device-id")
                .installId("new-install-id")
                .cdid("new-cdid")
                .resolution("2400*1080")
                .dpi("480")
                .hostAbi("arm64-v8a")
                .romVersion("V417IR+release-keys")
                .osVersion("12")
                .osApi(32)
                .userAgent("new-user-agent")
                .cookie("new-cookie")
                .aid("1967")
                .versionCode("68132")
                .versionName("6.8.1.32")
                .updateVersionCode("68132")
                .build();

        // When
        fqApiProperties.updateFromDeviceInfo(deviceInfo);

        // Then
        assertEquals("new-user-agent", fqApiProperties.getUserAgent());
        assertEquals("new-cookie", fqApiProperties.getCookie());
        assertEquals("Xiaomi", fqApiProperties.getDevice().getDeviceBrand());
        assertEquals("RedmiK50", fqApiProperties.getDevice().getDeviceType());
        assertEquals("new-device-id", fqApiProperties.getDevice().getDeviceId());
        assertEquals("new-install-id", fqApiProperties.getDevice().getInstallId());
        assertEquals("new-cdid", fqApiProperties.getDevice().getCdid());
        assertEquals("2400*1080", fqApiProperties.getDevice().getResolution());
        assertEquals("480", fqApiProperties.getDevice().getDpi());
        assertEquals("arm64-v8a", fqApiProperties.getDevice().getHostAbi());
        assertEquals("V417IR+release-keys", fqApiProperties.getDevice().getRomVersion());
        assertEquals("12", fqApiProperties.getDevice().getOsVersion());
        assertEquals("32", fqApiProperties.getDevice().getOsApi());
        assertEquals("1967", fqApiProperties.getDevice().getAid());
        assertEquals("68132", fqApiProperties.getDevice().getVersionCode());
        assertEquals("6.8.1.32", fqApiProperties.getDevice().getVersionName());
        assertEquals("68132", fqApiProperties.getDevice().getUpdateVersionCode());
    }

    /**
     * 测试部分字段为 null 时的处理 - 保留原有值
     * _Requirements: 1.2_
     */
    @Test
    public void testUpdateFromDeviceInfo_PartialNullFields_PreservesOriginalValues() {
        // Given - 记录原始值
        String originalUserAgent = fqApiProperties.getUserAgent();
        String originalCookie = fqApiProperties.getCookie();
        String originalDeviceId = fqApiProperties.getDevice().getDeviceId();
        
        // 创建只有部分字段的 DeviceInfo
        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceBrand("HUAWEI")
                .deviceType("P50")
                // 其他字段为 null
                .build();

        // When
        fqApiProperties.updateFromDeviceInfo(deviceInfo);

        // Then - 非 null 字段被更新
        assertEquals("HUAWEI", fqApiProperties.getDevice().getDeviceBrand());
        assertEquals("P50", fqApiProperties.getDevice().getDeviceType());
        
        // null 字段保留原值
        assertEquals(originalUserAgent, fqApiProperties.getUserAgent());
        assertEquals(originalCookie, fqApiProperties.getCookie());
        assertEquals(originalDeviceId, fqApiProperties.getDevice().getDeviceId());
    }

    /**
     * 测试传入 null DeviceInfo 时不抛出异常
     * _Requirements: 1.2_
     */
    @Test
    public void testUpdateFromDeviceInfo_NullDeviceInfo_NoException() {
        // Given
        String originalDeviceId = fqApiProperties.getDevice().getDeviceId();

        // When
        fqApiProperties.updateFromDeviceInfo(null);

        // Then - 原值保持不变
        assertEquals(originalDeviceId, fqApiProperties.getDevice().getDeviceId());
    }

    /**
     * 测试 osApi 从 Integer 转换为 String
     * _Requirements: 1.2_
     */
    @Test
    public void testUpdateFromDeviceInfo_OsApiConversion() {
        // Given
        DeviceInfo deviceInfo = DeviceInfo.builder()
                .osApi(33)
                .build();

        // When
        fqApiProperties.updateFromDeviceInfo(deviceInfo);

        // Then
        assertEquals("33", fqApiProperties.getDevice().getOsApi());
    }

    /**
     * 测试默认 randomizeOnStartup 值为 true
     * _Requirements: 2.1_
     */
    @Test
    public void testDefaultRandomizeOnStartup_IsTrue() {
        // Given - 新创建的 FQApiProperties

        // Then
        assertTrue(fqApiProperties.isRandomizeOnStartup());
    }

    /**
     * 测试 randomizeOnStartup 可以设置为 false
     * _Requirements: 2.1_
     */
    @Test
    public void testRandomizeOnStartup_CanBeSetToFalse() {
        // When
        fqApiProperties.setRandomizeOnStartup(false);

        // Then
        assertFalse(fqApiProperties.isRandomizeOnStartup());
    }
}
