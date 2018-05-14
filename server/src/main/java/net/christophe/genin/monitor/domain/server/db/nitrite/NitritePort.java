package net.christophe.genin.monitor.domain.server.db.nitrite;

import org.dizitart.no2.NitriteCollection;
import rx.Single;

/**
 * Interface for Port which use nitriteDb.
 */
public abstract class NitritePort {

    protected final NitriteDbs nitriteDbs;

    public NitritePort(NitriteDbs nitriteDbs) {
        this.nitriteDbs = nitriteDbs;
    }

    /**
     * Getter of curret collection.
     *
     * @return the collection.
     */
    protected abstract NitriteCollection getCollection();

    /**
     * Suppress all datas in a collection.
     *
     * @return the number of deleted rows.
     */
    public Single<Integer> removeAll() {
        return NitriteDbs.removeAll(getCollection());
    }
}
