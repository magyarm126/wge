#include <CD3DX12_RESOURCE_BARRIER.hpp>

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