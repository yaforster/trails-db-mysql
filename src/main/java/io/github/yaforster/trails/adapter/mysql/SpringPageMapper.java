package io.github.yaforster.trails.adapter.mysql;

import io.github.yaforster.trails.core.PagedResult;
import org.springframework.data.domain.Page;

public final class SpringPageMapper {

    private SpringPageMapper() {}

    public static <T> PagedResult<T> toPagedResult(Page<T> page) {
        return new PagedResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

}
