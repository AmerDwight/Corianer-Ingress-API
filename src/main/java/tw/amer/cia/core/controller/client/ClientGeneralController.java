package tw.amer.cia.core.controller.client;

import tw.amer.cia.core.component.functional.coriander.CallHostApiComponent;
import tw.amer.cia.core.component.structural.annotation.ClientRestController;
import tw.amer.cia.core.exception.CiaProcessorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@ClientRestController
public class ClientGeneralController
{
    @Autowired
    CallHostApiComponent callHostApiComponent;

    @RequestMapping(value = "/remote/check/host")
    public Object checkHostAlive() throws CiaProcessorException
    {
        boolean isHostAlive = callHostApiComponent.checkHostAlive();
        return isHostAlive;
    }
}
