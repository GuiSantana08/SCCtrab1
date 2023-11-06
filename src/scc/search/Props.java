package scc.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;

public class Props {

    public static final String SEARCH_PROP_FILE = "search-azurekeys-northeurope.props";

    public static final String PROP_SERVICE_NAME = "scc2324cs";
    public static final String PROP_SERVICE_URL = "https://scc2324cs.search.windows.net";
    public static final String PROP_INDEX_NAME = "cosmosdb-index";
    public static final String PROP_QUERY_KEY = "rXfxL3cqYMywdmxisOVy7n69eQ9zPkxi3Bgm3fM2rXAzSeA4POpC";

    public static final String PROPS_FILE = "azurekeys.props";

    private static Properties props;

    public static synchronized Properties getProperties() {
        if (props == null) {
            props = new Properties();
            try {
                props.load(new FileInputStream(PROPS_FILE));
            } catch (IOException e) {
                // do nothing
            }
        }
        return props;
    }

    public static SearchClient searchClient() {
        props = getProperties();

        SearchClient searchClient = new SearchClientBuilder()
                .credential(new AzureKeyCredential(props.getProperty(PROP_QUERY_KEY)))
                .endpoint(props.getProperty(PROP_SERVICE_URL)).indexName(props.getProperty(PROP_INDEX_NAME))
                .buildClient();

        return searchClient;
    }
}
