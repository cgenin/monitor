package net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure;

import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Configuration;
import rx.Single;

public interface IConfiguration {

    Single<Configuration> load();

    Single<Boolean> save(Configuration conf);
}
