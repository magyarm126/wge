#pragma once
#include <DXImports.hpp>

class HResultExceptionHandler {
public:
    explicit HResultExceptionHandler(HRESULT hresult);

    [[nodiscard]] HRESULT Error() const;

    [[nodiscard]] std::string ToString() const;

    void ThrowIfFailed() const;

    [[nodiscard]] HResultExceptionHandler Log() const;

private:
    const HRESULT _hresult;
};
