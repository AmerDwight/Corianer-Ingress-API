package tw.amer.cia.core.component.structural.info;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

@Slf4j
@Component
public class MethodCounter
{
    @Value("${coriander-ingress-api.setting.log-address}")
    @Setter
    private String SYSTEM_LOG_PATH;

    private static final  String DEFAULT_METHOD_COUNT_FILE = "MethodCounter.txt";

    private String realMethodCountFilePath;

    private final ConcurrentMap<String, Integer> methodCounts = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void incrementMethodCount(String methodName)
    {
        methodCounts.merge(methodName, 1, Integer::sum);
    }

    private void writeLogToFile()
    {
        StringBuilder logBuilder = new StringBuilder();
        methodCounts.forEach((method, count) ->
        {
            logBuilder.append("Method: ").append(method).append(", Count: ").append(count).append("\n");
        });


        try
        {
            String filePath = getBuiltRealMethodCountFilePath();
            Files.write(Paths.get(filePath), logBuilder.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e)
        {
            log.error("Error writing to log file: " + e.getMessage());
        }
    }

    // 開始記錄
    public void startLogging(int period, TimeUnit unit)
    {
        scheduler.scheduleAtFixedRate(this::writeLogToFile, 0, period, unit);
    }

    // 停止記錄
    public void stopLogging()
    {
        scheduler.shutdown();
        try
        {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS))
            {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private String getBuiltRealMethodCountFilePath(){
        if(StringUtils.isNotBlank(SYSTEM_LOG_PATH)){
            realMethodCountFilePath = SYSTEM_LOG_PATH + DEFAULT_METHOD_COUNT_FILE;
        }else{
            realMethodCountFilePath = "./" + DEFAULT_METHOD_COUNT_FILE;
        }
        return realMethodCountFilePath;
    }
}