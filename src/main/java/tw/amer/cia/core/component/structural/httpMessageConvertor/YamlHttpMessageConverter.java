package tw.amer.cia.core.component.structural.httpMessageConvertor;

import tw.amer.cia.core.common.ErrorConstantLib;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class YamlHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> {

    public static final String MEDIA_TYPE_STRING = "application/x-yaml";
    public static final MediaType MEDIA_TYPE = MediaType.valueOf(MEDIA_TYPE_STRING);
    private final Yaml yaml;

    public YamlHttpMessageConverter() {
        super(MEDIA_TYPE);
        this.yaml = new Yaml();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputMessage.getBody().read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] yamlBytes = baos.toByteArray();

        try {
            // 使用 SnakeYAML 解析
            String yamlContent = new String(yamlBytes, StandardCharsets.UTF_8);
            yaml.load(yamlContent); // 如果這裡拋出異常，說明 YAML 格式有誤
            log.debug("YAML: {} \n Class = {}",yamlContent,clazz);

            if(StringUtils.equalsIgnoreCase(clazz.getSimpleName(),String.class.getSimpleName())){
                return (T)yamlContent ; // 轉換成字串
            }
            return yaml.loadAs(yamlContent, clazz);  // 轉換成指定物件
        } catch (YAMLException e) {
            throw new HttpMessageNotReadableException(
                    ErrorConstantLib.WEB_API_DOC_CAN_NOT_PARSE_YAML_DATA.getCompleteMessage() + "\n" +
                    e.getMessage(), e);
        }
    }

    @Override
    protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), StandardCharsets.UTF_8)) {
            log.info("Try Send Out");
            if(t instanceof String){
                // Pass Convert 僅進行格式檢查
                String yamlContent = (String) t;
                try {
                    // YAML格式檢查
                    yaml.load(yamlContent);
                    log.debug("YAML format is valid.");

                    writer.write(yamlContent);
                } catch (YAMLException e) {
                    // YAML 格式錯誤
                    throw new HttpMessageNotReadableException(
                            "Failed to write YAML data due to invalid format: " + e.getMessage(), e);
                }
            }else{
                // Convert by Object Type
                yaml.dump(t, writer);
            }
        }
    }
}