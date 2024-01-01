#pragma once

#include <stdafx.h>

class DXHelper {
public:
    static IDXGIAdapter1 * GetHardwareAdapter(IDXGIFactory1 *pFactory);
};