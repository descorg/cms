package org.springcms.core.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cmsx.token")
public class JwtProperties {
    public void setState(Boolean state) {
        this.state = state;
    }

    public void setSingle(Boolean single) {
        this.single = single;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof JwtProperties))
            return false;
        JwtProperties other = (JwtProperties)o;
        if (!other.canEqual(this))
            return false;
        Object this$state = getState(), other$state = other.getState();
        if ((this$state == null) ? (other$state != null) : !this$state.equals(other$state))
            return false;
        Object this$single = getSingle(), other$single = other.getSingle();
        if ((this$single == null) ? (other$single != null) : !this$single.equals(other$single))
            return false;
        Object this$signKey = getSignKey(), other$signKey = other.getSignKey();
        return !((this$signKey == null) ? (other$signKey != null) : !this$signKey.equals(other$signKey));
    }

    protected boolean canEqual(Object other) {
        return other instanceof JwtProperties;
    }

    public int hashCode() {
        int PRIME = 59, result = 1;
        Object $state = getState();
        result = result * 59 + (($state == null) ? 43 : $state.hashCode());
        Object $single = getSingle();
        result = result * 59 + (($single == null) ? 43 : $single.hashCode());
        Object $signKey = getSignKey();
        return result * 59 + (($signKey == null) ? 43 : $signKey.hashCode());
    }

    public String toString() {
        return "JwtProperties(state=" + getState() + ", single=" + getSingle() + ", signKey=" + getSignKey() + ")";
    }

    private Boolean state = Boolean.FALSE;

    public Boolean getState() {
        return this.state;
    }

    private Boolean single = Boolean.FALSE;

    public Boolean getSingle() {
        return this.single;
    }

    private String signKey = "cmsxisapowerfulmicroservicearchitectureupgradedandoptimizedfromacommercialproject";

    public String getSignKey() {
        if (this.signKey.length() < 32)
            return "cmsxisapowerfulmicroservicearchitectureupgradedandoptimizedfromacommercialproject";
        return this.signKey;
    }
}
