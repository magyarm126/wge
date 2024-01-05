#pragma once
#include <DXImports.hpp>

struct CD3DX12_RECT : D3D12_RECT
{
    CD3DX12_RECT();
    explicit CD3DX12_RECT( const D3D12_RECT& o ) noexcept;
    explicit CD3DX12_RECT(
        LONG Left,
        LONG Top,
        LONG Right,
        LONG Bottom ) noexcept;
};
