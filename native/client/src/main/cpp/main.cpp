#include <iostream>
#include <d3d12.h>

int main() {
    std::cout << "Hello, Client!" << std::endl;
    constexpr D3D12_FORMAT_SUPPORT2 testEnum = D3D12_FORMAT_SUPPORT2_TILED;
    std::cout << testEnum << std::endl;
    return 0;
}