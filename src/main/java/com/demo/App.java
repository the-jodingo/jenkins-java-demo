package com.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Jenkins Java Demo.
 *
 * <p>Prints a coloured build banner to the console and writes a dark-themed
 * HTML build report to {@code target/site/build-report.html}.
 *
 * <p>Run it with:
 * <pre>
 *   mvn clean package
 *   java -jar target/jenkins-java-demo.jar
 * </pre>
 */
public final class App {

    // ---- ANSI colours -----------------------------------------------------
    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String GREEN  = "\u001B[38;5;41m";
    private static final String CYAN   = "\u001B[38;5;51m";
    private static final String GREY   = "\u001B[38;5;245m";
    private static final String ORANGE = "\u001B[38;5;208m";

    private static final String APP_NAME    = "JENKINS JAVA DEMO";
    private static final String APP_VERSION = "1.0.0";

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private App() {
        // utility class
    }

    public static void main(String[] args) {
        LocalDateTime startedAt = LocalDateTime.now();
        Path reportPath = Paths.get("target", "site", "build-report.html");

        System.out.println(renderBanner());
        System.out.println(renderDetails(startedAt));
        System.out.println(renderStages());

        try {
            writeHtmlReport(reportPath, startedAt);
            System.out.println(GREEN + "  HTML report written to " + reportPath + RESET + "\n");
        } catch (IOException e) {
            System.out.println(ORANGE + "  Could not write HTML report: " + e.getMessage() + RESET + "\n");
        }
    }

    /** The boxed title. Kept public so the tests can assert on it. */
    public static String renderBanner() {
        String line = "+----------------------------------------------------------+";
        return "\n" + CYAN + line + RESET + "\n"
             + CYAN + "|" + RESET + BOLD
             + center(APP_NAME, 58)
             + RESET + CYAN + "|" + RESET + "\n"
             + CYAN + "|" + RESET + GREY
             + center("Java 11  |  Maven  |  Jenkins CI/CD", 58)
             + RESET + CYAN + "|" + RESET + "\n"
             + CYAN + line + RESET + "\n";
    }

    /** Build number, java version, OS, start time. */
    public static String renderDetails(LocalDateTime startedAt) {
        StringBuilder sb = new StringBuilder();
        sb.append(GREY).append("  Build number : ").append(RESET)
          .append(CYAN).append(buildNumber()).append(RESET).append('\n');
        sb.append(GREY).append("  Java version : ").append(RESET)
          .append(System.getProperty("java.version")).append('\n');
        sb.append(GREY).append("  OS           : ").append(RESET)
          .append(System.getProperty("os.name")).append('\n');
        sb.append(GREY).append("  Version      : ").append(RESET)
          .append(APP_VERSION).append('\n');
        sb.append(GREY).append("  Started at   : ").append(RESET)
          .append(startedAt.format(STAMP)).append('\n');
        return sb.toString();
    }

    /** The pipeline checklist. */
    public static String renderStages() {
        List<String> stages = stages();
        StringBuilder sb = new StringBuilder();
        sb.append('\n').append(BOLD).append("  PIPELINE").append(RESET).append('\n');
        for (String stage : stages) {
            sb.append(GREEN).append("   [OK] ").append(RESET).append(stage).append('\n');
        }
        sb.append(GREEN).append(BOLD).append("\n  BUILD SUCCESS").append(RESET).append('\n');
        return sb.toString();
    }

    /** Stage names shown in the banner and in the HTML report. */
    public static List<String> stages() {
        List<String> list = new ArrayList<>();
        list.add("Checkout source");
        list.add("Compile");
        list.add("Unit tests");
        list.add("Package jar");
        list.add("Run demo");
        list.add("Archive artifacts");
        return list;
    }

    /** Jenkins injects BUILD_NUMBER; fall back to "local" when run by hand. */
    public static String buildNumber() {
        String number = System.getenv("BUILD_NUMBER");
        return (number == null || number.trim().isEmpty()) ? "local" : number.trim();
    }

    /** Right/left pads text inside a fixed width, so the box stays square. */
    static String center(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int pad = width - text.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }

    private static void writeHtmlReport(Path path, LocalDateTime startedAt) throws IOException {
        Files.createDirectories(path.getParent());

        StringBuilder rows = new StringBuilder();
        for (String stage : stages()) {
            rows.append("      <tr><td class=\"ok\">PASS</td><td>")
                .append(stage)
                .append("</td></tr>\n");
        }

        String html = "<!DOCTYPE html>\n"
            + "<html lang=\"en\">\n<head>\n"
            + "<meta charset=\"utf-8\">\n"
            + "<title>Build Report - " + APP_NAME + "</title>\n"
            + "<style>\n"
            + "  :root { color-scheme: dark; }\n"
            + "  body { margin:0; min-height:100vh; display:grid; place-items:center;\n"
            + "         font-family: ui-sans-serif, -apple-system, 'Segoe UI', sans-serif;\n"
            + "         background: radial-gradient(1200px 600px at 20% -10%, #12321c 0%, #070b08 55%);\n"
            + "         color:#d8f0d8; }\n"
            + "  .card { width:min(680px, 92vw); padding:40px; border-radius:20px;\n"
            + "          background:rgba(10,20,12,.82); border:1px solid #2a4a2a;\n"
            + "          box-shadow:0 30px 80px rgba(0,0,0,.6), inset 0 1px 0 rgba(79,255,79,.08); }\n"
            + "  h1 { margin:0 0 4px; font-size:26px; letter-spacing:.16em; color:#4fff4f; }\n"
            + "  .sub { color:#5a6f5a; font-size:13px; letter-spacing:.08em; margin-bottom:28px; }\n"
            + "  .pill { display:inline-block; padding:6px 14px; border-radius:999px;\n"
            + "          background:rgba(79,255,79,.12); border:1px solid rgba(79,255,79,.35);\n"
            + "          color:#4fff4f; font-size:12px; letter-spacing:.12em; margin-bottom:24px; }\n"
            + "  table { width:100%; border-collapse:collapse; font-size:14px; }\n"
            + "  td { padding:11px 8px; border-bottom:1px solid rgba(42,74,42,.6); }\n"
            + "  td.ok { color:#4fff4f; width:70px; font-size:12px; letter-spacing:.1em; }\n"
            + "  .meta { margin-top:26px; font-size:12px; color:#5a6f5a; line-height:1.9; }\n"
            + "</style>\n</head>\n<body>\n"
            + "  <div class=\"card\">\n"
            + "    <h1>" + APP_NAME + "</h1>\n"
            + "    <div class=\"sub\">Java 11 &middot; Maven &middot; Jenkins CI/CD</div>\n"
            + "    <div class=\"pill\">BUILD SUCCESS</div>\n"
            + "    <table>\n" + rows + "    </table>\n"
            + "    <div class=\"meta\">\n"
            + "      Build number: " + buildNumber() + "<br>\n"
            + "      Version: " + APP_VERSION + "<br>\n"
            + "      Started: " + startedAt.format(STAMP) + "<br>\n"
            + "      Java: " + System.getProperty("java.version") + "\n"
            + "    </div>\n"
            + "  </div>\n"
            + "</body>\n</html>\n";

        Files.write(path, html.getBytes(StandardCharsets.UTF_8));
    }
}
