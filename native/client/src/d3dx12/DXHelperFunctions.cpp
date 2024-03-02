#include <DXHelperFunctions.hpp>

IDXGIAdapter1 *DXHelperFunctions::GetHardwareAdapter(IDXGIFactory1 *pFactory) {
    ComPtr<IDXGIAdapter1> adapter;

    ComPtr<IDXGIFactory6> factory6;
    if (SUCCEEDED(pFactory->QueryInterface(IID_PPV_ARGS(&factory6)))) {
        for (
            UINT adapterIndex = 0;
            SUCCEEDED(factory6->EnumAdapterByGpuPreference(
                adapterIndex,
                DXGI_GPU_PREFERENCE_HIGH_PERFORMANCE,
                IID_PPV_ARGS(&adapter)));
            ++adapterIndex) {
            DXGI_ADAPTER_DESC1 desc;
            adapter->GetDesc1(&desc);

            if (desc.Flags & DXGI_ADAPTER_FLAG_SOFTWARE) {
                // Don't select the Basic Render Driver adapter.
                // If you want a software adapter, pass in "/warp" on the command line.
                continue;
            }

            // Check to see whether the adapter supports Direct3D 12, but don't create the
            // actual device yet.
            if (SUCCEEDED(D3D12CreateDevice(adapter.Get(), D3D_FEATURE_LEVEL_11_0, _uuidof(ID3D12Device), nullptr))) {
                break;
            }
        }
    }

    if (adapter.Get() == nullptr) {
        for (UINT adapterIndex = 0; SUCCEEDED(pFactory->EnumAdapters1(adapterIndex, &adapter)); ++adapterIndex) {
            DXGI_ADAPTER_DESC1 desc;
            if (FAILED(adapter->GetDesc1(&desc))) {
                throw;
            }

            if (desc.Flags & DXGI_ADAPTER_FLAG_SOFTWARE) {
                // Don't select the Basic Render Driver adapter.
                // If you want a software adapter, pass in "/warp" on the command line.
                continue;
            }

            // Check to see whether the adapter supports Direct3D 12, but don't create the
            // actual device yet.
            if (SUCCEEDED(D3D12CreateDevice(adapter.Get(), D3D_FEATURE_LEVEL_11_0, _uuidof(ID3D12Device), nullptr))) {
                break;
            }
        }
    }

    return adapter.Detach();
}

std::wstring DXHelperFunctions::GetAssetsPath() {
    int pathSize = 512;
    auto* assetsPath = new WCHAR[pathSize];

    DWORD size = GetModuleFileName(nullptr, assetsPath, pathSize);
    if (size == 0 || size == pathSize) {
        // Method failed or path was truncated.
        throw std::exception();
    }

    WCHAR *lastSlash = wcsrchr(assetsPath, L'\\');
    if (lastSlash) {
        *(lastSlash + 1) = L'\0';
    }

    return {assetsPath};
}

UINT8 DXHelperFunctions::D3D12GetFormatPlaneCount(ID3D12Device *pDevice, DXGI_FORMAT Format) noexcept {
    D3D12_FEATURE_DATA_FORMAT_INFO formatInfo = {Format, 0};
    if (FAILED(pDevice->CheckFeatureSupport(D3D12_FEATURE_FORMAT_INFO, &formatInfo, sizeof(formatInfo)))) {
        return 0;
    }
    return formatInfo.PlaneCount;
}

UINT DXHelperFunctions::D3D12CalcSubresource(UINT MipSlice, UINT ArraySlice, UINT PlaneSlice, UINT MipLevels,
                                             UINT ArraySize) noexcept {
    return MipSlice + ArraySlice * MipLevels + PlaneSlice * MipLevels * ArraySize;
}
