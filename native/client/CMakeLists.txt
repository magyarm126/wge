cmake_minimum_required(VERSION 3.27)
project(client)

set(CMAKE_CXX_STANDARD 23)

set(SOURCES
        src/main.cpp
        src/Win32Application.cpp
        src/MainDXWindow.cpp
        src/stdafx.cpp
        src/DXHelper.cpp
        src/d3dx12/CD3DX12_VIEWPORT.cpp
        src/d3dx12/CD3DX12_RECT.cpp
        src/d3dx12/CD3DX12_RESOURCE_BARRIER.cpp
        include/d3dx12/CD3DX12_RESOURCE_BARRIER.h
)

add_executable(client WIN32 ${SOURCES})

target_include_directories(client
        PRIVATE
        ${PROJECT_SOURCE_DIR}/include
        ${PROJECT_SOURCE_DIR}/include/d3dx12
)

target_link_libraries(client
        dxgi.lib
        d3d12.lib
        d3dcompiler.lib
)

ADD_CUSTOM_TARGET(
        copy_shader_files ALL
        COMMAND ${CMAKE_COMMAND} -E copy_directory
        ${PROJECT_SOURCE_DIR}/assets
        ${PROJECT_BINARY_DIR}/assets
        COMMENT "Copying assets for client" VERBATIM
)

ADD_DEPENDENCIES ( client copy_shader_files )