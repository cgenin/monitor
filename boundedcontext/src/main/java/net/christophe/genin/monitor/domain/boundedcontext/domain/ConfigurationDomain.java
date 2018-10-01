package net.christophe.genin.monitor.domain.boundedcontext.domain;

import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IConfiguration;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Configuration;
import rx.Single;

public class ConfigurationDomain {

    private final IConfiguration configurationDao;

    public ConfigurationDomain(IConfiguration configurationDao) {
        this.configurationDao = configurationDao;
    }

    public  Single<Configuration> load() {
        return configurationDao.load();
    }

    public  Single<Boolean> save(Configuration conf) {
        return configurationDao.save(conf);
    }
}
