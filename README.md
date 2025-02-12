# Utils

This is a collection of utilities that I use in my projects. This collection is compiled from the following projects:

- [github.com/Voinea-Radu/RedisManager](https://github.com/Voinea-Radu/RedisManager)
- [github.com/Voinea-Radu/MessageBuilder](https://github.com/Voinea-Radu/MessageBuilder)
- [github.com/Voinea-Radu/Logger](https://github.com/Voinea-Radu/Logger)
- [github.com/Voinea-Radu/FileManager](https://github.com/Voinea-Radu/FileManager)
- [github.com/Voinea-Radu/Lambda](https://github.com/Voinea-Radu/Lambda)

## How to add to your project

```kotlin
repositories {
    maven("https://repository.voinearadu.com/repository/maven-releases/")
    maven("https://repo.voinearadu.com/") // The short version of the above (might be slower on high latency connections)
}

dependencies {
    implementation("com.voinearadu:utils:VERSION")
    
    // To use the redis_manager
    implementation("redis.clients:jedis:<JEDIS_VERSION>")
}
```