package tw.amer.cia.core.component.functional.statistic.clc.supplier;

import tw.amer.cia.core.model.pojo.service.host.statistic.clc.ClcLogMessage;

import java.util.Collection;

public interface ClcHistoryLogSupplier {
    Collection<ClcLogMessage> supply();
}
