#include <CD3DX12_RECT.h>

CD3DX12_RECT::CD3DX12_RECT() : D3D12_RECT() {
}

CD3DX12_RECT::CD3DX12_RECT(const D3D12_RECT &o) noexcept: D3D12_RECT( o ) {}

CD3DX12_RECT::CD3DX12_RECT(LONG Left, LONG Top, LONG Right, LONG Bottom) noexcept : D3D12_RECT() {
    left = Left;
    top = Top;
    right = Right;
    bottom = Bottom;
}