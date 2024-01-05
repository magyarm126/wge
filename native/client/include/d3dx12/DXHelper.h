#pragma once

#include <iostream>
#include <stdafx.h>

struct CD3DX12_DEFAULT {};
extern const DECLSPEC_SELECTANY CD3DX12_DEFAULT D3D12_DEFAULT;

class DXHelper {
public:
    static IDXGIAdapter1 * GetHardwareAdapter(IDXGIFactory1 *pFactory);
    static std::string HrToString(HRESULT hr);
    static void ThrowIfFailed(HRESULT hr);
    static void GetAssetsPath(_Out_writes_(pathSize) WCHAR* path, UINT pathSize);
};