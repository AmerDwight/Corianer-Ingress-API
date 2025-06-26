package tw.amer.cia.core.component.functional.statistic.clc.supplier;

import tw.amer.cia.core.model.pojo.service.host.statistic.clc.ClcLogMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClcHistoryLogSupplierLib {

    @Autowired
    ApplicationContext applicationContext;

    public List<ClcHistoryLogSupplier> getSuppliers() {
        // 從 ApplicationContext 獲取所有 DataSupplier 的實現
        Map<String, ClcHistoryLogSupplier> supplierMap =
                applicationContext.getBeansOfType(ClcHistoryLogSupplier.class);
        return new ArrayList<>(supplierMap.values());
    }

    public List<ClcLogMessage> getAllHistoryCiaLogMessage() {
        return getSuppliers().stream().flatMap(supplier -> supplier.supply().stream()).collect(Collectors.toList());
    }
}
