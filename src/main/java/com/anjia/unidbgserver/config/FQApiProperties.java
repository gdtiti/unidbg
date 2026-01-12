package com.anjia.unidbgserver.config;

import com.anjia.unidbgserver.dto.DeviceInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * FQ API 配置属性
 * 用于管理FQ API的设备参数和请求配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "fq.api")
public class FQApiProperties {
    
    /**
     * API基础URL
     */
    private String baseUrl = "https://api5-normal-sinfonlineb.fqnovel.com";
    
    /**
     * 是否在启动时随机生成设备信息
     * 默认为 true
     */
    private boolean randomizeOnStartup = true;
    
    /**
     * 默认User-Agent
     */
    private String userAgent = "com.dragon.read.oversea.gp/68132 (Linux; U; Android 10; zh_CN; OnePlus11; Build/V291IR;tt-ok/3.12.13.4-tiktok)";
    
    /**
     * Cookie配置
     */
    private String cookie = "store-region=cn-zj; store-region-src=did; install_id=933935730456617";
    
    /**
     * 设备参数配置
     */
    private Device device = new Device();
    
    @Data
    public static class Device {
        /**
         * 设备唯一标识符
         */
        private String cdid = "17f05006-423a-4172-be4b-7d26a42f2f4a";
        
        /**
         * 安装ID
         */
        private String installId = "933935730456617";
        
        /**
         * 设备ID
         */
        private String deviceId = "933935730452521";
        
        /**
         * 应用ID
         */
        private String aid = "1967";
        
        /**
         * 版本代码
         */
        private String versionCode = "68132";
        
        /**
         * 版本名称
         */
        private String versionName = "6.8.1.32";
        
        /**
         * 更新版本代码
         */
        private String updateVersionCode = "68132";
        
        /**
         * 设备类型
         */
        private String deviceType = "OnePlus11";
        
        /**
         * 设备品牌
         */
        private String deviceBrand = "OnePlus";
        
        /**
         * ROM版本
         */
        private String romVersion = "V291IR+release-keys";
        
        /**
         * 分辨率
         */
        private String resolution = "3200*1440";
        
        /**
         * DPI
         */
        private String dpi = "640";
        
        /**
         * 主机ABI
         */
        private String hostAbi = "arm64-v8a";

        /**
         * Android 版本（例如 13）
         */
        private String osVersion = "13";

        /**
         * Android API（例如 32）
         */
        private String osApi = "32";
    }
    
    /**
     * 从 DeviceInfo 更新配置属性
     * 用于启动时随机化设备信息后更新配置
     * 
     * @param deviceInfo 设备信息对象
     */
    public void updateFromDeviceInfo(DeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            return;
        }
        
        // 更新 userAgent
        if (deviceInfo.getUserAgent() != null) {
            this.userAgent = deviceInfo.getUserAgent();
        }
        
        // 更新 cookie
        if (deviceInfo.getCookie() != null) {
            this.cookie = deviceInfo.getCookie();
        }
        
        // 更新 device 内部类的所有字段
        if (this.device == null) {
            this.device = new Device();
        }
        
        if (deviceInfo.getCdid() != null) {
            this.device.setCdid(deviceInfo.getCdid());
        }
        if (deviceInfo.getInstallId() != null) {
            this.device.setInstallId(deviceInfo.getInstallId());
        }
        if (deviceInfo.getDeviceId() != null) {
            this.device.setDeviceId(deviceInfo.getDeviceId());
        }
        if (deviceInfo.getAid() != null) {
            this.device.setAid(deviceInfo.getAid());
        }
        if (deviceInfo.getVersionCode() != null) {
            this.device.setVersionCode(deviceInfo.getVersionCode());
        }
        if (deviceInfo.getVersionName() != null) {
            this.device.setVersionName(deviceInfo.getVersionName());
        }
        if (deviceInfo.getUpdateVersionCode() != null) {
            this.device.setUpdateVersionCode(deviceInfo.getUpdateVersionCode());
        }
        if (deviceInfo.getDeviceType() != null) {
            this.device.setDeviceType(deviceInfo.getDeviceType());
        }
        if (deviceInfo.getDeviceBrand() != null) {
            this.device.setDeviceBrand(deviceInfo.getDeviceBrand());
        }
        if (deviceInfo.getRomVersion() != null) {
            this.device.setRomVersion(deviceInfo.getRomVersion());
        }
        if (deviceInfo.getResolution() != null) {
            this.device.setResolution(deviceInfo.getResolution());
        }
        if (deviceInfo.getDpi() != null) {
            this.device.setDpi(deviceInfo.getDpi());
        }
        if (deviceInfo.getHostAbi() != null) {
            this.device.setHostAbi(deviceInfo.getHostAbi());
        }
        if (deviceInfo.getOsVersion() != null) {
            this.device.setOsVersion(deviceInfo.getOsVersion());
        }
        if (deviceInfo.getOsApi() != null) {
            this.device.setOsApi(String.valueOf(deviceInfo.getOsApi()));
        }
    }
}
