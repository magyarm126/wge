#pragma once

class DXWindow{
public:
    virtual ~DXWindow() = default;
    virtual void init() = 0;
    virtual void update() = 0;
    virtual void render() = 0;
};