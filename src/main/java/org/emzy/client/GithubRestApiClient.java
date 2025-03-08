package org.emzy.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "github-api")
@ClientHeaderParam(name = "apiVersion", value = "${github.api.version}")
public interface GithubRestApiClient {

    @GET
    @Path("/users/{username}/repos")
    Uni<List<GithubRepoResponse>> getRepos(@PathParam("username") String username);

    @GET
    @Path("/repos/{username}/{repository}/branches")
    Uni<List<GithubBranchResponse>> getBranches(
            @PathParam("username") String username,
            @PathParam("repository") String repository
    );
}
