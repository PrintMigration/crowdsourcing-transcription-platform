package mainApplication.fusionAuth;

import io.fusionauth.client.FusionAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FusionAuthClientConfig {

    @Value("${fusionAuth.apiKey}")
    private String apiKey;

    @Value("${fusionAuth.baseUrl}")
    private String baseUrl;

    @Bean
    public FusionAuthClient setupClient() {
        FusionAuthClient fusionAuthClient = new FusionAuthClient(apiKey, baseUrl);
        return fusionAuthClient;
    }
}
