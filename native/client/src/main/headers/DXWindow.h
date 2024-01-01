#pragma once

#include <stdafx.h>

class DXWindow{
public:
    virtual ~DXWindow() = default;
    virtual void init() = 0;
    virtual void update() = 0;
    virtual void render() = 0;
    virtual void keyDown(UINT8 key) = 0;
    virtual void keyUp(UINT8 key) = 0;
};