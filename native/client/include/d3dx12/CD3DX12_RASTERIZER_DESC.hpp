#pragma once
#include <DXImports.hpp>

struct CD3DX12_RASTERIZER_DESC : D3D12_RASTERIZER_DESC
 {
  CD3DX12_RASTERIZER_DESC() = default;
  explicit CD3DX12_RASTERIZER_DESC( const D3D12_RASTERIZER_DESC& o ) noexcept :
      D3D12_RASTERIZER_DESC( o )
  {}
  explicit CD3DX12_RASTERIZER_DESC( CD3DX12_DEFAULT ) noexcept
  {
   FillMode = D3D12_FILL_MODE_SOLID;
   CullMode = D3D12_CULL_MODE_BACK;
   FrontCounterClockwise = FALSE;
   DepthBias = D3D12_DEFAULT_DEPTH_BIAS;
   DepthBiasClamp = D3D12_DEFAULT_DEPTH_BIAS_CLAMP;
   SlopeScaledDepthBias = D3D12_DEFAULT_SLOPE_SCALED_DEPTH_BIAS;
   DepthClipEnable = TRUE;
   MultisampleEnable = FALSE;
   AntialiasedLineEnable = FALSE;
   ForcedSampleCount = 0;
   ConservativeRaster = D3D12_CONSERVATIVE_RASTERIZATION_MODE_OFF;
  }
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
      D3D12_CONSERVATIVE_RASTERIZATION_MODE conservativeRaster) noexcept
  {
   FillMode = fillMode;
   CullMode = cullMode;
   FrontCounterClockwise = frontCounterClockwise;
   DepthBias = depthBias;
   DepthBiasClamp = depthBiasClamp;
   SlopeScaledDepthBias = slopeScaledDepthBias;
   DepthClipEnable = depthClipEnable;
   MultisampleEnable = multisampleEnable;
   AntialiasedLineEnable = antialiasedLineEnable;
   ForcedSampleCount = forcedSampleCount;
   ConservativeRaster = conservativeRaster;
  }
 };