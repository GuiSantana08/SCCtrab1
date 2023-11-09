package scc.search;

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

    public static SearchClient searchClient() {

        SearchClient searchClient = new SearchClientBuilder()
                .credential(new AzureKeyCredential(PROP_QUERY_KEY))
                .endpoint(PROP_SERVICE_URL).indexName(PROP_INDEX_NAME)
                .buildClient();

        return searchClient;
    }
}
