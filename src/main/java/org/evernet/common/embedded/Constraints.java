package org.evernet.common.embedded;

import java.io.Serializable;

public class Constraints implements Serializable {

    private Object min;

    private Object max;

    private Long minLength;

    private Long maxLength;

    private String pattern;

    public Constraints() {

    }

    public Constraints(Object min, Object max, Long minLength, Long maxLength, String pattern) {
        this.min = min;
        this.max = max;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pattern = pattern;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public Long getMinLength() {
        return minLength;
    }

    public void setMinLength(Long minLength) {
        this.minLength = minLength;
    }

    public Long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Long maxLength) {
        this.maxLength = maxLength;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
