package com.blog;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.http.server.PathContainer;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityPathTest {

    @Test
    public void testAdminSecurityPathMatching() {
        PathPatternParser parser = new PathPatternParser();
        PathPattern exactSettingsPattern = parser.parse("/api/settings/admin");
        PathPattern wildcardSettingsPattern = parser.parse("/api/settings/admin/**");
        PathPattern adminWildcardPattern = parser.parse("/api/admin/**");

        PathContainer pathSettings = PathContainer.parsePath("/api/settings/admin");
        PathContainer pathUpload = PathContainer.parsePath("/api/admin/upload");
        PathContainer pathMessages = PathContainer.parsePath("/api/admin/messages/1");

        assertTrue(exactSettingsPattern.matches(pathSettings), "Must match /api/settings/admin");
        assertTrue(adminWildcardPattern.matches(pathUpload), "Must match /api/admin/upload");
        assertTrue(adminWildcardPattern.matches(pathMessages), "Must match /api/admin/messages/1");
    }
}
