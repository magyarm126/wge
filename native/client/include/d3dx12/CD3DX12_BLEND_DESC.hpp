#pragma once
#include <DXImports.hpp>

struct CD3DX12_BLEND_DESC : D3D12_BLEND_DESC
{
    CD3DX12_BLEND_DESC();

    CD3DX12_BLEND_DESC( const D3D12_BLEND_DESC& o );

    explicit CD3DX12_BLEND_DESC( CD3DX12_DEFAULT );
};
