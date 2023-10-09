package scc.srv.resources;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import scc.srv.interfaces.MediaResourceInterface;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource implements MediaResourceInterface {

	private final String storageConnectionString = "";

	public String upload(byte[] contents, String filename) {
		try {
			BinaryData data = BinaryData.fromBytes(contents);

			// Get container client
			BlobContainerClient containerClient = new BlobContainerClientBuilder()
					.connectionString(storageConnectionString)
					.containerName("images")
					.buildClient();

			// Get client to blob
			BlobClient blob = containerClient.getBlobClient(filename);

			// Upload contents from BinaryData (check documentation for other alternatives)
			blob.upload(data);

			return "File updloaded : " + filename;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	public byte[] download(@PathParam("id") String id) {
		try {
			// Get container client
			BlobContainerClient containerClient = new BlobContainerClientBuilder()
					.connectionString(storageConnectionString)
					.containerName("images")
					.buildClient();

			// Get client to blob
			BlobClient blob = containerClient.getBlobClient(id);

			// Download contents to BinaryData (check documentation for other alternatives)
			BinaryData data = blob.downloadContent();

			byte[] arr = data.toBytes();

			System.out.println("Blob size : " + arr.length);

			return arr;
		} catch (Exception e) {
			e.printStackTrace();
			byte[] arr = null;
			return arr;
		}
	}
}
