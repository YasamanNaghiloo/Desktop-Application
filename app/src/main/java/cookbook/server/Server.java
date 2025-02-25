package cookbook.server;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Server {
  private static final String URL = "http://localhost:4000";
  private static final String UPLOAD_URL = URL + "/upload";


  public static String pathToUrl(String path) {
    return URL + path;
  }

  /**
   * Uploads a file to the server and returns the path to the file on the server.
   * This file can be access with the path http://localhost:4000/{path}.
   * 
   * @param file
   * 
   * @return The path to the file on the server
   * 
   * @throws IOException
   */
  public static String uploadFile(File file) throws IOException {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost uploadFile = new HttpPost(UPLOAD_URL);

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());

    HttpEntity multipart = builder.build();
    uploadFile.setEntity(multipart);

    CloseableHttpResponse response = httpClient.execute(uploadFile);
    HttpEntity responseEntity = response.getEntity();

    int statusCode = response.getStatusLine().getStatusCode();
    System.out.println("POST Response Code :: " + statusCode);

    if (statusCode == 200) {
      String result = EntityUtils.toString(responseEntity);
      System.out.println("Response Body :: " + result);

      // Extract the path from the response body
      // The Response will look like this: "File uploaded successfully. Path: %s"
      // So we need to split the string at : and get the second part
      String[] parts = result.split(":");
      // If the response is not as expected, print an error message
      if (parts.length == 2) {
        // Return the path to the file on the server
        return parts[1].trim();
      } else {
        throw new IOException("Unexpected response from server: " + result);
      }
    } else {
      throw new IOException("Failed to upload file. Response code: " + statusCode);
    }
  }
}
