package tw.amer.cia.core.component.functional.gateway.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GatewayPluginTemplateLoader {
    private Map<String, Class<? extends GatewayPluginTemplate>> templateMap = new HashMap<>();
    public static final ObjectMapper ObjectMapperForPlugins = new ObjectMapper();

    public GatewayPluginTemplateLoader(String gwPluginPackageUrl) {
        final String basePackage = gwPluginPackageUrl;
        loadTemplates(basePackage);
    }

    private void loadTemplates(String gwPluginPackageUrl) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(gwPluginPackageUrl))
                .setScanners(new SubTypesScanner(false)));

        Set<Class<? extends GatewayPluginTemplate>> classes = reflections.getSubTypesOf(GatewayPluginTemplate.class);
        for (Class<? extends GatewayPluginTemplate> clazz : classes) {
            templateMap.put(clazz.getSimpleName(), clazz);
        }

    }

    public GatewayPluginTemplate getPluginTemplate(String templateName) {
        Class<? extends GatewayPluginTemplate> templateClass = templateMap.get(templateName);
        if (templateClass == null) {
            throw new RuntimeException("No template found with name: " + templateName);
        }
        try {
            return templateClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + templateName, e);
        }
    }

    public static String base64Encode(Map<String, String> data) throws JsonProcessingException {
        String json = ObjectMapperForPlugins.writeValueAsString(data);
        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    public static Map<String, String> base64Decode(String base64Data) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        String json = new String(decodedBytes);
        return ObjectMapperForPlugins.readValue(json, new TypeReference<Map<String, String>>() {});
    }

}