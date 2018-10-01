package net.christophe.genin.monitor.domain.server.model;

/**
 * this class represents the dependencies on differents domain objects.
 */
public abstract class Dependency {

    private final String resource;
    private final String usedBy;



    public Dependency(String resource, String usedBy) {
        this.resource = resource;
        this.usedBy = usedBy;
    }

    public String resource(){
        return resource;
    }

    public String usedBy(){
        return usedBy;
    }
}
