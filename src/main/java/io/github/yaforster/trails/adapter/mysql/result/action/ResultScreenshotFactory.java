package io.github.yaforster.trails.adapter.mysql.result.action;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Component
public class ResultScreenshotFactory {

    public Optional<ResultScreenshotEntity> fromBase64(Long resultId, String base64Screenshot) {
        return parseScreenshot(base64Screenshot).map(payload -> toEntity(resultId, payload));
    }

    private ResultScreenshotEntity toEntity(Long resultId, ScreenshotPayload screenshotPayload) {
        String extension = fileExtensionFor(screenshotPayload.contentType());
        String fileName = "action-result-" + resultId + extension;
        return new ResultScreenshotEntity().setResultId(resultId)
                                           .setFileName(fileName)
                                           .setContentType(screenshotPayload.contentType())
                                           .setContent(screenshotPayload.content());
    }

    private Optional<ScreenshotPayload> parseScreenshot(String base64Screenshot) {
        if (base64Screenshot == null || base64Screenshot.isBlank()) {
            return Optional.empty();
        }
        String screenshotValue = base64Screenshot.trim();
        EncodedScreenshot encodedScreenshot = readEncodedScreenshot(screenshotValue);
        return decodeContent(encodedScreenshot);
    }

    private EncodedScreenshot readEncodedScreenshot(String screenshotValue) {
        int commaIndex = screenshotValue.indexOf(',');
        if (!screenshotValue.startsWith("data:") || commaIndex <= 0) {
            // Browser screenshots may contain bare Base64 rather than a data URI; these are PNG images.
            return new EncodedScreenshot(screenshotValue, "image/png");
        }
        String metadata = screenshotValue.substring(5, commaIndex);
        String contentType = readContentType(metadata);
        String encodedContent = screenshotValue.substring(commaIndex + 1);
        return new EncodedScreenshot(encodedContent, contentType);
    }

    private String readContentType(String metadata) {
        String[] metadataParts = metadata.split(";");
        if (metadataParts.length == 0 || metadataParts[0].isBlank()) {
            return "image/png";
        }
        return metadataParts[0];
    }

    private Optional<ScreenshotPayload> decodeContent(EncodedScreenshot screenshot) {
        try {
            byte[] decoded = Base64.getDecoder()
                                   .decode(screenshot.content());
            if (decoded.length == 0) {
                return Optional.empty();
            }
            ScreenshotPayload payload = new ScreenshotPayload(decoded, screenshot.contentType());
            return Optional.of(payload);
        } catch (IllegalArgumentException ignored) {
            // An unusable screenshot must not prevent the action result itself from being stored.
            return Optional.empty();
        }
    }

    private String fileExtensionFor(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".png";
        };
    }

}
