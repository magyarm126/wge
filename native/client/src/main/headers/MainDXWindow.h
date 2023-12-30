#pragma once
#include <DXWindow.h>

class MainDXWindow final : DXWindow {
public:
    ~MainDXWindow() override;

    void init() override;

    void update() override;

    void render() override;
};