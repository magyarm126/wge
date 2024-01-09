#include <HResultExceptionHandler.hpp>
#include <DXHelper.hpp>
#include <utility>

HResultExceptionHandler::HResultExceptionHandler(const HRESULT hresult) : _hresult(hresult) {
}

HResultExceptionHandler::HResultExceptionHandler(const std::function<HRESULT()> &lambda_functor,
                                                 const std::string &operation_name) {
    _operation_name = operation_name;
    _lambda_functor = lambda_functor;
}

HRESULT HResultExceptionHandler::Error() { return GetResult(); }

std::string HResultExceptionHandler::ToString() {
    std::string toString;

    if (FAILED(GetResult())) {
        toString = "[ERROR] HResult error occured";
    } else {
        toString = "[ LOG ] HResult";
    }

    if (!_operation_name.empty()) {
        toString += " for \"" + _operation_name + "\" - ";
    }
    char s_str[64] = {};
    sprintf_s(s_str, "HRESULT code: 0x%08X", static_cast<UINT>(GetResult()));
    toString += {s_str};

    return toString;
}

void HResultExceptionHandler::ThrowIfFailed() {
    if (FAILED(GetResult())) {
        throw std::runtime_error(ToString());
    }
    if (_log) {
        std::cout << ToString() << std::endl;
    }
}

HResultExceptionHandler HResultExceptionHandler::Log() {
    _log = true;
    return *this;
}

HResultExceptionHandler HResultExceptionHandler::OperationName(const std::string &operation_name) {
    _operation_name = operation_name;
    return *this;
}

HRESULT HResultExceptionHandler::GetResult() {
    if (_lambda_functor && !_lambda_resolved) {
        if (_log) {
            std::cout << "[ LOG ] HResult for \"" << _operation_name << "\" - Preparing context" << std::endl;
        }
        _hresult = _lambda_functor();
        _lambda_resolved = true;
    }
    return _hresult;
}
