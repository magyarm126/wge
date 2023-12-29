#ifndef MAIN_H
#define MAIN_H
#endif //MAIN_H

#include <stdafx.h>
/**
 * \brief A callback function, which you define in your application, that processes messages sent to a window. The WNDPROC type defines a pointer to this callback function. The WndProc name is a placeholder for the name of the function that you define in your application.
 * \param hwnd A handle to the window. This parameter is typically named hWnd.
 * \param uMsg The message. This parameter is typically named uMsg.
 * \param wParam Additional message information. This parameter is typically named wParam. The contents of the wParam parameter depend on the value of the uMsg parameter.
 * \param lParam Additional message information. This parameter is typically named lParam. The contents of the lParam parameter depend on the value of the uMsg parameter.
 * \return The return value is the result of the message processing, and depends on the message sent.
 */
LRESULT CALLBACK WindowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam);

/**
 * \brief Every Windows program includes an entry-point function named either WinMain or wWinMain.
 * \param hInstance hInstance is the handle to an instance or handle to a module. The operating system uses this value to identify the executable or EXE when it's loaded in memory. Certain Windows functions need the instance handle, for example to load icons or bitmaps.
 * \param lpCmdLine pCmdLine contains the command-line arguments as a Unicode string.
 * \param nShowCmd nCmdShow is a flag that indicates whether the main application window is minimized, maximized, or shown normally.
 * \return
 */
int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE, PWSTR lpCmdLine, int nShowCmd);