#pragma once
#include <DXHelper.h>
#include <stdafx.h>

struct CD3DX12_BLEND_DESC : D3D12_BLEND_DESC
{
    CD3DX12_BLEND_DESC() = default;
    explicit CD3DX12_BLEND_DESC( const D3D12_BLEND_DESC& o ) noexcept :
        D3D12_BLEND_DESC( o )
    {}
    explicit CD3DX12_BLEND_DESC( CD3DX12_DEFAULT ) noexcept
    {
        AlphaToCoverageEnable = FALSE;
        IndependentBlendEnable = FALSE;
        const D3D12_RENDER_TARGET_BLEND_DESC defaultRenderTargetBlendDesc =
        {
            FALSE,FALSE,
            D3D12_BLEND_ONE, D3D12_BLEND_ZERO, D3D12_BLEND_OP_ADD,
            D3D12_BLEND_ONE, D3D12_BLEND_ZERO, D3D12_BLEND_OP_ADD,
            D3D12_LOGIC_OP_NOOP,
            D3D12_COLOR_WRITE_ENABLE_ALL,
        };
        for (UINT i = 0; i < D3D12_SIMULTANEOUS_RENDER_TARGET_COUNT; ++i)
            RenderTarget[ i ] = defaultRenderTargetBlendDesc;
    }
};