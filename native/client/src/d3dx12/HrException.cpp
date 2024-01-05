#include <HrException.h>
#include <DXHelper.h>

HrException::HrException(const HRESULT hr): std::runtime_error(DXHelperFunctions::HrToString(hr)), m_hr(hr) {}

HRESULT HrException::Error() const { return m_hr; }