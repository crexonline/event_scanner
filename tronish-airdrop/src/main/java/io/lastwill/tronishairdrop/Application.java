package io.lastwill.tronishairdrop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.tronishairdrop.repositories.EosSnapshotEntryRepository;
import io.lastwill.tronishairdrop.repositories.EthSnapshotEntryRepository;
import io.lastwill.tronishairdrop.repositories.TransferEntryRepository;
import io.lastwill.tronishairdrop.repositories.TronSnapshotEntryRepository;
import io.lastwill.tronishairdrop.service.EosSnapshotScanner;
import io.lastwill.tronishairdrop.service.EthSnapshotScanner;
import io.lastwill.tronishairdrop.service.TronSnapshotScanner;
import io.mywish.eos.blockchain.EosBCModule;
import io.mywish.eos.blockchain.services.EosNetwork;
import io.mywish.eoscli4j.service.EosClientImpl;
import io.mywish.scanner.services.LastBlockFilePersister;
import io.mywish.tron.blockchain.TronBCModule;
import io.mywish.tron.blockchain.services.TronNetwork;
import io.mywish.troncli4j.service.TronClientImpl;
import io.mywish.web3.blockchain.Web3BCModule;
import io.mywish.web3.blockchain.parity.Web3jEx;
import io.mywish.web3.blockchain.service.Web3Network;
import okhttp3.OkHttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.web3j.protocol.http.HttpService;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(exclude = {EosBCModule.class, Web3BCModule.class, TronBCModule.class})
@ComponentScan({
        "io.lastwill.tronishairdrop",
        "io.mywish.scanner.services",
        "io.mywish.eos.blockchain.services",
        "io.mywish.web3.blockchain.service",
        "io.mywish.tron.blockchain.services",
})
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

    @Value("${eth-snapshot-block}")
    private long ethSnapshotBlock;

    @Value("${tron-snapshot-block}")
    private long tronSnapshotBlock;

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

    @Bean(NetworkType.ETHEREUM_MAINNET_VALUE)
    public Web3Network ethNetwork(
            @Value("${io.lastwill.eventscan.web3-url.ethereum}") String web3Url,
            OkHttpClient httpClient
    ) {
        return new Web3Network(
                NetworkType.ETHEREUM_MAINNET,
                Web3jEx.build(new HttpService(web3Url, httpClient, false)),
                0
        );
    }

    @Bean(NetworkType.TRON_MAINNET_VALUE)
    public TronNetwork tronNetwork(
            @Value("${etherscanner.tron.full-rpc-url.mainnet}") URI fullNode,
            @Value("${etherscanner.tron.event-rpc-url.mainnet}") URI eventNode,
            CloseableHttpClient httpClient,
            ObjectMapper mapper
    ) throws Exception {
        return new TronNetwork(
                NetworkType.TRON_MAINNET,
                new TronClientImpl(httpClient, fullNode, eventNode, mapper)
        );
    }

    @Profile("reg-fetcher")
    @Configuration
    public class RegFetcherConfig {
        @Bean
        public EosSnapshotScanner eosSnapshotScanner(
                EosNetwork eosNetwork,
                @Value("${etherscanner.start-block-dir}") String startBlockDir,
                @Value("${eos-reg-contract-created-block:#{null}}") long startBlock
        ) {
            return new EosSnapshotScanner(
                    eosNetwork,
                    new LastBlockFilePersister(NetworkType.EOS_MAINNET, startBlockDir, startBlock),
                    0,
                    0,
                    eosSnapshotBlock
            );
        }

        @Bean
        public EthSnapshotScanner ethSnapshotScanner(
                Web3Network ethNetwork,
                @Value("${etherscanner.start-block-dir}") String startBlockDir,
                @Value("${eth-reg-contract-created-block:#{null}}") long startBlock
        ) {
            return new EthSnapshotScanner(
                    ethNetwork,
                    new LastBlockFilePersister(NetworkType.ETHEREUM_MAINNET, startBlockDir, startBlock),
                    0,
                    0,
                    ethSnapshotBlock
            );
        }

        @Bean
        public TronSnapshotScanner tronSnapshotScanner(
                TronNetwork tronNetwork,
                @Value("${etherscanner.start-block-dir}") String startBlockDir,
                @Value("${tron-reg-contract-created-block:#{null}}") long startBlock
        ) {
            return new TronSnapshotScanner(
                    tronNetwork,
                    new LastBlockFilePersister(NetworkType.TRON_MAINNET, startBlockDir, startBlock),
                    0,
                    0,
                    tronSnapshotBlock
            );
        }
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
                @Value("${etherscanner.start-block-dir}") String startBlockDir,
                @Value("${eosish-contract-created-block:#{null}}") long startBlock
        ) {
            return new EosSnapshotScanner(
                    eosNetwork,
                    new LastBlockFilePersister(NetworkType.EOS_MAINNET, startBlockDir, startBlock),
                    0,
                    0,
                    eosSnapshotBlock
            );
        }

        @Bean
        public EthSnapshotScanner ethSnapshotScanner(
                Web3Network ethNetwork,
                @Value("${etherscanner.start-block-dir}") String startBlockDir,
                @Value("${wish-contract-created-block:#{null}}") long startBlock
        ) {
            return new EthSnapshotScanner(
                    ethNetwork,
                    new LastBlockFilePersister(NetworkType.ETHEREUM_MAINNET, startBlockDir, startBlock),
                    0,
                    0,
                    ethSnapshotBlock
            );
        }

        @Bean
        public TronSnapshotScanner tronSnapshotScanner(
                TronNetwork tronNetwork,
                @Value("${etherscanner.start-block-dir:#{null}}") String startBlockDir
        ) {
            return new TronSnapshotScanner(
                    tronNetwork,
                    new LastBlockFilePersister(NetworkType.TRON_MAINNET, startBlockDir, 1L),
                    0,
                    0,
                    tronSnapshotBlock
            );
        }
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

    @Bean
    public OkHttpClient okHttpClient(
            @Value("${io.lastwill.eventscan.backend.socket-timeout}") long socketTimeout,
            @Value("${io.lastwill.eventscan.backend.connection-timeout}") long connectionTimeout
    ) {
        return new OkHttpClient.Builder()
                .writeTimeout(socketTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(socketTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                // TODO: remove it!
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
    }
}
