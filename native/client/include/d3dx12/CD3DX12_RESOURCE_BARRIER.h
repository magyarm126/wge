#pragma once
#include <DXImports.h>

struct CD3DX12_RESOURCE_BARRIER : D3D12_RESOURCE_BARRIER
{
    CD3DX12_RESOURCE_BARRIER();
    explicit CD3DX12_RESOURCE_BARRIER(const D3D12_RESOURCE_BARRIER &o) noexcept;

    static CD3DX12_RESOURCE_BARRIER Transition(
        _In_ ID3D12Resource* pResource,
        D3D12_RESOURCE_STATES stateBefore,
        D3D12_RESOURCE_STATES stateAfter,
        UINT subresource = D3D12_RESOURCE_BARRIER_ALL_SUBRESOURCES,
        D3D12_RESOURCE_BARRIER_FLAGS flags = D3D12_RESOURCE_BARRIER_FLAG_NONE) noexcept;

    static CD3DX12_RESOURCE_BARRIER Aliasing(
        _In_ ID3D12Resource* pResourceBefore,
        _In_ ID3D12Resource* pResourceAfter) noexcept;

    static CD3DX12_RESOURCE_BARRIER UAV(
        _In_ ID3D12Resource* pResource) noexcept;
};