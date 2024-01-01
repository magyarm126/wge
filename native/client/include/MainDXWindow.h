#pragma once
#include <DXWindow.h>

class MainDXWindow final : DXWindow {
public:
    ~MainDXWindow() override;

    void init() override;

    void update() override;

    void render() override;

    void keyDown(UINT8 key) override;

    void keyUp(UINT8 key) override;
};