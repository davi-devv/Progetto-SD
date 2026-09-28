package it.unimib.sd2025.handler.common;

@FunctionalInterface
public interface ResourceBuilder <T> {
    T build(String[] parsing);
}
