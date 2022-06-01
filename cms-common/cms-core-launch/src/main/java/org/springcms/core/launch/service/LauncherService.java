package org.springcms.core.launch.service;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;

public interface LauncherService extends Ordered, Comparable<LauncherService> {
    default int getOrder() {
        return 0;
    }

    default int compareTo(LauncherService o) {
        return Integer.compare(getOrder(), o.getOrder());
    }

    void launcher(SpringApplicationBuilder paramSpringApplicationBuilder, String paramString1, String paramString2, boolean paramBoolean);
}
