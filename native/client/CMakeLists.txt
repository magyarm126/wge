cmake_minimum_required(VERSION 3.27)
project(wge_client)

set(CMAKE_CXX_STANDARD 23)

add_executable(wge_client client.cpp)
target_link_libraries(wge_client PRIVATE
        d3d12.lib
        dxgi.lib
        dxguid.lib
)