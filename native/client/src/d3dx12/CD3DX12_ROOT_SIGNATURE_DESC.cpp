#include <CD3DX12_ROOT_SIGNATURE_DESC.hpp>

CD3DX12_ROOT_SIGNATURE_DESC::CD3DX12_ROOT_SIGNATURE_DESC() = default;

CD3DX12_ROOT_SIGNATURE_DESC::CD3DX12_ROOT_SIGNATURE_DESC(const D3D12_ROOT_SIGNATURE_DESC &o) noexcept:
    D3D12_ROOT_SIGNATURE_DESC(o) {}

CD3DX12_ROOT_SIGNATURE_DESC::CD3DX12_ROOT_SIGNATURE_DESC(UINT numParameters, const D3D12_ROOT_PARAMETER *_pParameters,
    UINT numStaticSamplers, const D3D12_STATIC_SAMPLER_DESC *_pStaticSamplers,
    D3D12_ROOT_SIGNATURE_FLAGS flags) noexcept {
    Init(numParameters, _pParameters, numStaticSamplers, _pStaticSamplers, flags);
}

CD3DX12_ROOT_SIGNATURE_DESC::CD3DX12_ROOT_SIGNATURE_DESC(CD3DX12_DEFAULT) noexcept {
    Init(0, nullptr, 0, nullptr, D3D12_ROOT_SIGNATURE_FLAG_NONE);
}

void CD3DX12_ROOT_SIGNATURE_DESC::Init(UINT numParameters, const D3D12_ROOT_PARAMETER *_pParameters,
    UINT numStaticSamplers, const D3D12_STATIC_SAMPLER_DESC *_pStaticSamplers,
    D3D12_ROOT_SIGNATURE_FLAGS flags) noexcept {
    Init(*this, numParameters, _pParameters, numStaticSamplers, _pStaticSamplers, flags);
}

void CD3DX12_ROOT_SIGNATURE_DESC::Init(D3D12_ROOT_SIGNATURE_DESC &desc, UINT numParameters,
    const D3D12_ROOT_PARAMETER *_pParameters, UINT numStaticSamplers, const D3D12_STATIC_SAMPLER_DESC *_pStaticSamplers,
    D3D12_ROOT_SIGNATURE_FLAGS flags) noexcept {
    desc.NumParameters = numParameters;
    desc.pParameters = _pParameters;
    desc.NumStaticSamplers = numStaticSamplers;
    desc.pStaticSamplers = _pStaticSamplers;
    desc.Flags = flags;
}
