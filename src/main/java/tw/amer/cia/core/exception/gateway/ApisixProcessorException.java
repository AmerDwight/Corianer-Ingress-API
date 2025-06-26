package tw.amer.cia.core.exception.gateway;

import lombok.Data;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

@Data
@StandardException
public class ApisixProcessorException extends GatewayControllerException
{
    private final String GatewayType = "APISIX";
    private HttpStatus httpStatus;

    public static ApisixProcessorException createExceptionForHttp(HttpStatus httpStatus, String message)
    {
        ApisixProcessorException dataSourceAccessException = new ApisixProcessorException(message);
        dataSourceAccessException.setHttpStatus(httpStatus);
        return dataSourceAccessException;
    }
}
