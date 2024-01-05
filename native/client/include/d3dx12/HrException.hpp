#pragma once
#include <DXImports.hpp>

class HrException final : public std::runtime_error
{
public:
    explicit HrException(HRESULT hr);
    HRESULT Error() const;
private:
    const HRESULT m_hr;
};