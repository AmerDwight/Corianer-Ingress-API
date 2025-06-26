package tw.amer.cia.core.exception;

import lombok.Data;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

@StandardException
@Data
public class DataSourceAccessException extends Exception
{
    private HttpStatus httpStatus;

    public static DataSourceAccessException createExceptionForHttp(HttpStatus httpStatus, String message)
    {
        DataSourceAccessException dataSourceAccessException = new DataSourceAccessException(message);
        dataSourceAccessException.setHttpStatus(httpStatus);
        return dataSourceAccessException;
    }
}
