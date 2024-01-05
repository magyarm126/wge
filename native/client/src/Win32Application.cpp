#include <Win32Application.hpp>
#include <MainDXWindow.hpp>

Win32Application::Win32Application(const HINSTANCE instance_handler, const int show_command_flag) {
    _instance_handler = instance_handler;
    _show_command_flag = show_command_flag;
    _dx_window = reinterpret_cast<DXWindow*>(new MainDXWindow(1280, 720, L"D3D12 Hello Triangle"));
}

int Win32Application::Run() const {
    constexpr wchar_t CLASS_NAME[] = L"Native wge client";

    WNDCLASS wc = {};
    wc.lpfnWndProc = WindowProc;
    wc.hInstance = _instance_handler;
    wc.lpszClassName = CLASS_NAME;
    wc.style = CS_HREDRAW | CS_VREDRAW;

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
        _dx_window // Additional application data
    );

    if (hwnd == nullptr) {
        return 0;
    }

    _dx_window->init(hwnd);

    ShowWindow(hwnd, _show_command_flag);

    MSG msg = {};
    while (GetMessage(&msg, nullptr, 0, 0)) {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }

    delete _dx_window;

    return 0;
}

HWND Win32Application::GetWindowHandler() const { return _windowHandler; }

// ReSharper disable CppDFAConstantFunctionResult

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

    auto* dx_window = reinterpret_cast<DXWindow*>(GetWindowLongPtr(hwnd, GWLP_USERDATA));

    switch (uMsg) {
        case WM_CREATE:
        {
            // Save context, so dx_window is available for other events.
            const auto pCreateStruct = reinterpret_cast<LPCREATESTRUCT>(lParam);
            SetWindowLongPtr(hwnd, GWLP_USERDATA, reinterpret_cast<LONG_PTR>(pCreateStruct->lpCreateParams));
        }
        return 0;

        case WM_DESTROY:
            PostQuitMessage(0);
        return 0;

        case WM_KEYDOWN:
            if (dx_window) {
                dx_window->keyDown(static_cast<UINT8>(wParam));
            }
        return 0;

        case WM_KEYUP:
            if (dx_window) {
                dx_window->keyUp(static_cast<UINT8>(wParam));
            }
        return 0;

        case WM_PAINT: {
            if (dx_window) {
                dx_window->update();
                dx_window->render();
            }
        }
        return 0;
        default: break;
    }

    return DefWindowProc(hwnd, uMsg, wParam, lParam);
}
