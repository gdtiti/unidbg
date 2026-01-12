package com.anjia.unidbgserver.service;

import com.anjia.unidbgserver.dto.DeviceInfo;
import com.anjia.unidbgserver.dto.DeviceRegisterRequest;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Property-based tests for DeviceGeneratorService
 * 
 * Feature: startup-device-randomization
 */
public class DeviceGeneratorServicePropertyTest {

    private final DeviceGeneratorService deviceGeneratorService = new DeviceGeneratorService();

    // Predefined valid brands from DeviceGeneratorService
    private static final Set<String> VALID_BRANDS = new HashSet<>(Arrays.asList(
            "Xiaomi", "HUAWEI", "OPPO", "vivo", "OnePlus", "Samsung"
    ));

    // Valid models for each brand (from DeviceGeneratorService.DEVICE_BRANDS)
    private static final Map<String, Set<String>> VALID_MODELS = new HashMap<>();

    static {
        VALID_MODELS.put("Xiaomi", new HashSet<>(Arrays.asList(
                "24031PN0DC", "2304FPN6DC", "23078RKD5C", "23013RK75C", "22081212C",
                "2201123C", "21081111RG", "2107113SG", "2106118C", "2012123AC",
                "M2102K1AC", "M2011K2C", "M2007J1SC", "M2006C3LG", "RedmiK40",
                "RedmiK50", "MI11", "MI12", "MI13", "RedmiNote11", "RedmiNote12"
        )));

        VALID_MODELS.put("HUAWEI", new HashSet<>(Arrays.asList(
                "ELS-AN00", "TAS-AL00", "ANA-AN00", "LYA-AL00", "VOG-AL00",
                "HMA-AL00", "JKM-AL00", "WLZ-AN00", "BAL-AL00", "CDL-AN00",
                "P50", "P40", "Mate40", "Mate50", "nova9", "nova10"
        )));

        VALID_MODELS.put("OPPO", new HashSet<>(Arrays.asList(
                "CPH2207", "CPH2211", "CPH2237", "CPH2371", "CPH2399",
                "PDSM00", "PDST00", "PGBM10", "PGJM10", "PEQM00",
                "FindX5", "Reno8", "Reno9", "A96", "K10"
        )));

        VALID_MODELS.put("vivo", new HashSet<>(Arrays.asList(
                "V2197A", "V2118A", "V2055A", "V2073A", "V2102A",
                "PD2186", "PD2194", "PD1986", "PD1955", "PD1924",
                "X80", "X90", "S15", "Y76s", "iQOO9"
        )));

        VALID_MODELS.put("OnePlus", new HashSet<>(Arrays.asList(
                "LE2100", "LE2110", "MT2110", "MT2111", "PJZ110",
                "OnePlus9", "OnePlus10", "OnePlus11", "OnePlusNord"
        )));

        VALID_MODELS.put("Samsung", new HashSet<>(Arrays.asList(
                "SM-G9980", "SM-G9910", "SM-G7810", "SM-G7730", "SM-A5260",
                "GalaxyS22", "GalaxyS23", "GalaxyNote20", "GalaxyA53"
        )));
    }

    /**
     * Property 3: Valid Brand-Model Pairs
     * 
     * *For any* generated DeviceInfo, deviceBrand SHALL be one of predefined brands 
     * (Xiaomi, HUAWEI, OPPO, vivo, OnePlus, Samsung), AND deviceType SHALL be valid 
     * for that brand as defined in DeviceGeneratorService.DEVICE_BRANDS.
     * 
     * **Validates: Requirements 4.1, 4.2**
     */
    @Property(tries = 100)
    @Label("Property 3: Valid Brand-Model Pairs")
    void validBrandModelPairs(@ForAll @IntRange(min = 0, max = 99) int seed) {
        // Given - create request with useRealBrand = true
        DeviceRegisterRequest request = new DeviceRegisterRequest();
        request.setUseRealBrand(true);
        request.setUseRealAlgorithm(true);

        // When - generate device info
        DeviceInfo deviceInfo = deviceGeneratorService.generateDeviceInfo(request);

        // Then - verify brand is valid
        assertNotNull("DeviceInfo should not be null", deviceInfo);
        assertNotNull("Device brand should not be null", deviceInfo.getDeviceBrand());
        assertNotNull("Device type should not be null", deviceInfo.getDeviceType());

        String brand = deviceInfo.getDeviceBrand();
        String model = deviceInfo.getDeviceType();

        assertTrue(
                String.format("Brand '%s' should be one of predefined brands: %s", brand, VALID_BRANDS),
                VALID_BRANDS.contains(brand)
        );

        Set<String> validModelsForBrand = VALID_MODELS.get(brand);
        assertNotNull(
                String.format("Valid models should exist for brand '%s'", brand),
                validModelsForBrand
        );

        assertTrue(
                String.format("Model '%s' should be valid for brand '%s'. Valid models: %s", 
                        model, brand, validModelsForBrand),
                validModelsForBrand.contains(model)
        );
    }

    /**
     * Property 4: User-Agent and Cookie Consistency
     * 
     * *For any* generated DeviceInfo:
     * - The userAgent string SHALL contain the deviceType value
     * - The cookie string SHALL contain the installId value
     * 
     * **Validates: Requirements 4.3**
     */
    @Property(tries = 100)
    @Label("Property 4: User-Agent and Cookie Consistency")
    void userAgentAndCookieConsistency(@ForAll @IntRange(min = 0, max = 99) int seed) {
        // Given - create request with useRealBrand = true
        DeviceRegisterRequest request = new DeviceRegisterRequest();
        request.setUseRealBrand(true);
        request.setUseRealAlgorithm(true);

        // When - generate device info
        DeviceInfo deviceInfo = deviceGeneratorService.generateDeviceInfo(request);

        // Then - verify consistency
        assertNotNull("DeviceInfo should not be null", deviceInfo);
        assertNotNull("User-Agent should not be null", deviceInfo.getUserAgent());
        assertNotNull("Cookie should not be null", deviceInfo.getCookie());
        assertNotNull("Device type should not be null", deviceInfo.getDeviceType());
        assertNotNull("Install ID should not be null", deviceInfo.getInstallId());

        String userAgent = deviceInfo.getUserAgent();
        String cookie = deviceInfo.getCookie();
        String deviceType = deviceInfo.getDeviceType();
        String installId = deviceInfo.getInstallId();

        // User-Agent SHALL contain deviceType
        assertTrue(
                String.format("User-Agent '%s' should contain deviceType '%s'", userAgent, deviceType),
                userAgent.contains(deviceType)
        );

        // Cookie SHALL contain installId
        assertTrue(
                String.format("Cookie '%s' should contain installId '%s'", cookie, installId),
                cookie.contains(installId)
        );
    }
}
