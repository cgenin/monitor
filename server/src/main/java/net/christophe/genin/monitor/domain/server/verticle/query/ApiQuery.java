package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.boundedcontext.domain.ApiDomain;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IApi;
import net.christophe.genin.monitor.domain.server.db.adapter.Adapters;


/**
 * Read operation for Api.
 */
public class ApiQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ApiQuery.class);

    public static final String FIND = ApiQuery.class.getName() + ".find";

    @Override
    public void start() {
        vertx.eventBus().consumer(FIND, msg -> {
            IApi iApi = Adapters.get().apiHandler();
            new ApiDomain(iApi).findAll()

                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + FIND, err);
                                msg.fail(500, "Error in query");
                            }
                    );
        });

        logger.info("started");
    }
}
