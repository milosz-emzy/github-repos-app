package org.emzy.endpoints;

import java.util.List;

public record GithubRepositoryResponse(
        String repositoryName,
        String ownerLogin,
        List<Branch> branches
) {
    public record Branch(
            String name,
            String sha
    ) {
    }
}
