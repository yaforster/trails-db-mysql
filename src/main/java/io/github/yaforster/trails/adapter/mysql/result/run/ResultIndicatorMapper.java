package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.app.GlobalMapperConfig;
import io.github.yaforster.trails.core.data.ResultIndicator;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface ResultIndicatorMapper {

    ResultIndicator toResultIndicator(TestRunStatus status);

    TestRunStatus toTestRunStatus(ResultIndicator indicator);

}
