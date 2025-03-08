package org.emzy.endpoints;

public record UsernameNotFoundErrorResponse(
        int status,
        String message
) {
}
