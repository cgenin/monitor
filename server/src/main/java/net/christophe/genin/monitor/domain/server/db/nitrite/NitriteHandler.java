package net.christophe.genin.monitor.domain.server.db.nitrite;

import org.dizitart.no2.NitriteCollection;
import rx.Single;

public abstract class NitriteHandler {

    protected final NitriteDbs nitriteDbs;

    public NitriteHandler(NitriteDbs nitriteDbs) {
        this.nitriteDbs = nitriteDbs;
    }

    protected abstract NitriteCollection getCollection();

    public Single<Integer> removeAll() {
        return NitriteDbs.removeAll(getCollection());
    }
}
