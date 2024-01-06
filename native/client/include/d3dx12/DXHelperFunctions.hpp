#pragma once
#include <DXImports.hpp>

class DXHelperFunctions {
public:
    static IDXGIAdapter1 *GetHardwareAdapter(IDXGIFactory1 *pFactory);

    static std::wstring GetAssetsPath();

    static UINT8 D3D12GetFormatPlaneCount(_In_ ID3D12Device *pDevice, DXGI_FORMAT Format) noexcept;

    static UINT D3D12CalcSubresource(UINT MipSlice, UINT ArraySlice, UINT PlaneSlice, UINT MipLevels,
                                     UINT ArraySize) noexcept;
};
