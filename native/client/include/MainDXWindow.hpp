#pragma once
#include <DXWindow.hpp>

class MainDXWindow final : DXWindow {
public:
    MainDXWindow(UINT width, UINT height, std::wstring name);

    ~MainDXWindow() override;

    void init(HWND hwnd) override;

    void update() override;

    void render() override;

    void keyDown(UINT8 key) override;

    void keyUp(UINT8 key) override;

    HWND getWindowHandler() override;

    UINT GetWidth() const;

    UINT GetHeight() const;

    const WCHAR *GetTitle() const;

protected:
    // Viewport dimensions.
    UINT m_width;
    UINT m_height;
    float m_aspectRatio;

    // Adapter info.
    bool m_useWarpDevice;

    void setWindowHandler(HWND hwnd);

    std::wstring GetAssetFullPath(LPCWSTR assetName);

private:
    static const UINT FrameCount = 2;

    struct Vertex {
        XMFLOAT3 position;
        XMFLOAT4 color;
    };

    // Pipeline objects.
    CD3DX12_VIEWPORT m_viewport;
    CD3DX12_RECT m_scissorRect;
    ComPtr<IDXGISwapChain3> m_swapChain;
    ComPtr<ID3D12Device> m_device;
    ComPtr<ID3D12Resource> m_renderTargets[FrameCount];
    ComPtr<ID3D12CommandAllocator> m_commandAllocator;
    ComPtr<ID3D12CommandQueue> m_commandQueue;
    ComPtr<ID3D12RootSignature> m_rootSignature;
    ComPtr<ID3D12DescriptorHeap> m_rtvHeap;
    ComPtr<ID3D12PipelineState> m_pipelineState;
    ComPtr<ID3D12GraphicsCommandList> m_commandList;
    UINT m_rtvDescriptorSize;

    // App resources.
    ComPtr<ID3D12Resource> m_vertexBuffer;
    D3D12_VERTEX_BUFFER_VIEW m_vertexBufferView;

    // Synchronization objects.
    UINT m_frameIndex;
    HANDLE m_fenceEvent;
    ComPtr<ID3D12Fence> m_fence;
    UINT64 m_fenceValue;

    void LoadPipeline();

    void LoadAssets();

    void PopulateCommandList();

    void WaitForPreviousFrame();

    HWND _window_handler = nullptr;

    // Root assets path.
    std::wstring m_assetsPath;

    // Window title.
    std::wstring m_title;

    void testProto();
};
