package net.christophe.genin.domain.server.db.nitrite;

import org.dizitart.no2.NitriteCollection;
import rx.Single;

public abstract class NitriteHandler {

    protected final Dbs dbs;

    public NitriteHandler(Dbs dbs) {
        this.dbs = dbs;
    }

    protected abstract NitriteCollection getCollection();

    public Single<Integer> removeAll() {
        return Dbs.removeAll(getCollection());
    }
}
