package tw.amer.cia.core.exception;

import lombok.Data;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

@StandardException
@Data
public class CiaProcessorException extends Exception
{
    private HttpStatus httpStatus;

    public static CiaProcessorException createExceptionForHttp(HttpStatus httpStatus, String message)
    {
        CiaProcessorException dataSourceAccessException = new CiaProcessorException(message);
        dataSourceAccessException.setHttpStatus(httpStatus);
        return dataSourceAccessException;
    }
}
