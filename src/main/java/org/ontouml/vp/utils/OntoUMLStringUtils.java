package org.ontouml.vp.utils;

import org.ontouml.ontouml4j.model.Resource;
import org.ontouml.ontouml4j.model.MultilingualText;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OntoUMLStringUtils {

    private static final String NOT_AVAILABLE = "";

    private static String formatOptionalMultilingualText(Optional<MultilingualText> multilingualTextOpt) {
        Optional<Map.Entry<String, String>> firstEntryOpt = multilingualTextOpt.flatMap(
                mt -> mt.getMap().entrySet().stream().findFirst()
        );
        return firstEntryOpt
                .map(entry -> formatSingleLanguageString(entry.getKey(), entry.getValue()))
                .orElse(NOT_AVAILABLE);
    }

    private static String formatMultilingualTextList(List<MultilingualText> multilingualTexts) {
        if (multilingualTexts == null || multilingualTexts.isEmpty()) return NOT_AVAILABLE;
        return multilingualTexts.stream()
                .map(mt -> mt.getMap().entrySet().stream()
                        .map(entry -> formatSingleLanguageString(entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(", "))
                )
                .collect(Collectors.joining("\n"));
    }

    public static String formatLanguageString(Optional<Map<String, String>> langStringOpt) {
        return langStringOpt.flatMap(ls -> ls.entrySet().stream().findFirst())
                .map(entry -> formatSingleLanguageString(entry.getKey(), entry.getValue()))
                .orElse(NOT_AVAILABLE);
    }
    
    public static String formatMultiLanguageString(List<Map<String, String>> langStrings) {
        if (langStrings == null || langStrings.isEmpty()) return NOT_AVAILABLE;
        return langStrings.stream()
                .map(ls -> ls.entrySet().stream()
                        .map(entry -> formatSingleLanguageString(entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(", "))
                )
                .collect(Collectors.joining("\n"));
    }

    public static String formatResource(Optional<Resource> resourceOpt) {
        if (resourceOpt.isPresent()) {
            return resourceOpt.map(OntoUMLStringUtils::formatSingleResource)
                    .orElse(NOT_AVAILABLE);
        }
        return NOT_AVAILABLE;
    }

    public static String formatResource(Resource resource) {
        if (resource == null) return NOT_AVAILABLE;
        return formatSingleResource(resource);
    }

    public static String formatResources(Collection<Resource> resources) {
        if (resources == null || resources.isEmpty()) return NOT_AVAILABLE;
        return resources.stream()
                .map(OntoUMLStringUtils::formatSingleResource)
                .collect(Collectors.joining("\n"));
    }

    public static String formatStringList(List<String> list) {
        if (list == null || list.isEmpty()) return NOT_AVAILABLE;
        return String.join("\n", list);
    }

    /**
     * Formats a Map representing language tags and their corresponding text values.
     *
     * @param map The map of language tags to text values.
     * @return A formatted string, e.g., "English Text (@en)\nPortuguese Text (@pt)", or "".
     */
    public static String formatMultilingualTextMap(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return NOT_AVAILABLE;
        }
        return map.entrySet().stream()
                .map(entry -> formatSingleLanguageString(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    // -- Private helpers --

    private static String formatSingleLanguageString(String langTag, String value) {
        return value + (langTag != null && !langTag.isEmpty() ? " (@" + langTag + ")" : "");
    }

    private static String formatSingleResource(Resource resource) {
        String name = formatOptionalMultilingualText(Optional.ofNullable(resource.getName()));
        String uriString = Optional.ofNullable(resource.getUri())
                                    .map(URI::toString)
                                    .map(u -> " <" + u + ">")
                                    .orElse("");

        if (!name.equals(NOT_AVAILABLE) && !uriString.isEmpty()) {
            return name + uriString;
        } else if (!name.equals(NOT_AVAILABLE)) {
            return name;
        } else if (!uriString.isEmpty()) {
            return uriString.trim();
        } else {
            return NOT_AVAILABLE;
        }
    }
} 