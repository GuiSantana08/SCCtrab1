package scc.azure.functions.src.main.serverless;

import com.microsoft.azure.functions.annotation.*;

import redis.clients.jedis.Jedis;
import scc.azure.cache.RedisCache;

import com.microsoft.azure.functions.*;

/**
 * Azure Functions with Blob Trigger.
 */
public class BlobStoreFunction {
	@FunctionName("blobtest")
	public void setLastBlobInfo(
			@BlobTrigger(name = "blobtest", dataType = "binary", path = "images/{name}", connection = "BlobStoreConnection") byte[] content,
			@BindingName("name") String blobname,
			final ExecutionContext context) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.incr("cnt:blob");
			jedis.set("serverless::blob::name",
					"Blob name : " + blobname + " ; size = " + (content == null ? "0" : content.length));
		}
	}

}
