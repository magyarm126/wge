# Web game engine

## System dependency graph

```mermaid
flowchart TB
    subgraph Server[Server]
        subgraph web_server [Kotlin web server]
        end
    end

    subgraph APi[API]
        subgraph web_library [Typescript library]
        end
        subgraph kotlin_library [Kotlin library]
        end
        subgraph native_library [Native C++ library]
        end
        native_library-. manually follows .->kotlin_library
        web_library-. compiled from .->kotlin_library
    end
    
    subgraph client[Client]
        subgraph native_client [Native C++ Client]
        end
        subgraph java_client [Java OpenGl]
        end
        subgraph web_client [Web Angular Frontend]
        end
    end
    
    native_client-->native_library

    web_client-->web_library
    web_server-->kotlin_library
    
    java_client-->kotlin_library

    web_server -. publishes .-> web_client
```