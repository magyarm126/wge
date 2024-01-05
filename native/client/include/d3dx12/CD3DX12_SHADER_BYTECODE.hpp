#pragma once
#include <DXImports.hpp>

struct CD3DX12_SHADER_BYTECODE : D3D12_SHADER_BYTECODE
{
    CD3DX12_SHADER_BYTECODE();
    explicit CD3DX12_SHADER_BYTECODE(const D3D12_SHADER_BYTECODE &o) noexcept;

    CD3DX12_SHADER_BYTECODE(
        _In_ ID3DBlob* pShaderBlob ) noexcept;

    CD3DX12_SHADER_BYTECODE(
        const void* _pShaderBytecode,
        SIZE_T bytecodeLength ) noexcept;
};