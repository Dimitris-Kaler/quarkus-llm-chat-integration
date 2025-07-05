package dim.kal.com.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

@ApplicationScoped
public class ClientConfiguration {

    @Produces
    @ApplicationScoped
    public Client createClient() {
        return ClientBuilder.newClient();
    }
}
