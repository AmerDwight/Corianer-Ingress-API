package tw.amer.cia.core.controller.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import tw.amer.cia.core.component.functional.coriander.CallClientApiComponent;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.pojo.component.property.NodeStatusDTO;

import java.util.List;
import java.util.Map;

@HostRestController
public class HostGeneralController {
    @Autowired
    CallClientApiComponent callClientApiComponent;

    @RequestMapping(value = "/remote/check/client")
    public Object checkClientAlive() throws CiaProcessorException {
        Map<String, List<NodeStatusDTO>> returnStatus = callClientApiComponent.tryCheckAllClientAliveBroadcast();
        return returnStatus;
    }
}
