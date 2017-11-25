package net.christophe.genin.domain.server.db.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ESSource {

    private final String host;
    private final Integer port;

    public ESSource(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public TransportClient client() throws UnknownHostException {
        return new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));

    }


}
