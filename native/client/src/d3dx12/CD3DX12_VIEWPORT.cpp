#include <CD3DX12_VIEWPORT.hpp>

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