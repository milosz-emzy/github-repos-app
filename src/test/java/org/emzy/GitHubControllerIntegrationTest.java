package org.emzy;

import io.quarkus.test.junit.QuarkusTest;
import org.emzy.endpoints.GithubRepositoryResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;


@QuarkusTest
public class GitHubControllerIntegrationTest {

    @Test
    public void testGetRepositories() {
        var response = given()
                .when().get("/repos/milosz-emzy")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("$", GithubRepositoryResponse.class);

        assertThat(response)
                .extracting(GithubRepositoryResponse::ownerLogin)
                .allMatch(ownerLogin -> ownerLogin.equals("milosz-emzy"));

        assertThat(response)
                .extracting(GithubRepositoryResponse::repositoryName)
                .contains("hello-openai")
                .contains("neetcode")
                .contains("java-multithreading")
                .contains("java-features")
                .contains("github-repos-app");

        //should not contain Spoon-Knife, because fork=true
        assertThat(response)
                .extracting(GithubRepositoryResponse::repositoryName)
                .noneMatch(repoName -> repoName.equals("Spoon-Knife"));

        assertThat(response)
                .extracting(GithubRepositoryResponse::branches)
                .isNotEmpty()
                .allMatch(branches -> !branches.getFirst().name().isEmpty() && !branches.getFirst().sha().isEmpty());
    }
}
