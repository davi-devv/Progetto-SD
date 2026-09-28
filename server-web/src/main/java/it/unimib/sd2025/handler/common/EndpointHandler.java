package it.unimib.sd2025.handler.common;

import jakarta.ws.rs.core.Response;

public interface EndpointHandler<T> {
    Response handle(T input);
}