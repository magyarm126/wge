#include <CD3DX12_SHADER_BYTECODE.hpp>

CD3DX12_SHADER_BYTECODE::CD3DX12_SHADER_BYTECODE() = default;

CD3DX12_SHADER_BYTECODE::CD3DX12_SHADER_BYTECODE(const D3D12_SHADER_BYTECODE &o) noexcept:
    D3D12_SHADER_BYTECODE(o) {}

CD3DX12_SHADER_BYTECODE::CD3DX12_SHADER_BYTECODE(ID3DBlob *pShaderBlob) noexcept {
    pShaderBytecode = pShaderBlob->GetBufferPointer();
    BytecodeLength = pShaderBlob->GetBufferSize();
}

CD3DX12_SHADER_BYTECODE::CD3DX12_SHADER_BYTECODE(const void *_pShaderBytecode, SIZE_T bytecodeLength) noexcept {
    pShaderBytecode = _pShaderBytecode;
    BytecodeLength = bytecodeLength;
}
