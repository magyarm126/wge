#include <MainDXWindow.hpp>

MainDXWindow::MainDXWindow(UINT width, UINT height, std::wstring name) {
    m_width = width;
    m_height = height;
    m_title = name;

    m_assetsPath = DXHelperFunctions::GetAssetsPath();

    m_aspectRatio = static_cast<float>(width) / static_cast<float>(height);

    m_frameIndex = 0;
    m_viewport = CD3DX12_VIEWPORT(0.0f, 0.0f, static_cast<float>(width), static_cast<float>(height));
    m_scissorRect = CD3DX12_RECT(0, 0, static_cast<LONG>(width), static_cast<LONG>(height));
    m_rtvDescriptorSize = 0;
}

MainDXWindow::~MainDXWindow() {
    // Ensure that the GPU is no longer referencing resources that are about to be
    // cleaned up by the destructor.
    WaitForPreviousFrame();

    CloseHandle(m_fenceEvent);
}

void MainDXWindow::update() {
}

void MainDXWindow::keyDown(UINT8 key) {
}

void MainDXWindow::keyUp(UINT8 key) {
}

void MainDXWindow::init(HWND hwnd) {
    setWindowHandler(hwnd);
    LoadPipeline();
    LoadAssets();
}

void MainDXWindow::setWindowHandler(HWND hwnd) {
    _window_handler = hwnd;
}

HWND MainDXWindow::getWindowHandler() {
    return _window_handler;
}

UINT MainDXWindow::GetWidth() const { return m_width; }

UINT MainDXWindow::GetHeight() const { return m_height; }

const WCHAR *MainDXWindow::GetTitle() const { return m_title.c_str(); }

std::wstring MainDXWindow::GetAssetFullPath(LPCWSTR assetName) {
    // Helper function for resolving the full path of assets.
    return m_assetsPath + L"assets\\" + assetName;
}

// Load the rendering pipeline dependencies.
void MainDXWindow::LoadPipeline() {
    UINT dxgiFactoryFlags = 0;

#if defined(_DEBUG)
    // Enable the debug layer (requires the Graphics Tools "optional feature").
    // NOTE: Enabling the debug layer after device creation will invalidate the active device.
    {
        ComPtr<ID3D12Debug> debugController;
        if (SUCCEEDED(D3D12GetDebugInterface(IID_PPV_ARGS(&debugController))))
        {
            debugController->EnableDebugLayer();

            // Enable additional debug layers.
            dxgiFactoryFlags |= DXGI_CREATE_FACTORY_DEBUG;
        }
    }
#endif

    ComPtr<IDXGIFactory4> factory;
    HResultExceptionHandler(CreateDXGIFactory2(dxgiFactoryFlags, IID_PPV_ARGS(&factory)))
            .OperationName("CreateFactory")
            .Log()
            .ThrowIfFailed();
    if (m_useWarpDevice) {
        ComPtr<IDXGIAdapter> warpAdapter;
        HResultExceptionHandler(factory->EnumWarpAdapter(IID_PPV_ARGS(&warpAdapter)))
                .OperationName("EnumWarpAdapter")
                .Log()
                .ThrowIfFailed();
        HResultExceptionHandler(D3D12CreateDevice(
                    warpAdapter.Get(),
                    D3D_FEATURE_LEVEL_11_0,
                    IID_PPV_ARGS(&m_device)
                ))
                .OperationName("WarpAdapter Device")
                .Log()
                .ThrowIfFailed();
    } else {
        const ComPtr<IDXGIAdapter1> hardwareAdapter = DXHelperFunctions::GetHardwareAdapter(factory.Get());

        HResultExceptionHandler(D3D12CreateDevice(
                    hardwareAdapter.Get(),
                    D3D_FEATURE_LEVEL_11_0,
                    IID_PPV_ARGS(&m_device)
                ))
                .OperationName("HardwareAdapter device")
                .Log()
                .ThrowIfFailed();
    }

    // Describe and create the command queue.
    D3D12_COMMAND_QUEUE_DESC queueDesc = {};
    queueDesc.Flags = D3D12_COMMAND_QUEUE_FLAG_NONE;
    queueDesc.Type = D3D12_COMMAND_LIST_TYPE_DIRECT;

    HResultExceptionHandler(m_device->CreateCommandQueue(&queueDesc, IID_PPV_ARGS(&m_commandQueue)))
            .OperationName("CreateCommandQueue")
            .Log()
            .ThrowIfFailed();

    // Describe and create the swap chain.
    DXGI_SWAP_CHAIN_DESC1 swapChainDesc = {};
    swapChainDesc.BufferCount = FrameCount;
    swapChainDesc.Width = m_width;
    swapChainDesc.Height = m_height;
    swapChainDesc.Format = DXGI_FORMAT_R8G8B8A8_UNORM;
    swapChainDesc.BufferUsage = DXGI_USAGE_RENDER_TARGET_OUTPUT;
    swapChainDesc.SwapEffect = DXGI_SWAP_EFFECT_FLIP_DISCARD;
    swapChainDesc.SampleDesc.Count = 1;

    ComPtr<IDXGISwapChain1> swapChain;
    HResultExceptionHandler(factory->CreateSwapChainForHwnd(
                m_commandQueue.Get(), // Swap chain needs the queue so that it can force a flush on it.
                getWindowHandler(),
                &swapChainDesc,
                nullptr,
                nullptr,
                &swapChain
            ))
            .OperationName("CreateSwapChainForHwnd")
            .Log()
            .ThrowIfFailed();

    // This sample does not support fullscreen transitions.
    HResultExceptionHandler(factory->MakeWindowAssociation(getWindowHandler(), DXGI_MWA_NO_ALT_ENTER))
            .OperationName("MakeWindowAssociation")
            .Log()
            .ThrowIfFailed();

    HResultExceptionHandler(swapChain.As(&m_swapChain))
            .OperationName("AssignSwapChainToMember")
            .Log()
            .ThrowIfFailed();
    m_frameIndex = m_swapChain->GetCurrentBackBufferIndex();

    // Create descriptor heaps.
    {
        // Describe and create a render target view (RTV) descriptor heap.
        D3D12_DESCRIPTOR_HEAP_DESC rtvHeapDesc = {};
        rtvHeapDesc.NumDescriptors = FrameCount;
        rtvHeapDesc.Type = D3D12_DESCRIPTOR_HEAP_TYPE_RTV;
        rtvHeapDesc.Flags = D3D12_DESCRIPTOR_HEAP_FLAG_NONE;
        HResultExceptionHandler(m_device->CreateDescriptorHeap(&rtvHeapDesc, IID_PPV_ARGS(&m_rtvHeap)))
                .OperationName("CreateDescriptorHeap")
                .Log()
                .ThrowIfFailed();

        m_rtvDescriptorSize = m_device->GetDescriptorHandleIncrementSize(D3D12_DESCRIPTOR_HEAP_TYPE_RTV);
    }

    // Create frame resources.
    {
        CD3DX12_CPU_DESCRIPTOR_HANDLE rtvHandle(m_rtvHeap->GetCPUDescriptorHandleForHeapStart());

        // Create a RTV for each frame.
        for (UINT n = 0; n < FrameCount; n++) {
            HResultExceptionHandler(m_swapChain->GetBuffer(n, IID_PPV_ARGS(&m_renderTargets[n])))
                    .OperationName("GetBuffer")
                    .ThrowIfFailed();
            m_device->CreateRenderTargetView(m_renderTargets[n].Get(), nullptr, rtvHandle);
            rtvHandle.Offset(1, m_rtvDescriptorSize);
        }
    }

    HResultExceptionHandler(
                m_device->CreateCommandAllocator(D3D12_COMMAND_LIST_TYPE_DIRECT, IID_PPV_ARGS(&m_commandAllocator)))
            .OperationName("CreateCommandAllocator")
            .Log()
            .ThrowIfFailed();
}

// Load the sample assets.
void MainDXWindow::LoadAssets() {
    // Create an empty root signature.
    {
        CD3DX12_ROOT_SIGNATURE_DESC rootSignatureDesc;
        rootSignatureDesc.Init(0, nullptr, 0, nullptr, D3D12_ROOT_SIGNATURE_FLAG_ALLOW_INPUT_ASSEMBLER_INPUT_LAYOUT);

        ComPtr<ID3DBlob> signature;
        ComPtr<ID3DBlob> error;
        HResultExceptionHandler(D3D12SerializeRootSignature(&rootSignatureDesc, D3D_ROOT_SIGNATURE_VERSION_1,
                                                            &signature,
                                                            &error))
                .OperationName("SerializeRootSignature")
                .Log()
                .ThrowIfFailed();
        HResultExceptionHandler(m_device->CreateRootSignature(0, signature->GetBufferPointer(),
                                                              signature->GetBufferSize(),
                                                              IID_PPV_ARGS(&m_rootSignature)))
                .OperationName("AssignRootSignature")
                .Log()
                .ThrowIfFailed();
    }

    // Create the pipeline state, which includes compiling and loading shaders.
    {
        ComPtr<ID3DBlob> vertexShader;
        ComPtr<ID3DBlob> pixelShader;

#if defined(_DEBUG)
        // Enable better shader debugging with the graphics debugging tools.
        UINT compileFlags = D3DCOMPILE_DEBUG | D3DCOMPILE_SKIP_OPTIMIZATION;
#else
        UINT compileFlags = 0;
#endif

        HResultExceptionHandler(D3DCompileFromFile(GetAssetFullPath(L"shaders.hlsl").c_str(), nullptr, nullptr,
                                                   "VSMain",
                                                   "vs_5_0", compileFlags, 0, &vertexShader, nullptr))
                .OperationName("CompileVShader")
                .Log()
                .ThrowIfFailed();
        HResultExceptionHandler(D3DCompileFromFile(GetAssetFullPath(L"shaders.hlsl").c_str(), nullptr, nullptr,
                                                   "PSMain",
                                                   "ps_5_0", compileFlags, 0, &pixelShader, nullptr))
                .OperationName("CompilePShader")
                .Log()
                .ThrowIfFailed();

        // Define the vertex input layout.
        D3D12_INPUT_ELEMENT_DESC inputElementDescs[] =
        {
            {"POSITION", 0, DXGI_FORMAT_R32G32B32_FLOAT, 0, 0, D3D12_INPUT_CLASSIFICATION_PER_VERTEX_DATA, 0},
            {"COLOR", 0, DXGI_FORMAT_R32G32B32A32_FLOAT, 0, 12, D3D12_INPUT_CLASSIFICATION_PER_VERTEX_DATA, 0}
        };

        // Describe and create the graphics pipeline state object (PSO).
        D3D12_GRAPHICS_PIPELINE_STATE_DESC psoDesc = {};
        psoDesc.InputLayout = {inputElementDescs, _countof(inputElementDescs)};
        psoDesc.pRootSignature = m_rootSignature.Get();
        psoDesc.VS = CD3DX12_SHADER_BYTECODE(vertexShader.Get());
        psoDesc.PS = CD3DX12_SHADER_BYTECODE(pixelShader.Get());
        psoDesc.RasterizerState = CD3DX12_RASTERIZER_DESC(D3D12_DEFAULT);
        psoDesc.BlendState = CD3DX12_BLEND_DESC(D3D12_DEFAULT);
        psoDesc.DepthStencilState.DepthEnable = FALSE;
        psoDesc.DepthStencilState.StencilEnable = FALSE;
        psoDesc.SampleMask = UINT_MAX;
        psoDesc.PrimitiveTopologyType = D3D12_PRIMITIVE_TOPOLOGY_TYPE_TRIANGLE;
        psoDesc.NumRenderTargets = 1;
        psoDesc.RTVFormats[0] = DXGI_FORMAT_R8G8B8A8_UNORM;
        psoDesc.SampleDesc.Count = 1;
        HResultExceptionHandler(
                    m_device->CreateGraphicsPipelineState(&psoDesc, IID_PPV_ARGS(&m_pipelineState)))
                .OperationName("CreateGraphicPipelineState")
                .Log()
                .ThrowIfFailed();
    }

    // Create the command list.
    HResultExceptionHandler(m_device->CreateCommandList(0, D3D12_COMMAND_LIST_TYPE_DIRECT,
                                                        m_commandAllocator.Get(),
                                                        m_pipelineState.Get(), IID_PPV_ARGS(&m_commandList)))
            .OperationName("CreateCommandList")
            .Log()
            .ThrowIfFailed();

    // Command lists are created in the recording state, but there is nothing
    // to record yet. The main loop expects it to be closed, so close it now.
    HResultExceptionHandler(m_commandList->Close())
            .OperationName("AssignCommandList")
            .Log()
            .ThrowIfFailed();

    // Create the vertex buffer.
    {
        // Define the geometry for a triangle.
        Vertex triangleVertices[] =
        {
            {{0.0f, 0.25f * m_aspectRatio, 0.0f}, {1.0f, 0.0f, 0.0f, 1.0f}},
            {{0.25f, -0.25f * m_aspectRatio, 0.0f}, {0.0f, 1.0f, 0.0f, 1.0f}},
            {{-0.25f, -0.25f * m_aspectRatio, 0.0f}, {0.0f, 0.0f, 1.0f, 1.0f}}
        };

        const UINT vertexBufferSize = sizeof(triangleVertices);

        CD3DX12_HEAP_PROPERTIES cd_3dx12_heap_properties(D3D12_HEAP_TYPE_UPLOAD);
        auto cd_3dx12_resource_desc = CD3DX12_RESOURCE_DESC::Buffer(vertexBufferSize);
        // Note: using upload heaps to transfer static data like vert buffers is not
        // recommended. Every time the GPU needs it, the upload heap will be marshalled
        // over. Please read up on Default Heap usage. An upload heap is used here for
        // code simplicity and because there are very few verts to actually transfer.
        HResultExceptionHandler(m_device->CreateCommittedResource(
                    &cd_3dx12_heap_properties,
                    D3D12_HEAP_FLAG_NONE,
                    &cd_3dx12_resource_desc,
                    D3D12_RESOURCE_STATE_GENERIC_READ,
                    nullptr,
                    IID_PPV_ARGS(&m_vertexBuffer)))
                .OperationName("CreateCommitedResource")
                .Log()
                .ThrowIfFailed();

        // Copy the triangle data to the vertex buffer.
        UINT8 *pVertexDataBegin;
        CD3DX12_RANGE readRange(0, 0); // We do not intend to read from this resource on the CPU.
        HResultExceptionHandler(
                    m_vertexBuffer->Map(0, &readRange, reinterpret_cast<void **>(&pVertexDataBegin)))
                .OperationName("MapVertexBuffer")
                .Log()
                .ThrowIfFailed();
        memcpy(pVertexDataBegin, triangleVertices, sizeof(triangleVertices));
        m_vertexBuffer->Unmap(0, nullptr);

        // Initialize the vertex buffer view.
        m_vertexBufferView.BufferLocation = m_vertexBuffer->GetGPUVirtualAddress();
        m_vertexBufferView.StrideInBytes = sizeof(Vertex);
        m_vertexBufferView.SizeInBytes = vertexBufferSize;
    }

    // Create synchronization objects and wait until assets have been uploaded to the GPU.
    {
        HResultExceptionHandler(m_device->CreateFence(0, D3D12_FENCE_FLAG_NONE, IID_PPV_ARGS(&m_fence)))
                .OperationName("CreateFence")
                .Log()
                .ThrowIfFailed();
        m_fenceValue = 1;

        // Create an event handle to use for frame synchronization.
        m_fenceEvent = CreateEvent(nullptr, FALSE, FALSE, nullptr);
        if (m_fenceEvent == nullptr) {
            HResultExceptionHandler(HRESULT_FROM_WIN32(GetLastError()))
                    .OperationName("LastWinError")
                    .Log()
                    .ThrowIfFailed();
        }

        // Wait for the command list to execute; we are reusing the same command
        // list in our main loop but for now, we just want to wait for setup to
        // complete before continuing.
        WaitForPreviousFrame();
    }
}

// Render the scene.
void MainDXWindow::render() {
    // Record all the commands we need to render the scene into the command list.
    PopulateCommandList();

    // Execute the command list.
    ID3D12CommandList *ppCommandLists[] = {m_commandList.Get()};
    m_commandQueue->ExecuteCommandLists(_countof(ppCommandLists), ppCommandLists);

    // Present the frame.
    HResultExceptionHandler(m_swapChain->Present(1, 0))
            .OperationName("PresentSwapChain")
            .ThrowIfFailed();

    WaitForPreviousFrame();
}

void MainDXWindow::PopulateCommandList() {
    // Command list allocators can only be reset when the associated
    // command lists have finished execution on the GPU; apps should use
    // fences to determine GPU execution progress.
    HResultExceptionHandler(m_commandAllocator->Reset())
            .OperationName("ResetCommandAllocator")
            .ThrowIfFailed();

    // However, when ExecuteCommandList() is called on a particular command
    // list, that command list can then be reset at any time and must be before
    // re-recording.
    HResultExceptionHandler(m_commandList->Reset(m_commandAllocator.Get(), m_pipelineState.Get()))
            .OperationName("ResetCommandList")
            .ThrowIfFailed();

    // Set necessary state.
    m_commandList->SetGraphicsRootSignature(m_rootSignature.Get());
    m_commandList->RSSetViewports(1, &m_viewport);
    m_commandList->RSSetScissorRects(1, &m_scissorRect);

    const auto cd_3dx12_resource_barrier = CD3DX12_RESOURCE_BARRIER::Transition(
        m_renderTargets[m_frameIndex].Get(), D3D12_RESOURCE_STATE_PRESENT, D3D12_RESOURCE_STATE_RENDER_TARGET);
    // Indicate that the back buffer will be used as a render target.
    m_commandList->ResourceBarrier(1, &cd_3dx12_resource_barrier);

    const CD3DX12_CPU_DESCRIPTOR_HANDLE rtvHandle(m_rtvHeap->GetCPUDescriptorHandleForHeapStart(), m_frameIndex,
                                                  m_rtvDescriptorSize);
    m_commandList->OMSetRenderTargets(1, &rtvHandle, FALSE, nullptr);

    // Record commands.
    const float clearColor[] = {0.0f, 0.2f, 0.4f, 1.0f};
    m_commandList->ClearRenderTargetView(rtvHandle, clearColor, 0, nullptr);
    m_commandList->IASetPrimitiveTopology(D3D_PRIMITIVE_TOPOLOGY_TRIANGLELIST);
    m_commandList->IASetVertexBuffers(0, 1, &m_vertexBufferView);
    m_commandList->DrawInstanced(3, 1, 0, 0);

    auto p_barriers = CD3DX12_RESOURCE_BARRIER::Transition(m_renderTargets[m_frameIndex].Get(),
                                                           D3D12_RESOURCE_STATE_RENDER_TARGET,
                                                           D3D12_RESOURCE_STATE_PRESENT);
    // Indicate that the back buffer will now be used to present.
    m_commandList->ResourceBarrier(1, &p_barriers);

    HResultExceptionHandler(m_commandList->Close())
            .OperationName("CloseCommandList")
            .ThrowIfFailed();
}

void MainDXWindow::WaitForPreviousFrame() {
    // WAITING FOR THE FRAME TO COMPLETE BEFORE CONTINUING IS NOT BEST PRACTICE.
    // This is code implemented as such for simplicity. The D3D12HelloFrameBuffering
    // sample illustrates how to use fences for efficient resource usage and to
    // maximize GPU utilization.

    // Signal and increment the fence value.
    const UINT64 fence = m_fenceValue;
    HResultExceptionHandler(m_commandQueue->Signal(m_fence.Get(), fence))
            .OperationName("SignalCommandQueue")
            .ThrowIfFailed();
    m_fenceValue++;

    // Wait until the previous frame is finished.
    if (m_fence->GetCompletedValue() < fence) {
        HResultExceptionHandler(m_fence->SetEventOnCompletion(fence, m_fenceEvent))
                .OperationName("CompleteFence")
                .ThrowIfFailed();
        WaitForSingleObject(m_fenceEvent, INFINITE);
    }

    m_frameIndex = m_swapChain->GetCurrentBackBufferIndex();
}
