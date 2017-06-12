package com.twosigma.testing.webtau.reporter;

import com.twosigma.testing.reporter.TestStepPayload;

import java.util.Collections;
import java.util.Map;

/**
 * @author mykola
 */
public class ScreenshotStepPayload implements TestStepPayload {
    private String base64png;

    public ScreenshotStepPayload(String base64png) {
        this.base64png = base64png;
    }

    public String getBase64png() {
        return base64png;
    }

    @Override
    public Map<String, ?> toMap() {
        return Collections.singletonMap("base64png", base64png);
    }
}
