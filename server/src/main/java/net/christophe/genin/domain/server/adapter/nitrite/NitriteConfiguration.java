package net.christophe.genin.domain.server.adapter.nitrite;

import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.model.Configuration;
import rx.Single;

import java.util.Optional;

public class NitriteConfiguration extends Configuration {

    public static Single<Configuration> load() {
        return Single.fromCallable(() -> Optional.ofNullable(Dbs.instance
                .repository(NitriteConfiguration.class)
                .find().firstOrDefault())
                .orElseGet(NitriteConfiguration::new));
    }
}
