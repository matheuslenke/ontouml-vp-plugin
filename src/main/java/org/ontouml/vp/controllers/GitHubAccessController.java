package org.ontouml.vp.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.ontouml.vp.OntoUMLPlugin;
import org.ontouml.vp.model.Configurations;
import org.ontouml.vp.model.GitHubRelease;
import org.ontouml.vp.model.GitHubReleaseAsset;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class GitHubAccessController {

  public static final String GITHUB_API = "https://api.github.com";
  public static final String REPOS = "/repos";
  public static final String RELEASES = "/releases";

  public static void lookupUpdates() throws IOException {
    List<GitHubRelease> releases = GitHubAccessController.getReleases();
    Configurations config = Configurations.getInstance();
    config.setReleases(releases);
    config.save();
  }

  public static List<GitHubRelease> getReleases() throws IOException {
    final URL url =
        new URL(
            GITHUB_API
                + REPOS
                + "/"
                + OntoUMLPlugin.PLUGIN_REPO_OWNER
                + "/"
                + OntoUMLPlugin.PLUGIN_REPO_NAME
                + RELEASES);
    final HttpsURLConnection request = (HttpsURLConnection) url.openConnection();

    request.setRequestMethod("GET");
    request.setRequestProperty("Accept", "application/vnd.github.v3+json");
    request.setReadTimeout(60000);

    final int responseCode = request.getResponseCode();

    if (responseCode == HttpsURLConnection.HTTP_OK) {
      try (BufferedReader in =
          new BufferedReader(new InputStreamReader(request.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
          response.append(line);
        }

        JsonArray releasesArray =
                new JsonParser().parse(response.toString()).getAsJsonArray();
        List<GitHubRelease> releases = new ArrayList<>();

        releasesArray.forEach(
            item -> {
              releases.add(new GitHubRelease(item.getAsJsonObject()));
            });

        return releases;
      }
    }

    return null;
  }

  public static File downloadReleaseAsset(GitHubReleaseAsset asset)
      throws IOException {
    final URL downloadURL = new URL(asset.getDownloadUrl());
    final ReadableByteChannel rbc = Channels.newChannel(downloadURL.openStream());
    final Path pluginDownloadDir = Files.createTempDirectory("pluginDownloadDir");
    final File downloadFile = new File(pluginDownloadDir.toFile(), asset.getName());
    final FileOutputStream fos = new FileOutputStream(downloadFile);

    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    fos.close();

    return downloadFile;
  }
}
