#pragma once

#include <DXWindow.hpp>
#include <stdafx.h>

class Win32Application {
public:
    Win32Application(HINSTANCE instance_handler, int show_command_flag);
    int Run() const;
    HWND GetWindowHandler() const;

protected:
    static LRESULT CALLBACK WindowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam);
private:
    HWND _windowHandler = nullptr;
    HINSTANCE _instance_handler = nullptr;
    int _show_command_flag;
    DXWindow* _dx_window;
};
