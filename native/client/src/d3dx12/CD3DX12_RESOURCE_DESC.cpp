#include <CD3DX12_RESOURCE_DESC.hpp>
#include <DXHelperFunctions.hpp>

CD3DX12_RESOURCE_DESC::CD3DX12_RESOURCE_DESC() = default;

CD3DX12_RESOURCE_DESC::CD3DX12_RESOURCE_DESC(const D3D12_RESOURCE_DESC &o) noexcept:
    D3D12_RESOURCE_DESC( o ) {}

CD3DX12_RESOURCE_DESC::CD3DX12_RESOURCE_DESC(D3D12_RESOURCE_DIMENSION dimension, UINT64 alignment, UINT64 width,
    UINT height, UINT16 depthOrArraySize, UINT16 mipLevels, DXGI_FORMAT format, UINT sampleCount, UINT sampleQuality,
    D3D12_TEXTURE_LAYOUT layout, D3D12_RESOURCE_FLAGS flags) noexcept {
    Dimension = dimension;
    Alignment = alignment;
    Width = width;
    Height = height;
    DepthOrArraySize = depthOrArraySize;
    MipLevels = mipLevels;
    Format = format;
    SampleDesc.Count = sampleCount;
    SampleDesc.Quality = sampleQuality;
    Layout = layout;
    Flags = flags;
}

CD3DX12_RESOURCE_DESC CD3DX12_RESOURCE_DESC::Buffer(const D3D12_RESOURCE_ALLOCATION_INFO &resAllocInfo,
    D3D12_RESOURCE_FLAGS flags) noexcept {
    return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_BUFFER, resAllocInfo.Alignment, resAllocInfo.SizeInBytes,
                                  1, 1, 1, DXGI_FORMAT_UNKNOWN, 1, 0, D3D12_TEXTURE_LAYOUT_ROW_MAJOR, flags );
}

CD3DX12_RESOURCE_DESC CD3DX12_RESOURCE_DESC::
Buffer(UINT64 width, D3D12_RESOURCE_FLAGS flags, UINT64 alignment) noexcept {
    return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_BUFFER, alignment, width, 1, 1, 1,
                                  DXGI_FORMAT_UNKNOWN, 1, 0, D3D12_TEXTURE_LAYOUT_ROW_MAJOR, flags );
}

CD3DX12_RESOURCE_DESC CD3DX12_RESOURCE_DESC::Tex1D(DXGI_FORMAT format, UINT64 width, UINT16 arraySize, UINT16 mipLevels,
    D3D12_RESOURCE_FLAGS flags, D3D12_TEXTURE_LAYOUT layout, UINT64 alignment) noexcept {
    return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_TEXTURE1D, alignment, width, 1, arraySize,
                                  mipLevels, format, 1, 0, layout, flags );
}

CD3DX12_RESOURCE_DESC CD3DX12_RESOURCE_DESC::Tex2D(DXGI_FORMAT format, UINT64 width, UINT height, UINT16 arraySize,
    UINT16 mipLevels, UINT sampleCount, UINT sampleQuality, D3D12_RESOURCE_FLAGS flags, D3D12_TEXTURE_LAYOUT layout,
    UINT64 alignment) noexcept {
    return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_TEXTURE2D, alignment, width, height, arraySize,
                                  mipLevels, format, sampleCount, sampleQuality, layout, flags );
}

CD3DX12_RESOURCE_DESC CD3DX12_RESOURCE_DESC::Tex3D(DXGI_FORMAT format, UINT64 width, UINT height, UINT16 depth,
    UINT16 mipLevels, D3D12_RESOURCE_FLAGS flags, D3D12_TEXTURE_LAYOUT layout, UINT64 alignment) noexcept {
    return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_TEXTURE3D, alignment, width, height, depth,
                                  mipLevels, format, 1, 0, layout, flags );
}

UINT16 CD3DX12_RESOURCE_DESC::Depth() const noexcept { return (Dimension == D3D12_RESOURCE_DIMENSION_TEXTURE3D ? DepthOrArraySize : 1); }

UINT16 CD3DX12_RESOURCE_DESC::ArraySize() const noexcept { return (Dimension != D3D12_RESOURCE_DIMENSION_TEXTURE3D ? DepthOrArraySize : 1); }

UINT8 CD3DX12_RESOURCE_DESC::PlaneCount(ID3D12Device *pDevice) const noexcept { return DXHelperFunctions::D3D12GetFormatPlaneCount(pDevice, Format); }

UINT CD3DX12_RESOURCE_DESC::Subresources(ID3D12Device *pDevice) const noexcept { return MipLevels * ArraySize() * PlaneCount(pDevice); }

UINT CD3DX12_RESOURCE_DESC::CalcSubresource(UINT MipSlice, UINT ArraySlice, UINT PlaneSlice) noexcept { return DXHelperFunctions::D3D12CalcSubresource(MipSlice, ArraySlice, PlaneSlice, MipLevels, ArraySize()); }

bool operator==(const D3D12_RESOURCE_DESC &l, const D3D12_RESOURCE_DESC &r) noexcept {
    return l.Dimension == r.Dimension &&
           l.Alignment == r.Alignment &&
           l.Width == r.Width &&
           l.Height == r.Height &&
           l.DepthOrArraySize == r.DepthOrArraySize &&
           l.MipLevels == r.MipLevels &&
           l.Format == r.Format &&
           l.SampleDesc.Count == r.SampleDesc.Count &&
           l.SampleDesc.Quality == r.SampleDesc.Quality &&
           l.Layout == r.Layout &&
           l.Flags == r.Flags;
}

bool operator!=(const D3D12_RESOURCE_DESC &l, const D3D12_RESOURCE_DESC &r) noexcept { return !( l == r ); }
