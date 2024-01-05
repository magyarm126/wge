#pragma once
#include <DXImports.hpp>

class DXHelperFunctions {
public:
    static IDXGIAdapter1 * GetHardwareAdapter(IDXGIFactory1 *pFactory);
    static std::string HrToString(HRESULT hr);
    static void ThrowIfFailed(HRESULT hr);
    static void GetAssetsPath(_Out_writes_(pathSize) WCHAR* path, UINT pathSize);
};