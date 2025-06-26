package tw.amer.cia.core.component.structural.resource.sqlCommandLoader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

@Slf4j
public class H2SqlCommander extends SqlCommander
{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String SQL_COMMAND_PATH_DIRECTORY = "h2/";

    public H2SqlCommander(ResourceLoader resourceLoader)
    {
        super(resourceLoader);
    }

    @Override
    public boolean initial()
    {
        boolean procedureSuccess = true;
        log.info("Initializing H2 Db database...");

        try
        {
            String sqlCommand = this.loadSql(this.SQL_COMMAND_INITIAL_SQL_COMMAND_FILE);
            if (StringUtils.isNotEmpty(sqlCommand))
            {
                log.debug("Executing SQL command: {}", sqlCommand);
                jdbcTemplate.execute(sqlCommand);
                log.info("SQL command executed successfully.");
            } else
            {
                log.warn("SQL command is empty or null, skipping execution.");
            }
        } catch (Exception e)
        {
            log.error("Error executing SQL command", e);
            procedureSuccess = false;
        }

        log.info("Initialization completed with status: {}", procedureSuccess);
        return procedureSuccess;
    }

    protected String loadSql(String fileName)
    {
        try
        {
            String sql = this.loadSql(this.SQL_COMMAND_PATH_DIRECTORY, fileName);
            return sql;
        } catch (IOException e)
        {
            return "";
        }
    }
}