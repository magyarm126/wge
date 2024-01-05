#include <Win32Application.hpp>

/**
 * \brief Every Windows program includes an entry-point function named either WinMain or wWinMain.
 * \param hInstance hInstance is the handle to an instance or handle to a module. The operating system uses this value to identify the executable or EXE when it's loaded in memory. Certain Windows functions need the instance handle, for example to load icons or bitmaps.
 * \param hPrevInstance hPrevInstance has no meaning. It was used in 16-bit Windows, but is now always zero.
 * \param lpCmdLine pCmdLine contains the command-line arguments as a Unicode string.
 * \param nShowCmd nCmdShow is a flag that indicates whether the main application window is minimized, maximized, or shown normally.
 * \return
 */
// ReSharper disable CppParameterMayBeConst
// ReSharper disable CppParameterNeverUsed
int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, PWSTR lpCmdLine, int nShowCmd) {
    const auto winApp = new Win32Application(hInstance, nShowCmd);
    return winApp->Run();
}