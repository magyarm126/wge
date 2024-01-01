cmake_minimum_required(VERSION 3.27)
project(client)

set(CMAKE_CXX_STANDARD 23)

set(SOURCES
        src/main.cpp
        src/Win32Application.cpp
        src/MainDXWindow.cpp
        src/stdafx.cpp
        src/DXHelper.cpp
)

add_executable(client WIN32 ${SOURCES})

target_include_directories(client
        PRIVATE
        ${PROJECT_SOURCE_DIR}/include
)

target_link_libraries(client
        dxgi.lib
        d3d12.lib
        d3dcompiler.lib
)