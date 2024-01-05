// ReSharper disable CppUnusedIncludeDirectiveDXImports
#pragma once

#include <CD3DX12_BLEND_DESC.h>
#include <CD3DX12_CPU_DESCRIPTOR_HANDLE.h>
#include <CD3DX12_RESOURCE_BARRIER.h>
#include <CD3DX12_SHADER_BYTECODE.h>
#include <CD3DX12_RASTERIZER_DESC.h>
#include <CD3DX12_ROOT_SIGNATURE_DESC.h>
#include <CD3DX12_HEAP_PROPERTIES.h>
#include <CD3DX12_RESOURCE_DESC.h>
#include <CD3DX12_VIEWPORT.h>
#include <CD3DX12_RECT.h>
#include "HrException.h"

#include "DXImports.h"

class DXHelper {
public:
    static IDXGIAdapter1 * GetHardwareAdapter(IDXGIFactory1 *pFactory);
    static std::string HrToString(HRESULT hr);
    static void ThrowIfFailed(HRESULT hr);
    static void GetAssetsPath(_Out_writes_(pathSize) WCHAR* path, UINT pathSize);
};