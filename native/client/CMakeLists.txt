cmake_minimum_required(VERSION 3.27)
project(client)

set(CMAKE_CXX_STANDARD 23)

find_package(Protobuf REQUIRED)

include_directories(${Protobuf_INCLUDE_DIRS})
include_directories(${CMAKE_CURRENT_BINARY_DIR})


set(PROTO_SRCS ${PROJECT_SOURCE_DIR}/src)
set(PROTO_HDRS ${PROJECT_SOURCE_DIR}/include)
protobuf_generate_cpp(PROTO_SRCS PROTO_HDRS ${PROJECT_SOURCE_DIR}/../../common/protobuf/common.proto)

MESSAGE("Libs ${Protobuf_LIBRARIES}")

set(SOURCES
        src/main.cpp
        src/Win32Application.cpp
        src/MainDXWindow.cpp
        src/stdafx.cpp
        src/d3dx12/CD3DX12_VIEWPORT.cpp
        src/d3dx12/CD3DX12_RECT.cpp
        src/d3dx12/CD3DX12_RESOURCE_BARRIER.cpp
        src/d3dx12/HResultExceptionHandler.cpp
        src/d3dx12/CD3DX12_CPU_DESCRIPTOR_HANDLE.cpp
        src/d3dx12/CD3DX12_SHADER_BYTECODE.cpp
        src/d3dx12/CD3DX12_RASTERIZER_DESC.cpp
        src/d3dx12/CD3DX12_BLEND_DESC.cpp
        src/d3dx12/CD3DX12_HEAP_PROPERTIES.cpp
        src/d3dx12/CD3DX12_RESOURCE_DESC.cpp
        src/d3dx12/CD3DX12_RANGE.cpp
        src/d3dx12/CD3DX12_ROOT_SIGNATURE_DESC.cpp
        src/d3dx12/DXHelperFunctions.cpp
)

add_executable(client WIN32 ${SOURCES} ${PROTO_SRCS} ${PROTO_HDRS})

target_include_directories(client
        PRIVATE
        ${PROJECT_SOURCE_DIR}/include
        ${PROJECT_SOURCE_DIR}/include/d3dx12
)

target_link_libraries(client
        dxgi.lib
        d3d12.lib
        d3dcompiler.lib
        ${Protobuf_LIBRARIES}
)

ADD_CUSTOM_TARGET(
        copy_shader_files ALL
        COMMAND ${CMAKE_COMMAND} -E copy_directory
        ${PROJECT_SOURCE_DIR}/assets
        ${PROJECT_BINARY_DIR}/assets
        COMMENT "Copying assets for client" VERBATIM
)

ADD_DEPENDENCIES(client copy_shader_files)