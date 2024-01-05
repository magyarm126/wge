#include <CD3DX12_CPU_DESCRIPTOR_HANDLE.hpp>

CD3DX12_CPU_DESCRIPTOR_HANDLE::CD3DX12_CPU_DESCRIPTOR_HANDLE() = default;

CD3DX12_CPU_DESCRIPTOR_HANDLE::CD3DX12_CPU_DESCRIPTOR_HANDLE(const D3D12_CPU_DESCRIPTOR_HANDLE &o) noexcept:
    D3D12_CPU_DESCRIPTOR_HANDLE(o) {}

CD3DX12_CPU_DESCRIPTOR_HANDLE::CD3DX12_CPU_DESCRIPTOR_HANDLE(CD3DX12_DEFAULT) noexcept { ptr = 0; }

CD3DX12_CPU_DESCRIPTOR_HANDLE::CD3DX12_CPU_DESCRIPTOR_HANDLE(const D3D12_CPU_DESCRIPTOR_HANDLE &other,
    INT offsetScaledByIncrementSize) noexcept {
    InitOffsetted(other, offsetScaledByIncrementSize);
}

CD3DX12_CPU_DESCRIPTOR_HANDLE::CD3DX12_CPU_DESCRIPTOR_HANDLE(const D3D12_CPU_DESCRIPTOR_HANDLE &other,
    INT offsetInDescriptors, UINT descriptorIncrementSize) noexcept {
    InitOffsetted(other, offsetInDescriptors, descriptorIncrementSize);
}

CD3DX12_CPU_DESCRIPTOR_HANDLE & CD3DX12_CPU_DESCRIPTOR_HANDLE::Offset(INT offsetInDescriptors,
    UINT descriptorIncrementSize) noexcept {
    ptr = SIZE_T(INT64(ptr) + INT64(offsetInDescriptors) * INT64(descriptorIncrementSize));
    return *this;
}

CD3DX12_CPU_DESCRIPTOR_HANDLE & CD3DX12_CPU_DESCRIPTOR_HANDLE::Offset(INT offsetScaledByIncrementSize) noexcept {
    ptr = SIZE_T(INT64(ptr) + INT64(offsetScaledByIncrementSize));
    return *this;
}

bool CD3DX12_CPU_DESCRIPTOR_HANDLE::operator==(_In_ const D3D12_CPU_DESCRIPTOR_HANDLE& other) const noexcept
{
    return (ptr == other.ptr);
}

bool CD3DX12_CPU_DESCRIPTOR_HANDLE::operator!=(const D3D12_CPU_DESCRIPTOR_HANDLE &other) const noexcept {
    return (ptr != other.ptr);
}

CD3DX12_CPU_DESCRIPTOR_HANDLE & CD3DX12_CPU_DESCRIPTOR_HANDLE::operator=(
    const D3D12_CPU_DESCRIPTOR_HANDLE &other) noexcept {
    ptr = other.ptr;
    return *this;
}

void CD3DX12_CPU_DESCRIPTOR_HANDLE::InitOffsetted(const D3D12_CPU_DESCRIPTOR_HANDLE &base,
    INT offsetScaledByIncrementSize) noexcept {
    InitOffsetted(*this, base, offsetScaledByIncrementSize);
}

void CD3DX12_CPU_DESCRIPTOR_HANDLE::InitOffsetted(const D3D12_CPU_DESCRIPTOR_HANDLE &base, INT offsetInDescriptors,
    UINT descriptorIncrementSize) noexcept {
    InitOffsetted(*this, base, offsetInDescriptors, descriptorIncrementSize);
}

void CD3DX12_CPU_DESCRIPTOR_HANDLE::InitOffsetted(D3D12_CPU_DESCRIPTOR_HANDLE &handle,
    const D3D12_CPU_DESCRIPTOR_HANDLE &base, INT offsetScaledByIncrementSize) noexcept {
    handle.ptr = SIZE_T(INT64(base.ptr) + INT64(offsetScaledByIncrementSize));
}

void CD3DX12_CPU_DESCRIPTOR_HANDLE::InitOffsetted(D3D12_CPU_DESCRIPTOR_HANDLE &handle,
    const D3D12_CPU_DESCRIPTOR_HANDLE &base, INT offsetInDescriptors, UINT descriptorIncrementSize) noexcept {
    handle.ptr = SIZE_T(INT64(base.ptr) + INT64(offsetInDescriptors) * INT64(descriptorIncrementSize));
}
