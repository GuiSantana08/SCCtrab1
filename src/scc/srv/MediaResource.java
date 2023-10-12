package scc.srv;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import scc.utils.Hash;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource {
	String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=scc2223lab2;AccountKey=aKo5p8YMR6kaaXQdYpakkzC1KDS0rKibpazF4dyV0MpL2ezGcRjzOJEuzCVYZ2lPsgCMWi5L37c/+AStrnDibA==;EndpointSuffix=core.windows.net";
	BlobContainerClient containerClient = new BlobContainerClientBuilder()
			.connectionString(storageConnectionString)
			.containerName("images")
			.buildClient();

	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		BlobClient blob = containerClient.getBlobClient(key);
		blob.upload(BinaryData.fromBytes(contents));
		return key;
	}

	/**
	 * Return the contents of an image. Throw an appropriate error message if
	 * id does not exist.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] download(@PathParam("id") String id) {
		BlobClient blob = containerClient.getBlobClient(id);
		BinaryData data = blob.downloadContent();
		return data.toBytes();

	}
}
