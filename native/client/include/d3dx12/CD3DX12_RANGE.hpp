#pragma once
#include <DXImports.hpp>

struct CD3DX12_RANGE : public D3D12_RANGE
{
    CD3DX12_RANGE() = default;
    explicit CD3DX12_RANGE(const D3D12_RANGE &o) noexcept :
        D3D12_RANGE(o)
    {}
    CD3DX12_RANGE(
        SIZE_T begin,
        SIZE_T end ) noexcept
    {
        Begin = begin;
        End = end;
    }
};
