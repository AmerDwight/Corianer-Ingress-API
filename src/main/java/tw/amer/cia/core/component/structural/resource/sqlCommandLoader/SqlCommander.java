package tw.amer.cia.core.component.structural.resource.sqlCommandLoader;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public abstract class SqlCommander
{

    protected final ResourceLoader resourceLoader;
    protected final String SQL_COMMAND_INITIAL_SQL_COMMAND_FILE = "initial.sql";
    protected String SQL_COMMAND_PATH_PREFIX = "sqlCommand/";

    public SqlCommander(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public abstract boolean initial();

    protected String loadSql(String directoryPath, String fileName) throws IOException
    {
        return loadResourceAsString(SQL_COMMAND_PATH_PREFIX + directoryPath + fileName);
    }

    protected String loadResourceAsString(String resourcePath) throws IOException
    {
        Resource resource = resourceLoader.getResource("classpath:" + resourcePath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream())))
        {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}