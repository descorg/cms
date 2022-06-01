package org.springcms.core.launch.constant;

public interface NacosConstant {
    public static final String NACOS_ADDR = "127.0.0.1:8848";

    public static final String NACOS_CONFIG_PREFIX = "cmsx";

    public static final String NACOS_GROUP_SUFFIX = "-group";

    public static final String NACOS_CONFIG_FORMAT = "yaml";

    public static final String NACOS_CONFIG_JSON_FORMAT = "json";

    public static final String NACOS_CONFIG_REFRESH = "true";

    public static final String NACOS_CONFIG_GROUP = "DEFAULT_GROUP";

    public static final String NACOS_SEATA_GROUP = "SEATA_GROUP";

    static String dataId(String appName) {
        return appName + "." + "yaml";
    }

    static String dataId(String appName, String profile) {
        return dataId(appName, profile, "yaml");
    }

    static String dataId(String appName, String profile, String format) {
        return appName + "-" + profile + "." + format;
    }

    static String sharedDataId() {
        return "cmsx.yaml";
    }

    static String sharedDataId(String profile) {
        return "cmsx-" + profile + "." + "yaml";
    }

    static String sharedDataIds(String profile) {
        return "cmsx.yaml,cmsx-" + profile + "." + "yaml";
    }
}
