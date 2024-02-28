package application.resource;

public record Response<T>(int status, String message, T body) { }
