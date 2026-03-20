package com.example.library_be.service;

public interface RedisService {

    void save(String key, String value, long timeout);

    String get(String key);

    void delete(String key);

    boolean exists(String key);
}
