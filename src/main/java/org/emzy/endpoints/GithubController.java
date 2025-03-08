package org.emzy.endpoints;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.emzy.client.GithubRepoResponse;
import org.emzy.client.GithubRestApiClient;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.ArrayList;
import java.util.List;


@Path("/")
public class GithubController {
    private final GithubRestApiClient githubRestApiClient;

    GithubController(@RestClient GithubRestApiClient githubRestApiClient) {
        this.githubRestApiClient = githubRestApiClient;
    }

    @GET
    @Path("/repos/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<GithubRepositoryResponse>> getRepositories(String username) {

        return githubRestApiClient.getRepos(username)
                .onFailure()
                .transform(cause -> new NotFoundException("Username not found."))
                .onItem()
                .transformToUni(repos -> {
                            List<Uni<GithubRepositoryResponse>> reposUni = repos.stream()
                                    .filter(githubRepoResponse -> !githubRepoResponse.fork())
                                    .map(this::getGithubRepositoryResponse)
                                    .toList();

                            Uni<List<GithubRepositoryResponse>> uni = Uni.combine()
                                    .all()
                                    .unis(reposUni)
                                    .with(GithubRepositoryResponse.class, ArrayList::new);
                            return uni;
                        }
                );
    }

    private Uni<GithubRepositoryResponse> getGithubRepositoryResponse(GithubRepoResponse repo) {
        return githubRestApiClient.getBranches(repo.owner().login(), repo.name())
                .map(branches -> new GithubRepositoryResponse(
                        repo.name(),
                        repo.owner().login(),
                        branches.stream()
                                .map(branch ->
                                        new GithubRepositoryResponse.Branch(branch.name(), branch.commit().sha()))
                                .toList()
                ));
    }

    @ServerExceptionMapper
    public Response notFoundException(NotFoundException exception) {
        Log.error(exception.getMessage());
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(
                        new UsernameNotFoundErrorResponse(
                                Response.Status.NOT_FOUND.getStatusCode(),
                                exception.getMessage()
                        )
                )
                .build();
    }
}

