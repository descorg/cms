package org.springcms.core.launch;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springcms.core.launch.constant.NacosConstant;
import org.springcms.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class CmsApplication {
    public static ConfigurableApplicationContext run(String appName, Class source, String... args) {
        SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, args);
        return builder.run(args);
    }

    public static SpringApplicationBuilder createSpringApplicationBuilder(String appName, Class source, String... args) {
        String profile;
        Assert.hasText(appName, "[appName]服务名不能为空");

        StandardEnvironment standardEnvironment = new StandardEnvironment();
        MutablePropertySources propertySources = standardEnvironment.getPropertySources();
        propertySources.addFirst((PropertySource)new SimpleCommandLinePropertySource(args));
        propertySources.addLast((PropertySource)new MapPropertySource("systemProperties", standardEnvironment.getSystemProperties()));
        propertySources.addLast((PropertySource)new SystemEnvironmentPropertySource("systemEnvironment", standardEnvironment.getSystemEnvironment()));
        String[] activeProfiles = standardEnvironment.getActiveProfiles();
        List<String> profiles = Arrays.asList(activeProfiles);
        List<String> presetProfiles = new ArrayList<>(Arrays.asList(new String[] { "dev", "test", "prod" }));
        presetProfiles.retainAll(profiles);
        List<String> activeProfileList = new ArrayList<>(profiles);
        Function<Object[], String> joinFun = StringUtils::arrayToCommaDelimitedString;
        SpringApplicationBuilder builder = new SpringApplicationBuilder(new Class[] { source });

        if (activeProfileList.isEmpty()) {
            profile = "dev";
            activeProfileList.add(profile);
            builder.profiles(new String[] { profile });
        } else if (activeProfileList.size() == 1) {
            profile = activeProfileList.get(0);
        } else {
            throw new RuntimeException("同时存在环境变量：[" + StringUtils.arrayToCommaDelimitedString(activeProfiles) + "]");
        }

        String startJarPath = CmsApplication.class.getResource("/").getPath().split("!")[0];
        String activePros = joinFun.apply(activeProfileList.toArray());
        System.out.printf("----启动中，读取到的环境变量：[%s]，jar地址:[%s]----%n", new Object[] { activePros, startJarPath });

        Properties props = System.getProperties();
        props.setProperty("spring.application.name", appName);
        props.setProperty("spring.profiles.active", profile);
        props.setProperty("info.version", "1.1.1.RELEASE");
        props.setProperty("info.desc", appName);
        props.setProperty("file.encoding", StandardCharsets.UTF_8.name());
        props.setProperty("cmsx.env", profile);
        props.setProperty("cmsx.name", appName);
        props.setProperty("cmsx.is-local", String.valueOf(isLocalDev()));
        props.setProperty("cmsx.dev-mode", profile.equals("prod") ? "false" : "true");
        props.setProperty("cmsx.service.version", "1.1.1.RELEASE");
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("spring.main.allow-bean-definition-overriding", "true");
        defaultProperties.setProperty("spring.sleuth.sampler.percentage", "1.0");
        defaultProperties.setProperty("spring.cloud.alibaba.seata.tx-service-group", appName.concat("-group"));
        defaultProperties.setProperty("spring.cloud.nacos.config.file-extension", "yaml");
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].data-id", NacosConstant.sharedDataId());
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].group", "DEFAULT_GROUP");
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].refresh", "true");
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].data-id", NacosConstant.sharedDataId(profile));
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].group", "DEFAULT_GROUP");
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].refresh", "true");
        builder.properties(defaultProperties);

        List<LauncherService> launcherList = new ArrayList<>();
        ServiceLoader.<LauncherService>load(LauncherService.class).forEach(launcherList::add);
        ((List<LauncherService>)launcherList.stream().sorted(Comparator.comparing(LauncherService::getOrder)).collect(Collectors.toList()))
                .forEach(launcherService -> launcherService.launcher(builder, appName, profile, isLocalDev()));
        return builder;
    }

    public static boolean isLocalDev() {
        String osName = System.getProperty("os.name");
        return (StringUtils.hasText(osName) && !"LINUX".equalsIgnoreCase(osName));
    }
}
