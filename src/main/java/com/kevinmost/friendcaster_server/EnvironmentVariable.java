package com.kevinmost.friendcaster_server;

public enum EnvironmentVariable {
    PORT;

    private final String name;

    EnvironmentVariable() {
        this.name = name();
    }

    EnvironmentVariable(String name) {
        this.name = name;
    }

    public String get() {
        final String value = System.getenv(name);
        if (value == null) {
            throw new IllegalStateException("Environment Variable " + name() + " is not defined");
        }
        return value;
    }
}
