#include <CD3DX12_HEAP_PROPERTIES.hpp>

CD3DX12_HEAP_PROPERTIES::CD3DX12_HEAP_PROPERTIES() = default;

CD3DX12_HEAP_PROPERTIES::CD3DX12_HEAP_PROPERTIES(const D3D12_HEAP_PROPERTIES &o) noexcept:
    D3D12_HEAP_PROPERTIES(o) {}

CD3DX12_HEAP_PROPERTIES::CD3DX12_HEAP_PROPERTIES(D3D12_CPU_PAGE_PROPERTY cpuPageProperty,
                                                 D3D12_MEMORY_POOL memoryPoolPreference, UINT creationNodeMask, UINT nodeMask) noexcept {
    Type = D3D12_HEAP_TYPE_CUSTOM;
    CPUPageProperty = cpuPageProperty;
    MemoryPoolPreference = memoryPoolPreference;
    CreationNodeMask = creationNodeMask;
    VisibleNodeMask = nodeMask;
}

CD3DX12_HEAP_PROPERTIES::CD3DX12_HEAP_PROPERTIES(D3D12_HEAP_TYPE type, UINT creationNodeMask, UINT nodeMask) noexcept {
    Type = type;
    CPUPageProperty = D3D12_CPU_PAGE_PROPERTY_UNKNOWN;
    MemoryPoolPreference = D3D12_MEMORY_POOL_UNKNOWN;
    CreationNodeMask = creationNodeMask;
    VisibleNodeMask = nodeMask;
}

bool CD3DX12_HEAP_PROPERTIES::IsCPUAccessible() const noexcept {
    return Type == D3D12_HEAP_TYPE_UPLOAD || Type == D3D12_HEAP_TYPE_READBACK || (Type == D3D12_HEAP_TYPE_CUSTOM &&
               (CPUPageProperty == D3D12_CPU_PAGE_PROPERTY_WRITE_COMBINE || CPUPageProperty == D3D12_CPU_PAGE_PROPERTY_WRITE_BACK));
}

bool operator==(const D3D12_HEAP_PROPERTIES &l, const D3D12_HEAP_PROPERTIES &r) noexcept {
    return l.Type == r.Type && l.CPUPageProperty == r.CPUPageProperty &&
           l.MemoryPoolPreference == r.MemoryPoolPreference &&
           l.CreationNodeMask == r.CreationNodeMask &&
           l.VisibleNodeMask == r.VisibleNodeMask;
}

bool operator!=(const D3D12_HEAP_PROPERTIES &l, const D3D12_HEAP_PROPERTIES &r) noexcept { return !( l == r ); }
