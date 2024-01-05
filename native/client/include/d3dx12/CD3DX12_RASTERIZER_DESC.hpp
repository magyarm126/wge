#pragma once
#include <DXImports.hpp>

struct CD3DX12_RASTERIZER_DESC : D3D12_RASTERIZER_DESC
 {
  CD3DX12_RASTERIZER_DESC();
  explicit CD3DX12_RASTERIZER_DESC( const D3D12_RASTERIZER_DESC& o ) noexcept;

  explicit CD3DX12_RASTERIZER_DESC( CD3DX12_DEFAULT ) noexcept;

  explicit CD3DX12_RASTERIZER_DESC(
      D3D12_FILL_MODE fillMode,
      D3D12_CULL_MODE cullMode,
      BOOL frontCounterClockwise,
      INT depthBias,
      FLOAT depthBiasClamp,
      FLOAT slopeScaledDepthBias,
      BOOL depthClipEnable,
      BOOL multisampleEnable,
      BOOL antialiasedLineEnable,
      UINT forcedSampleCount,
      D3D12_CONSERVATIVE_RASTERIZATION_MODE conservativeRaster) noexcept;
 };