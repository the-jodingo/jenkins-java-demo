package com.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Five small tests. Jenkins reads the results from
 * target/surefire-reports/ after {@code mvn test}.
 */
class AppTest {

    @Test
    @DisplayName("banner contains the app name")
    void bannerContainsAppName() {
        assertTrue(App.renderBanner().contains("JENKINS JAVA DEMO"));
    }

    @Test
    @DisplayName("banner box is present and square")
    void bannerHasBoxBorders() {
        String banner = App.renderBanner();
        assertTrue(banner.contains("+---"));
        for (String line : banner.split("\n")) {
            String plain = line.replaceAll("\u001B\\[[;\\d]*m", "");
            if (plain.startsWith("+---") || plain.startsWith("|")) {
                assertEquals(60, plain.length(), "box lines must be 60 chars wide");
            }
        }
    }

    @Test
    @DisplayName("pipeline lists six stages")
    void pipelineHasSixStages() {
        List<String> stages = App.stages();
        assertEquals(6, stages.size());
        assertEquals("Checkout source", stages.get(0));
        assertEquals("Archive artifacts", stages.get(5));
    }

    @Test
    @DisplayName("build number falls back to local when BUILD_NUMBER is absent")
    void buildNumberFallsBackToLocal() {
        String number = App.buildNumber();
        assertFalse(number.isEmpty());
        assertTrue(number.equals("local") || number.matches("\\d+"));
    }

    @Test
    @DisplayName("details block reports the java version")
    void detailsShowJavaVersion() {
        String details = App.renderDetails(LocalDateTime.now());
        assertTrue(details.contains(System.getProperty("java.version")));
    }
}
