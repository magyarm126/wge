#pragma once
#include <DXImports.hpp>
#include <functional>

class HResultExceptionHandler {
public:
    explicit HResultExceptionHandler(HRESULT hresult);

    explicit HResultExceptionHandler(HRESULT hresult, std::string  operation_name);

    explicit HResultExceptionHandler(const std::function<HRESULT()>& lambda_functor, const std::string& operation_name);

    [[nodiscard]] HRESULT Error();

    [[nodiscard]] std::string ToString();

    void ThrowIfFailed();

    [[nodiscard]] HResultExceptionHandler Log();

private:
    HRESULT GetResult();

    HRESULT _hresult = -1l;
    std::string _operation_name;
    bool _log = false;
    std::function<HRESULT()> _labda_functor;
    bool _labda_resolved = false;
};