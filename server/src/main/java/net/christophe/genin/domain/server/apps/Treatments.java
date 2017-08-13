package net.christophe.genin.domain.server.apps;

public enum Treatments {
    PROJECTS(0),
    TABLES(1),
    END(2);

    private final Integer state;

    Treatments(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return state;
    }
}
