package org.emzy.client;

public record GithubRepoResponse(
        String name,
        boolean fork,
        Owner owner
) {
    public record Owner(
            String login
    ) {
    }
}
