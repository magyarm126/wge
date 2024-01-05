#pragma once
#include <stdafx.h>

struct CD3DX12_VIEWPORT : D3D12_VIEWPORT
{
    CD3DX12_VIEWPORT();
    explicit CD3DX12_VIEWPORT( const D3D12_VIEWPORT& o ) noexcept;
    explicit CD3DX12_VIEWPORT(
        FLOAT topLeftX,
        FLOAT topLeftY,
        FLOAT width,
        FLOAT height,
        FLOAT minDepth = D3D12_MIN_DEPTH,
        FLOAT maxDepth = D3D12_MAX_DEPTH ) noexcept;
    explicit CD3DX12_VIEWPORT(
        _In_ ID3D12Resource* pResource,
        UINT mipSlice = 0,
        FLOAT topLeftX = 0.0f,
        FLOAT topLeftY = 0.0f,
        FLOAT minDepth = D3D12_MIN_DEPTH,
        FLOAT maxDepth = D3D12_MAX_DEPTH ) noexcept;
};