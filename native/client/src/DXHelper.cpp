#include "DXHelper.h"

IDXGIAdapter1 * DXHelper::GetHardwareAdapter(IDXGIFactory1 *pFactory) {

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
            if(FAILED(adapter->GetDesc1(&desc))) {
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

std::string DXHelper::HrToString(HRESULT hr) {
    char s_str[64] = {};
    sprintf_s(s_str, "HRESULT of 0x%08X", static_cast<UINT>(hr));
    return std::string(s_str);
}

void DXHelper::ThrowIfFailed(HRESULT hr) {
    if (FAILED(hr))
    {
        std::cout << "Error occured:" << hr;
        throw HrException(hr);
    }
}

void DXHelper::GetAssetsPath(WCHAR *path, UINT pathSize) {
    if (path == nullptr)
    {
        throw std::exception();
    }

    DWORD size = GetModuleFileName(nullptr, path, pathSize);
    if (size == 0 || size == pathSize)
    {
        // Method failed or path was truncated.
        throw std::exception();
    }

    WCHAR* lastSlash = wcsrchr(path, L'\\');
    if (lastSlash)
    {
        *(lastSlash + 1) = L'\0';
    }
}

HrException::HrException(const HRESULT hr): std::runtime_error(DXHelper::HrToString(hr)), m_hr(hr) {}

HRESULT HrException::Error() const { return m_hr; }

CD3DX12_RESOURCE_BARRIER::CD3DX12_RESOURCE_BARRIER() : D3D12_RESOURCE_BARRIER() {
}

CD3DX12_RESOURCE_BARRIER::CD3DX12_RESOURCE_BARRIER(const D3D12_RESOURCE_BARRIER &o) noexcept:
    D3D12_RESOURCE_BARRIER(o) {}

CD3DX12_RESOURCE_BARRIER CD3DX12_RESOURCE_BARRIER::Transition(ID3D12Resource *pResource,
    D3D12_RESOURCE_STATES stateBefore, D3D12_RESOURCE_STATES stateAfter, UINT subresource,
    D3D12_RESOURCE_BARRIER_FLAGS flags) noexcept {
    CD3DX12_RESOURCE_BARRIER result = {};
    D3D12_RESOURCE_BARRIER &barrier = result;
    result.Type = D3D12_RESOURCE_BARRIER_TYPE_TRANSITION;
    result.Flags = flags;
    barrier.Transition.pResource = pResource;
    barrier.Transition.StateBefore = stateBefore;
    barrier.Transition.StateAfter = stateAfter;
    barrier.Transition.Subresource = subresource;
    return result;
}

CD3DX12_RESOURCE_BARRIER CD3DX12_RESOURCE_BARRIER::Aliasing(ID3D12Resource *pResourceBefore,
    ID3D12Resource *pResourceAfter) noexcept {
    CD3DX12_RESOURCE_BARRIER result = {};
    D3D12_RESOURCE_BARRIER &barrier = result;
    result.Type = D3D12_RESOURCE_BARRIER_TYPE_ALIASING;
    barrier.Aliasing.pResourceBefore = pResourceBefore;
    barrier.Aliasing.pResourceAfter = pResourceAfter;
    return result;
}

CD3DX12_RESOURCE_BARRIER CD3DX12_RESOURCE_BARRIER::UAV(ID3D12Resource *pResource) noexcept {
    CD3DX12_RESOURCE_BARRIER result = {};
    D3D12_RESOURCE_BARRIER &barrier = result;
    result.Type = D3D12_RESOURCE_BARRIER_TYPE_UAV;
    barrier.UAV.pResource = pResource;
    return result;
}


CD3DX12_VIEWPORT::CD3DX12_VIEWPORT(const D3D12_VIEWPORT &o) noexcept : D3D12_VIEWPORT( o ) {}

CD3DX12_VIEWPORT::CD3DX12_VIEWPORT() : D3D12_VIEWPORT() {}

CD3DX12_VIEWPORT::CD3DX12_VIEWPORT(FLOAT topLeftX, FLOAT topLeftY, FLOAT width, FLOAT height, FLOAT minDepth, FLOAT maxDepth) noexcept : D3D12_VIEWPORT() {
    TopLeftX = topLeftX;
    TopLeftY = topLeftY;
    Width = width;
    Height = height;
    MinDepth = minDepth;
    MaxDepth = maxDepth;
}

CD3DX12_VIEWPORT::CD3DX12_VIEWPORT(ID3D12Resource *pResource, UINT mipSlice, FLOAT topLeftX, FLOAT topLeftY, FLOAT minDepth, FLOAT maxDepth) noexcept : D3D12_VIEWPORT() {
    auto Desc = pResource->GetDesc();
    const UINT64 SubresourceWidth = Desc.Width >> mipSlice;
    const UINT64 SubresourceHeight = Desc.Height >> mipSlice;
    switch (Desc.Dimension)
    {
        case D3D12_RESOURCE_DIMENSION_BUFFER:
            TopLeftX = topLeftX;
        TopLeftY = 0.0f;
        Width = float(Desc.Width) - topLeftX;
        Height = 1.0f;
        break;
        case D3D12_RESOURCE_DIMENSION_TEXTURE1D:
            TopLeftX = topLeftX;
        TopLeftY = 0.0f;
        Width = (SubresourceWidth ? float(SubresourceWidth) : 1.0f) - topLeftX;
        Height = 1.0f;
        break;
        case D3D12_RESOURCE_DIMENSION_TEXTURE2D:
        case D3D12_RESOURCE_DIMENSION_TEXTURE3D:
            TopLeftX = topLeftX;
        TopLeftY = topLeftY;
        Width = (SubresourceWidth ? float(SubresourceWidth) : 1.0f) - topLeftX;
        Height = (SubresourceHeight ? float(SubresourceHeight) : 1.0f) - topLeftY;
        break;
        default: break;
    }

    MinDepth = minDepth;
    MaxDepth = maxDepth;
}

CD3DX12_RECT::CD3DX12_RECT() : D3D12_RECT() {
}

CD3DX12_RECT::CD3DX12_RECT(const D3D12_RECT &o) noexcept: D3D12_RECT( o ) {}

CD3DX12_RECT::CD3DX12_RECT(LONG Left, LONG Top, LONG Right, LONG Bottom) noexcept : D3D12_RECT() {
    left = Left;
    top = Top;
    right = Right;
    bottom = Bottom;
}