#include <HResultExceptionHandler.hpp>
#include <DXHelper.hpp>

HResultExceptionHandler::HResultExceptionHandler(const HRESULT hresult) : _hresult(hresult) {
}

HRESULT HResultExceptionHandler::Error() const { return _hresult; }

std::string HResultExceptionHandler::ToString() const {
    char s_str[64] = {};
    sprintf_s(s_str, "HRESULT of 0x%08X", static_cast<UINT>(_hresult));
    return {s_str};
}

void HResultExceptionHandler::ThrowIfFailed() const {
    if (FAILED(_hresult)) {
        std::cout << "[ERROR] HResult error occured:" << _hresult << std::endl;
        throw std::runtime_error(ToString());
    }
}

HResultExceptionHandler HResultExceptionHandler::Log() const {
    std::cout << "[ LOG ] HResult recieved:" << _hresult << std::endl;
    return *this;
}
