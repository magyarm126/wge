#pragma once
#include <DXImports.hpp>

class HResultExceptionHandler {
public:
    explicit HResultExceptionHandler(HRESULT hresult);

    explicit HResultExceptionHandler(const std::function<HRESULT()>& lambda_functor, const std::string& operation_name);

    [[nodiscard]] HRESULT Error();

    [[nodiscard]] std::string ToString();

    void ThrowIfFailed();

    [[nodiscard]] HResultExceptionHandler Log();

    [[nodiscard]] HResultExceptionHandler OperationName(const std::string& operation_name);

private:
    HRESULT GetResult();

    HRESULT _hresult = -1l;
    std::string _operation_name;
    bool _log = false;
    std::function<HRESULT()> _lambda_functor;
    bool _lambda_resolved = false;
};