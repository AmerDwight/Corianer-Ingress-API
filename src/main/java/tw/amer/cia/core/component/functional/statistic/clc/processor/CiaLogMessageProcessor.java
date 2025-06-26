package tw.amer.cia.core.component.functional.statistic.clc.processor;

import tw.amer.cia.core.model.pojo.service.host.statistic.clc.ClcLogMessage;

public interface CiaLogMessageProcessor<T> {
    ClcLogMessage processLog(T logData);
}