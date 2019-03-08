package io.lastwill.tronishairdrop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.tronishairdrop.repositories.EosSnapshotEntryRepository;
import io.lastwill.tronishairdrop.repositories.TransferEntryRepository;
import io.mywish.eos.blockchain.EosBCModule;
import io.lastwill.tronishairdrop.repositories.EthSnapshotEntryRepository;
import io.lastwill.tronishairdrop.repositories.TronSnapshotEntryRepository;
import io.lastwill.tronishairdrop.service.EosSnapshotScanner;
import io.mywish.eos.blockchain.services.EosNetwork;
import io.mywish.eoscli4j.service.EosClientImpl;
import io.mywish.scanner.services.LastBlockMemoryPersister;
import io.mywish.web3.blockchain.Web3BCModule;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.*;

import java.net.URI;

@SpringBootApplication(exclude = {EosBCModule.class, Web3BCModule.class})
@ComponentScan({"io.mywish.scanner.services", "io.mywish.eos.blockchain.services"})
//@Import({
//        ScannerModule.class,
//        EventModule.class
//})
//@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class}, basePackages = "io.lastwill.eventscan")
//@EnableScheduling
//@EnableJpaRepositories(basePackageClasses = {NetworkRepository.class, LastBlockRepository.class})
//@EnableJpaRepositories("io.lastwill.eventscan")
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .addCommandLineProperties(true)
                .web(false)
                .sources(Application.class)
                .main(Application.class)
                .run(args);
    }

    @Value("${eos-snapshot-block}")
    private long eosSnapshotBlock;

    @Autowired
    private EosSnapshotEntryRepository eosSnapshotEntryRepository;
    @Autowired
    private EthSnapshotEntryRepository ethSnapshotEntryRepository;
    @Autowired
    private TronSnapshotEntryRepository tronSnapshotEntryRepository;
    @Autowired
    private TransferEntryRepository transferEntryRepository;

    @Bean(NetworkType.EOS_MAINNET_VALUE)
    public EosNetwork eosNetwork(
            @Value("${etherscanner.eos.tcp-url.mainnet}") URI eosTcpUri,
            @Value("${etherscanner.eos.rpc-url.mainnet}") URI eosRpcUri,
            CloseableHttpClient httpClient,
            ObjectMapper mapper
    ) throws Exception {
        return new EosNetwork(
                NetworkType.EOS_MAINNET,
                new EosClientImpl(eosTcpUri, httpClient, eosRpcUri, mapper)
        );
    }

    @Profile("reg-fetcher")
    @Configuration
    public class RegFetcherConfig {
        @Bean
        public CommandLineRunner clearDatabase() {
            return args -> {
                eosSnapshotEntryRepository.deleteAll();
                ethSnapshotEntryRepository.deleteAll();
                tronSnapshotEntryRepository.deleteAll();
            };
        }

        @Bean
        public EosSnapshotScanner eosSnapshotScanner(
                EosNetwork eosNetwork,
                @Value("${eos-reg-contract-created-block}") long startBlock
        ) {
            return new EosSnapshotScanner(
                    eosNetwork,
                    new LastBlockMemoryPersister(startBlock),
                    0,
                    0,
                    eosSnapshotBlock
            );
        }

        // todo: eth, tron scanners
    }

    @Profile("transfers-fetcher")
    @Configuration
    public class TransfersFetcherCinfig {
        @Bean
        public CommandLineRunner clearDatabase() {
            return args -> transferEntryRepository.deleteAll();
        }

        @Bean
        public EosSnapshotScanner eosSnapshotScanner(
                EosNetwork eosNetwork,
                @Value("${eosish-contract-created-block}") long startBlock
        ) {
            return new EosSnapshotScanner(
                    eosNetwork,
                    new LastBlockMemoryPersister(startBlock),
                    0,
                    0,
                    eosSnapshotBlock
            );
        }

        // todo: eth, tron scanners
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient closeableHttpClient(
            @Value("${io.lastwill.eventscan.backend.get-connection-timeout}") int getConnectionTimeout,
            @Value("${io.lastwill.eventscan.backend.connection-timeout}") int connectionTimeout,
            @Value("${io.lastwill.eventscan.backend.socket-timeout}") int socketTimeout) {

        return HttpClientBuilder
                .create()
                .setMaxConnPerRoute(50)
                .setMaxConnTotal(200)
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(socketTimeout)
                                .setConnectionRequestTimeout(getConnectionTimeout)
                                .setCookieSpec(CookieSpecs.STANDARD)
                                .build()
                )
                .setConnectionManagerShared(true)
                .build();
    }

    //    @Bean
//    public OkHttpClient okHttpClient(
//            @Value("${io.lastwill.eventscan.backend.socket-timeout}") long socketTimeout,
//            @Value("${io.lastwill.eventscan.backend.connection-timeout}") long connectionTimeout
//    ) {
//        return new OkHttpClient.Builder()
//                .writeTimeout(socketTimeout, TimeUnit.MILLISECONDS)
//                .readTimeout(socketTimeout, TimeUnit.MILLISECONDS)
//                .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
//                .build();
//    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                // TODO: remove it!
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
    }
}
