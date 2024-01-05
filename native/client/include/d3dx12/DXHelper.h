#pragma once

#include <iostream>
#include <stdafx.h>

struct CD3DX12_DEFAULT {};
extern const DECLSPEC_SELECTANY CD3DX12_DEFAULT D3D12_DEFAULT;

class DXHelper {
public:
    static IDXGIAdapter1 * GetHardwareAdapter(IDXGIFactory1 *pFactory);
    static std::string HrToString(HRESULT hr);
    static void ThrowIfFailed(HRESULT hr);
    static void GetAssetsPath(_Out_writes_(pathSize) WCHAR* path, UINT pathSize);
};

struct CD3DX12_BLEND_DESC : public D3D12_BLEND_DESC
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

struct CD3DX12_HEAP_PROPERTIES : public D3D12_HEAP_PROPERTIES
{
    CD3DX12_HEAP_PROPERTIES() = default;
    explicit CD3DX12_HEAP_PROPERTIES(const D3D12_HEAP_PROPERTIES &o) noexcept :
        D3D12_HEAP_PROPERTIES(o)
    {}
    CD3DX12_HEAP_PROPERTIES(
        D3D12_CPU_PAGE_PROPERTY cpuPageProperty,
        D3D12_MEMORY_POOL memoryPoolPreference,
        UINT creationNodeMask = 1,
        UINT nodeMask = 1 ) noexcept
    {
        Type = D3D12_HEAP_TYPE_CUSTOM;
        CPUPageProperty = cpuPageProperty;
        MemoryPoolPreference = memoryPoolPreference;
        CreationNodeMask = creationNodeMask;
        VisibleNodeMask = nodeMask;
    }
    explicit CD3DX12_HEAP_PROPERTIES(
        D3D12_HEAP_TYPE type,
        UINT creationNodeMask = 1,
        UINT nodeMask = 1 ) noexcept
    {
        Type = type;
        CPUPageProperty = D3D12_CPU_PAGE_PROPERTY_UNKNOWN;
        MemoryPoolPreference = D3D12_MEMORY_POOL_UNKNOWN;
        CreationNodeMask = creationNodeMask;
        VisibleNodeMask = nodeMask;
    }
    bool IsCPUAccessible() const noexcept
    {
        return Type == D3D12_HEAP_TYPE_UPLOAD || Type == D3D12_HEAP_TYPE_READBACK || (Type == D3D12_HEAP_TYPE_CUSTOM &&
            (CPUPageProperty == D3D12_CPU_PAGE_PROPERTY_WRITE_COMBINE || CPUPageProperty == D3D12_CPU_PAGE_PROPERTY_WRITE_BACK));
    }
};
inline bool operator==( const D3D12_HEAP_PROPERTIES& l, const D3D12_HEAP_PROPERTIES& r ) noexcept
{
    return l.Type == r.Type && l.CPUPageProperty == r.CPUPageProperty &&
        l.MemoryPoolPreference == r.MemoryPoolPreference &&
        l.CreationNodeMask == r.CreationNodeMask &&
        l.VisibleNodeMask == r.VisibleNodeMask;
}
inline bool operator!=( const D3D12_HEAP_PROPERTIES& l, const D3D12_HEAP_PROPERTIES& r ) noexcept
{ return !( l == r ); }

inline UINT8 D3D12GetFormatPlaneCount(
    _In_ ID3D12Device* pDevice,
    DXGI_FORMAT Format
    ) noexcept
{
    D3D12_FEATURE_DATA_FORMAT_INFO formatInfo = { Format, 0 };
    if (FAILED(pDevice->CheckFeatureSupport(D3D12_FEATURE_FORMAT_INFO, &formatInfo, sizeof(formatInfo))))
    {
        return 0;
    }
    return formatInfo.PlaneCount;
}

inline constexpr UINT D3D12CalcSubresource( UINT MipSlice, UINT ArraySlice, UINT PlaneSlice, UINT MipLevels, UINT ArraySize ) noexcept
{
    return MipSlice + ArraySlice * MipLevels + PlaneSlice * MipLevels * ArraySize;
}

struct CD3DX12_RESOURCE_DESC : public D3D12_RESOURCE_DESC
{
    CD3DX12_RESOURCE_DESC() = default;
    explicit CD3DX12_RESOURCE_DESC( const D3D12_RESOURCE_DESC& o ) noexcept :
        D3D12_RESOURCE_DESC( o )
    {}
    CD3DX12_RESOURCE_DESC(
        D3D12_RESOURCE_DIMENSION dimension,
        UINT64 alignment,
        UINT64 width,
        UINT height,
        UINT16 depthOrArraySize,
        UINT16 mipLevels,
        DXGI_FORMAT format,
        UINT sampleCount,
        UINT sampleQuality,
        D3D12_TEXTURE_LAYOUT layout,
        D3D12_RESOURCE_FLAGS flags ) noexcept
    {
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
    static inline CD3DX12_RESOURCE_DESC Buffer(
        const D3D12_RESOURCE_ALLOCATION_INFO& resAllocInfo,
        D3D12_RESOURCE_FLAGS flags = D3D12_RESOURCE_FLAG_NONE ) noexcept
    {
        return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_BUFFER, resAllocInfo.Alignment, resAllocInfo.SizeInBytes,
            1, 1, 1, DXGI_FORMAT_UNKNOWN, 1, 0, D3D12_TEXTURE_LAYOUT_ROW_MAJOR, flags );
    }
    static inline CD3DX12_RESOURCE_DESC Buffer(
        UINT64 width,
        D3D12_RESOURCE_FLAGS flags = D3D12_RESOURCE_FLAG_NONE,
        UINT64 alignment = 0 ) noexcept
    {
        return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_BUFFER, alignment, width, 1, 1, 1,
            DXGI_FORMAT_UNKNOWN, 1, 0, D3D12_TEXTURE_LAYOUT_ROW_MAJOR, flags );
    }
    static inline CD3DX12_RESOURCE_DESC Tex1D(
        DXGI_FORMAT format,
        UINT64 width,
        UINT16 arraySize = 1,
        UINT16 mipLevels = 0,
        D3D12_RESOURCE_FLAGS flags = D3D12_RESOURCE_FLAG_NONE,
        D3D12_TEXTURE_LAYOUT layout = D3D12_TEXTURE_LAYOUT_UNKNOWN,
        UINT64 alignment = 0 ) noexcept
    {
        return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_TEXTURE1D, alignment, width, 1, arraySize,
            mipLevels, format, 1, 0, layout, flags );
    }
    static inline CD3DX12_RESOURCE_DESC Tex2D(
        DXGI_FORMAT format,
        UINT64 width,
        UINT height,
        UINT16 arraySize = 1,
        UINT16 mipLevels = 0,
        UINT sampleCount = 1,
        UINT sampleQuality = 0,
        D3D12_RESOURCE_FLAGS flags = D3D12_RESOURCE_FLAG_NONE,
        D3D12_TEXTURE_LAYOUT layout = D3D12_TEXTURE_LAYOUT_UNKNOWN,
        UINT64 alignment = 0 ) noexcept
    {
        return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_TEXTURE2D, alignment, width, height, arraySize,
            mipLevels, format, sampleCount, sampleQuality, layout, flags );
    }
    static inline CD3DX12_RESOURCE_DESC Tex3D(
        DXGI_FORMAT format,
        UINT64 width,
        UINT height,
        UINT16 depth,
        UINT16 mipLevels = 0,
        D3D12_RESOURCE_FLAGS flags = D3D12_RESOURCE_FLAG_NONE,
        D3D12_TEXTURE_LAYOUT layout = D3D12_TEXTURE_LAYOUT_UNKNOWN,
        UINT64 alignment = 0 ) noexcept
    {
        return CD3DX12_RESOURCE_DESC( D3D12_RESOURCE_DIMENSION_TEXTURE3D, alignment, width, height, depth,
            mipLevels, format, 1, 0, layout, flags );
    }
    inline UINT16 Depth() const noexcept
    { return (Dimension == D3D12_RESOURCE_DIMENSION_TEXTURE3D ? DepthOrArraySize : 1); }
    inline UINT16 ArraySize() const noexcept
    { return (Dimension != D3D12_RESOURCE_DIMENSION_TEXTURE3D ? DepthOrArraySize : 1); }
    inline UINT8 PlaneCount(_In_ ID3D12Device* pDevice) const noexcept
    { return D3D12GetFormatPlaneCount(pDevice, Format); }
    inline UINT Subresources(_In_ ID3D12Device* pDevice) const noexcept
    { return MipLevels * ArraySize() * PlaneCount(pDevice); }
    inline UINT CalcSubresource(UINT MipSlice, UINT ArraySlice, UINT PlaneSlice) noexcept
    { return D3D12CalcSubresource(MipSlice, ArraySlice, PlaneSlice, MipLevels, ArraySize()); }
};
inline bool operator==( const D3D12_RESOURCE_DESC& l, const D3D12_RESOURCE_DESC& r ) noexcept
{
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
inline bool operator!=( const D3D12_RESOURCE_DESC& l, const D3D12_RESOURCE_DESC& r ) noexcept
{ return !( l == r ); }

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

struct CD3DX12_ROOT_SIGNATURE_DESC : public D3D12_ROOT_SIGNATURE_DESC
{
    CD3DX12_ROOT_SIGNATURE_DESC() = default;
    explicit CD3DX12_ROOT_SIGNATURE_DESC(const D3D12_ROOT_SIGNATURE_DESC &o) noexcept :
        D3D12_ROOT_SIGNATURE_DESC(o)
    {}
    CD3DX12_ROOT_SIGNATURE_DESC(
        UINT numParameters,
        _In_reads_opt_(numParameters) const D3D12_ROOT_PARAMETER* _pParameters,
        UINT numStaticSamplers = 0,
        _In_reads_opt_(numStaticSamplers) const D3D12_STATIC_SAMPLER_DESC* _pStaticSamplers = nullptr,
        D3D12_ROOT_SIGNATURE_FLAGS flags = D3D12_ROOT_SIGNATURE_FLAG_NONE) noexcept
    {
        Init(numParameters, _pParameters, numStaticSamplers, _pStaticSamplers, flags);
    }
    CD3DX12_ROOT_SIGNATURE_DESC(CD3DX12_DEFAULT) noexcept
    {
        Init(0, nullptr, 0, nullptr, D3D12_ROOT_SIGNATURE_FLAG_NONE);
    }

    inline void Init(
        UINT numParameters,
        _In_reads_opt_(numParameters) const D3D12_ROOT_PARAMETER* _pParameters,
        UINT numStaticSamplers = 0,
        _In_reads_opt_(numStaticSamplers) const D3D12_STATIC_SAMPLER_DESC* _pStaticSamplers = nullptr,
        D3D12_ROOT_SIGNATURE_FLAGS flags = D3D12_ROOT_SIGNATURE_FLAG_NONE) noexcept
    {
        Init(*this, numParameters, _pParameters, numStaticSamplers, _pStaticSamplers, flags);
    }

    static inline void Init(
        _Out_ D3D12_ROOT_SIGNATURE_DESC &desc,
        UINT numParameters,
        _In_reads_opt_(numParameters) const D3D12_ROOT_PARAMETER* _pParameters,
        UINT numStaticSamplers = 0,
        _In_reads_opt_(numStaticSamplers) const D3D12_STATIC_SAMPLER_DESC* _pStaticSamplers = nullptr,
        D3D12_ROOT_SIGNATURE_FLAGS flags = D3D12_ROOT_SIGNATURE_FLAG_NONE) noexcept
    {
        desc.NumParameters = numParameters;
        desc.pParameters = _pParameters;
        desc.NumStaticSamplers = numStaticSamplers;
        desc.pStaticSamplers = _pStaticSamplers;
        desc.Flags = flags;
    }
};