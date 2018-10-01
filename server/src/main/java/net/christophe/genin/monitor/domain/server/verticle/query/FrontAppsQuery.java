package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.monitor.domain.boundedcontext.domain.ConfigurationDomain;
import net.christophe.genin.monitor.domain.boundedcontext.domain.FrontsDomain;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IConfiguration;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IFrontApps;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Configuration;
import net.christophe.genin.monitor.domain.server.db.adapter.Adapters;
import rx.Observable;
import rx.Subscription;

import java.util.List;

public class FrontAppsQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(FrontAppsQuery.class);

    public static final String FIND_ALL = FrontAppsQuery.class.getName() + ".find.all";
    public static final String FIND_BY_GROUP = FrontAppsQuery.class.getName() + ".find.group.by";
    public static final String SERVICES_LIST = FrontAppsQuery.class.getName() + ".services.list";
    public static final String FIND_WEB_APP_BY_DOMAIN = FrontAppsQuery.class.getName() + ".find.by.domain";

    @Override
    public void start() {

        vertx.eventBus().consumer(FIND_ALL, msg -> {
            IFrontApps iFrontApps = Adapters.get().frontAppsHandler();
             new FrontsDomain(iFrontApps).findAll()
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error", err);
                                msg.fail(500, "error");
                            });
        });

        vertx.eventBus().consumer(FIND_BY_GROUP, msg ->
                getNpmFIlters()
                        .flatMap(npmFilters -> {
                                    IFrontApps iFrontApps = Adapters.get().frontAppsHandler();
                                    return new FrontsDomain(iFrontApps).findByGroup(npmFilters);
                                }
                        )
                        .reduce(new JsonArray(), JsonArray::add)
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error", err);
                                    msg.fail(500, "error");
                                }));

        vertx.eventBus().consumer(SERVICES_LIST, msg ->
                getNpmFIlters()
                        .flatMap(npmFilters -> {
                            IFrontApps iFrontApps = Adapters.get().frontAppsHandler();
                            return new FrontsDomain(iFrontApps).getServicesList(npmFilters);
                        })
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error", err);
                                    msg.fail(500, "error");
                                })
        );

        vertx.eventBus().<String>consumer(FIND_WEB_APP_BY_DOMAIN, msg -> {
                    String domain = msg.body().toLowerCase();
                    IFrontApps iFrontApps = Adapters.get().frontAppsHandler();
                    new FrontsDomain(iFrontApps).findBy(domain, getNpmFIlters())
                            .subscribe(
                                    msg::reply,
                                    err -> {
                                        logger.error("error", err);
                                        msg.fail(500, "error");
                                    });
                }

        );


        logger.info("started");
    }


    private Observable<List<String>> getNpmFIlters() {
        IConfiguration iConfiguration = Adapters.get().configurationHandler();
        return new ConfigurationDomain(iConfiguration).load()
                .toObservable()
                .map(Configuration::npmFilters);
    }


}
