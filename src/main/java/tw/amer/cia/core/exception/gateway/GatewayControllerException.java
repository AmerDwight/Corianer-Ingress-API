package tw.amer.cia.core.exception.gateway;

import lombok.Data;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

@Data
@StandardException
public class GatewayControllerException extends Exception
{
    private HttpStatus httpStatus;
    private String GatewayType;

    public static GatewayControllerException createExceptionForHttp(HttpStatus httpStatus, String message)
    {
        GatewayControllerException gatewayControllerException = new GatewayControllerException(message);
        gatewayControllerException.setHttpStatus(httpStatus);
        return gatewayControllerException;
    }
}
