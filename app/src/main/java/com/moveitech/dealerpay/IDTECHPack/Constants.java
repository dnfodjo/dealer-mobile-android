package com.moveitech.dealerpay.IDTECHPack;

public class Constants {
    //In production the apikey should be treated as a secret key that is never known on the client side.
    public static final String SB_API_KEY = "24425c33043244778a188bd19846e860";
    public static final String PROD_API_KEY = "24425c33043244778a188bd19846e860";
    public static final String PROD_BASE_URL = "https://gateway-sb.clearent.net";
    public static final String SB_BASE_URL = "https://gateway-sb.clearent.net";

//    public static final String SB_PUBLIC_KEY = "307a301406072a8648ce3d020106092b240303020801010c036200042b0cfb3a1faaca8fb779081717a0bafb03e0cb061a1ef297f75dc5b951aaf163b0c2021e9bb73071bf89c711070e96ab1b63c674be13041d9eb68a456eb6ae63a97a9345c120cd8bff1d5998b2ebbafc198c5c5b26c687bfbeb68b312feb43bf";
//    public static final String PROD_PUBLIC_KEY = "307a301406072a8648ce3d020106092b240303020801010c036200042b0cfb3a1faaca8fb779081717a0bafb03e0cb061a1ef297f75dc5b951aaf163b0c2021e9bb73071bf89c711070e96ab1b63c674be13041d9eb68a456eb6ae63a97a9345c120cd8bff1d5998b2ebbafc198c5c5b26c687bfbeb68b312feb43bf";

    public static final String SB_PUBLIC_KEY = "307a301406072a8648ce3d020106092b240303020801010c0362000444b8c0c51e4dbb6311337fd2e5cc799a8f56f8011e6074161edd898ee54b86ab5a6b34fb05e62949c550f2c17303dc2865faf38574af0ef47d44f8b11c2fe31f7b4263a423d12e7f575b3f8a6f44ed29cf47bc8cf64bb04ad9b7194629452ce1";
    public static final String PROD_PUBLIC_KEY = "307a301406072a8648ce3d020106092b240303020801010c0362000444b8c0c51e4dbb6311337fd2e5cc799a8f56f8011e6074161edd898ee54b86ab5a6b34fb05e62949c550f2c17303dc2865faf38574af0ef47d44f8b11c2fe31f7b4263a423d12e7f575b3f8a6f44ed29cf47bc8cf64bb04ad9b7194629452ce1";
    public static final String SOFTWARE_TYPE = "Clearent IDTech JDemo";
    public static final String SOFTWARE_TYPE_VERSION = "1.126.0";
    public static final String DEFAULT_EMAIl_ADDRESS = "dhigginbotham@clearent.com";
    public static final String BLUETOOTH_READER_PREFIX = "IDTECH-VP3300";
    public static final String IDTECH = "IDTECH";
//    public static final String IDTECH = "ALE-L21";
    public static final long BLUETOOTH_SCAN_PERIOD = 10000;


    public static String getSoftwareTypeAndVersion() {
        return Constants.SOFTWARE_TYPE + " " + Constants.SOFTWARE_TYPE_VERSION;
    }
}
