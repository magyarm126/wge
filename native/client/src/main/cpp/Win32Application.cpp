#include <Win32Application.h>
#include <MainDXWindow.h>

Win32Application::Win32Application(HINSTANCE instance_handler, const int show_command_flag) {
    _instance_handler = instance_handler;
    _show_command_flag = show_command_flag;
    _dx_window = reinterpret_cast<DXWindow*>(new MainDXWindow());
}

// ReSharper disable CppDFAConstantFunctionResult
int Win32Application::Run(){
    constexpr wchar_t CLASS_NAME[] = L"Native wge client";

    WNDCLASS wc = {};
    wc.lpfnWndProc = WindowProc;
    wc.hInstance = _instance_handler;
    wc.lpszClassName = CLASS_NAME;

    RegisterClass(&wc);

    const HWND &hwnd = CreateWindowEx(
        0, // Optional window styles.
        CLASS_NAME, // Window class
        L"Web Game Engine - Native C++ Client", // Window text
        WS_OVERLAPPEDWINDOW, // Window style

        // Size and position
        CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,

        nullptr, // Parent window
        nullptr, // Menu
        _instance_handler, // Instance handle
        nullptr // Additional application data
    );

    if (hwnd == nullptr) {
        return 0;
    }
    ShowWindow(hwnd, _show_command_flag);

    MSG msg = {};
    while (GetMessage(&msg, nullptr, 0, 0)) {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }

    return 0;
}

/**
 * \brief A callback function, which you define in your application, that processes messages sent to a window. The WNDPROC type defines a pointer to this callback function. The WndProc name is a placeholder for the name of the function that you define in your application.
 * \param hwnd A handle to the window. This parameter is typically named hWnd.
 * \param uMsg The message. This parameter is typically named uMsg.
 * \param wParam Additional message information. This parameter is typically named wParam. The contents of the wParam parameter depend on the value of the uMsg parameter.
 * \param lParam Additional message information. This parameter is typically named lParam. The contents of the lParam parameter depend on the value of the uMsg parameter.
 * \return The return value is the result of the message processing, and depends on the message sent.
 */
// ReSharper disable CppParameterMayBeConst
LRESULT CALLBACK Win32Application::WindowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam) {
    switch (uMsg) {
        case WM_DESTROY:
            PostQuitMessage(0);
        return 0;

        case WM_PAINT: {
            PAINTSTRUCT ps;
            const HDC &hdc = BeginPaint(hwnd, &ps);

            // All painting occurs here, between BeginPaint and EndPaint.
            FillRect(hdc, &ps.rcPaint, reinterpret_cast<HBRUSH>((COLOR_WINDOW + 1)));
            EndPaint(hwnd, &ps);
        }
        return 0;
        default: break;
    }

    return DefWindowProc(hwnd, uMsg, wParam, lParam);
}

