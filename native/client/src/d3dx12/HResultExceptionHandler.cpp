#include <HResultExceptionHandler.hpp>
#include <DXHelper.hpp>
#include <utility>

HResultExceptionHandler::HResultExceptionHandler(const HRESULT hresult) : _hresult(hresult) {
}

HResultExceptionHandler::HResultExceptionHandler(const std::function<HRESULT()> & lambda_functor,
                                                 const std::string &operation_name) {
    _operation_name = operation_name;
    _labda_functor = lambda_functor;
}

HRESULT HResultExceptionHandler::Error() { return GetResult(); }

std::string HResultExceptionHandler::ToString() {
    char s_str[64] = {};
    sprintf_s(s_str, "HRESULT code: 0x%08X", static_cast<UINT>(GetResult()));
    return {s_str};
}

void HResultExceptionHandler::ThrowIfFailed() {

    std::string log_message;
    const bool failed = FAILED(GetResult());

    if (failed) {
        log_message = "[ERROR] HResult error occured";
    } else {
        log_message = "[ LOG ] HResult recieved";
    }

    if (_log || failed) {
        std::cout << log_message;
        if (!_operation_name.empty()) {
            std::cout << " for \"" << _operation_name << "\"";
        }
        std::cout << ": " << ToString() << std::endl;
    }

    if (failed) {
        throw std::runtime_error(ToString());
    }
}

HResultExceptionHandler HResultExceptionHandler::Log() {
    _log = true;
    return *this;
}

HResultExceptionHandler HResultExceptionHandler::OperationName(const std::string& operation_name) {
    _operation_name = operation_name;
    return *this;
}

HRESULT HResultExceptionHandler::GetResult() {
    if (_labda_functor && !_labda_resolved) {
        if (_log) {
            std::cout << "[ LOG ] Preparing HResult context for: " << _operation_name << std::endl;
        }
        _hresult = _labda_functor();
        _labda_resolved = true;
    }
    return _hresult;
}
