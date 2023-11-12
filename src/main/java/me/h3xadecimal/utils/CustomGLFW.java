package me.h3xadecimal.utils;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiPlatformIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.callback.ImPlatformFuncViewport;
import imgui.callback.ImPlatformFuncViewportFloat;
import imgui.callback.ImPlatformFuncViewportImVec2;
import imgui.callback.ImPlatformFuncViewportString;
import imgui.callback.ImPlatformFuncViewportSuppBoolean;
import imgui.callback.ImPlatformFuncViewportSuppImVec2;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.lwjgl3.glfw.ImGuiImplGlfwNative;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallback;

public class CustomGLFW {

    private static final String OS = System.getProperty("os.name", "generic").toLowerCase();
    protected static final boolean IS_WINDOWS = OS.contains("win");
    protected static final boolean IS_APPLE;
    private long mainWindowPtr;
    private boolean glfwHawWindowTopmost;
    private boolean glfwHasWindowAlpha;
    private boolean glfwHasPerMonitorDpi;
    private boolean glfwHasFocusWindow;
    private boolean glfwHasFocusOnShow;
    private boolean glfwHasMonitorWorkArea;
    private boolean glfwHasOsxWindowPosFix;
    private long mouseWindowPtr;
    private static double grabbedLastMouseX;
    private static double grabbedLastMouseY;
    private static double grabbedCurrMouseX;
    private static double grabbedCurrMouseY;
    private final int[] winWidth = new int[1];
    private final int[] winHeight = new int[1];
    private final int[] fbWidth = new int[1];
    private final int[] fbHeight = new int[1];
    private final long[] mouseCursors = new long[9];
    private final long[] keyOwnerWindows = new long[512];
    private final boolean[] keyPressedGame = new boolean[512];
    private final float[] emptyNavInputs = new float[21];
    private final boolean[] mouseJustPressed = new boolean[5];
    private final ImVec2 mousePosBackup = new ImVec2();
    private final double[] mouseX = new double[1];
    private final double[] mouseY = new double[1];
    private final int[] windowX = new int[1];
    private final int[] windowY = new int[1];
    private final int[] monitorX = new int[1];
    private final int[] monitorY = new int[1];
    private final int[] monitorWorkAreaX = new int[1];
    private final int[] monitorWorkAreaY = new int[1];
    private final int[] monitorWorkAreaWidth = new int[1];
    private final int[] monitorWorkAreaHeight = new int[1];
    private final float[] monitorContentScaleX = new float[1];
    private final float[] monitorContentScaleY = new float[1];
    private GLFWWindowFocusCallback prevUserCallbackWindowFocus = null;
    private GLFWMouseButtonCallback prevUserCallbackMouseButton = null;
    private GLFWScrollCallback prevUserCallbackScroll = null;
    private GLFWCursorPosCallback prevUserCallbackCursorPos = null;
    private GLFWKeyCallback prevUserCallbackKey = null;
    private GLFWCharModsCallback prevUserCallbackChar = null;
    private GLFWMonitorCallback prevUserCallbackMonitor = null;
    private GLFWCursorEnterCallback prevUserCallbackCursorEnter = null;
    private boolean callbacksInstalled = false;
    private boolean wantUpdateMonitors = true;
    private double time = 0.0d;
    private MouseHandledBy grabbed = null;
    private int ignoreMouseMovements = 0;
    private boolean releasedAllKeysBecauseOfDialog = false;
    private boolean releasedAllKeysBecauseOfDisable = false;
    private final double[] grabbedOriginalMouseX = new double[1];
    private final double[] grabbedOriginalMouseY = new double[1];
    private int grabLinkedMouseButton = -1;
    private boolean viewportWindowsHidden = false;

    static {
        IS_APPLE = OS.contains("mac") || OS.contains("darwin");
        grabbedLastMouseX = 0.0d;
        grabbedLastMouseY = 0.0d;
        grabbedCurrMouseX = 0.0d;
        grabbedCurrMouseY = 0.0d;
    }

    public enum MouseHandledBy {
        EDITOR_GRABBED,
        IMGUI,
        GAME,
        BOTH;

        public boolean allowImgui() {
            return this == IMGUI || this == BOTH;
        }

        public boolean allowGame() {
            return this == GAME || this == BOTH;
        }
    }

    public MouseHandledBy getMouseHandledBy() {
        return this.grabbed != null ? this.grabbed : ImGui.getIO().getWantCaptureMouse() ? MouseHandledBy.IMGUI : MouseHandledBy.BOTH;
    }

    public boolean isGrabbed() {
        return this.grabbed != null;
    }

    public void ungrab() {
        if (this.grabbed == null) {
            return;
        }
        this.grabbed = null;
        GLFW.glfwSetInputMode(this.mainWindowPtr, 208897, 212993);
        GLFW.glfwSetCursorPos(this.mainWindowPtr, this.grabbedOriginalMouseX[0], this.grabbedOriginalMouseY[0]);
    }

    public void setGrabbed(boolean passthroughToGame, int grabLinkedMouseButton, double x, double y) {
        if (grabLinkedMouseButton >= 0 && GLFW.glfwGetMouseButton(this.mainWindowPtr, grabLinkedMouseButton) == 0) {
            ungrab();
        } else if (this.grabbed != null) {
        } else {
            this.grabbed = passthroughToGame ? MouseHandledBy.GAME : MouseHandledBy.EDITOR_GRABBED;
            if (grabLinkedMouseButton >= 0) {
                this.grabLinkedMouseButton = grabLinkedMouseButton;
            }
            if (x >= 0.0d && y >= 0.0d) {
                this.grabbedOriginalMouseX[0] = x;
                this.grabbedOriginalMouseY[0] = y;
            } else {
                GLFW.glfwGetCursorPos(this.mainWindowPtr, this.grabbedOriginalMouseX, this.grabbedOriginalMouseY);
            }
            GLFW.glfwSetInputMode(this.mainWindowPtr, 208897, 212995);
            this.ignoreMouseMovements = 2;
        }
    }

    public double getGrabbedMouseDeltaX() {
        double delta = grabbedCurrMouseX - grabbedLastMouseX;
        grabbedLastMouseX = grabbedCurrMouseX;
        return delta;
    }

    public double getGrabbedMouseDeltaY() {
        double delta = grabbedCurrMouseY - grabbedLastMouseY;
        grabbedLastMouseY = grabbedCurrMouseY;
        return delta;
    }

    public void windowFocusCallback(long windowId, boolean focused) {
        if (this.prevUserCallbackWindowFocus != null && windowId == this.mainWindowPtr) {
            this.prevUserCallbackWindowFocus.invoke(windowId, focused);
        }
        ImGui.getIO().addFocusEvent(focused);
    }

    public void cursorEnterCallback(long windowId, boolean entered) {
        if (this.prevUserCallbackCursorEnter != null && windowId == this.mainWindowPtr) {
            this.prevUserCallbackCursorEnter.invoke(windowId, entered);
        }
        if (entered) {
            this.mouseWindowPtr = windowId;
        }
        if (!entered && this.mouseWindowPtr == windowId) {
            this.mouseWindowPtr = 0L;
        }
    }

    public void monitorCallback(long windowId, int event) {
        if (this.prevUserCallbackMonitor != null && windowId == this.mainWindowPtr) {
            this.prevUserCallbackMonitor.invoke(windowId, event);
        }
        this.wantUpdateMonitors = true;
    }

    public boolean init(final long windowId, boolean installCallbacks) {
        this.mainWindowPtr = windowId;
        detectGlfwVersionAndEnabledFeatures();
        ImGuiIO io2 = ImGui.getIO();
        io2.addBackendFlags(1030);
        io2.setBackendPlatformName("imgui_java_impl_glfw");
        int[] keyMap = {-1, 263, 262, 265, 264, 266, 267, 268, 269, 260, 261, 259, 32, 257, 256, 335, 65, 67, 86, 88, 89, 90};
        io2.setKeyMap(keyMap);
        io2.setGetClipboardTextFn(new ImStrSupplier() { // from class: com.moulberry.axiom.editor.CustomGLFW.1
            @Override // imgui.callback.ImStrSupplier
            public String get() {
                String clipboardString = GLFW.glfwGetClipboardString(windowId);
                return clipboardString != null ? clipboardString : "";
            }
        });
        io2.setSetClipboardTextFn(new ImStrConsumer() { // from class: com.moulberry.axiom.editor.CustomGLFW.2
            @Override // imgui.callback.ImStrConsumer
            public void accept(String str) {
                GLFW.glfwSetClipboardString(windowId, str);
            }
        });
        GLFWErrorCallback prevErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI) null);
        this.mouseCursors[0] = GLFW.glfwCreateStandardCursor(221185);
        this.mouseCursors[1] = GLFW.glfwCreateStandardCursor(221186);
        this.mouseCursors[2] = GLFW.glfwCreateStandardCursor(221193);
        this.mouseCursors[3] = GLFW.glfwCreateStandardCursor(221190);
        this.mouseCursors[4] = GLFW.glfwCreateStandardCursor(221189);
        this.mouseCursors[5] = GLFW.glfwCreateStandardCursor(221185);
        this.mouseCursors[6] = GLFW.glfwCreateStandardCursor(221185);
        this.mouseCursors[7] = GLFW.glfwCreateStandardCursor(221188);
        this.mouseCursors[8] = GLFW.glfwCreateStandardCursor(221185);
        GLFW.glfwSetErrorCallback(prevErrorCallback);
        if (installCallbacks) {
            this.callbacksInstalled = true;
            this.prevUserCallbackWindowFocus = GLFW.glfwSetWindowFocusCallback(windowId, this::windowFocusCallback);
            this.prevUserCallbackCursorEnter = GLFW.glfwSetCursorEnterCallback(windowId, this::cursorEnterCallback);
            this.prevUserCallbackMonitor = GLFW.glfwSetMonitorCallback(this::monitorCallback);
        }
        updateMonitors();
        GLFW.glfwSetMonitorCallback(this::monitorCallback);
        ImGuiViewport mainViewport = ImGui.getMainViewport();
        mainViewport.setPlatformHandle(this.mainWindowPtr);
        if (IS_WINDOWS) {
            mainViewport.setPlatformHandleRaw(GLFWNativeWin32.glfwGetWin32Window(windowId));
        }
        if (io2.hasConfigFlags(1024)) {
            initPlatformInterface();
            return true;
        }
        return true;
    }

    public void newFrame() {
        ImGuiIO io2 = ImGui.getIO();
        GLFW.glfwGetWindowSize(this.mainWindowPtr, this.winWidth, this.winHeight);
        GLFW.glfwGetFramebufferSize(this.mainWindowPtr, this.fbWidth, this.fbHeight);
        io2.setDisplaySize(this.winWidth[0], this.winHeight[0]);
        if (this.winWidth[0] > 0 && this.winHeight[0] > 0) {
            float scaleX = this.fbWidth[0] / this.winWidth[0];
            float scaleY = this.fbHeight[0] / this.winHeight[0];
            io2.setDisplayFramebufferScale(scaleX, scaleY);
        }
        if (this.wantUpdateMonitors) {
            updateMonitors();
        }
        double currentTime = GLFW.glfwGetTime();
        io2.setDeltaTime(this.time > 0.0d ? (float) (currentTime - this.time) : 0.016666668f);
        this.time = currentTime;
        this.releasedAllKeysBecauseOfDialog = false;
        boolean shiftDown = false;
        boolean ctrlDown = false;
        boolean altDown = false;
        boolean superDown = false;
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        for (int n = 0; n < platformIO.getViewportsSize(); n++) {
            ImGuiViewport viewport = platformIO.getViewports(n);
            long windowPtr = viewport.getPlatformHandle();
            if (GLFW.glfwGetWindowAttrib(windowPtr, 131073) != 0) {
                shiftDown |= (GLFW.glfwGetKey(windowPtr, 340) == 0 && GLFW.glfwGetKey(windowPtr, 344) == 0) ? false : true;
                ctrlDown |= (GLFW.glfwGetKey(windowPtr, 341) == 0 && GLFW.glfwGetKey(windowPtr, 345) == 0) ? false : true;
                altDown |= (GLFW.glfwGetKey(windowPtr, 342) == 0 && GLFW.glfwGetKey(windowPtr, 346) == 0) ? false : true;
                superDown |= (GLFW.glfwGetKey(windowPtr, 343) == 0 && GLFW.glfwGetKey(windowPtr, 347) == 0) ? false : true;
            }
        }
        io2.setKeyShift(shiftDown);
        io2.setKeyCtrl(ctrlDown);
        io2.setKeyAlt(altDown);
        io2.setKeySuper(superDown);
        updateMousePosAndButtons();
        updateMouseCursor();
        updateGamepads();
    }

    public void updateReleaseAllKeys(boolean release) {
        if (release) {
            if (!this.releasedAllKeysBecauseOfDisable) {
                this.releasedAllKeysBecauseOfDisable = true;
                ImGuiIO io2 = ImGui.getIO();
                for (int key = 0; key < this.keyOwnerWindows.length; key++) {
                    io2.setKeysDown(key, false);
                    this.keyOwnerWindows[key] = 0;
                }
                io2.setKeyCtrl(false);
                io2.setKeyShift(false);
                io2.setKeyAlt(false);
                io2.setKeySuper(false);
                return;
            }
            return;
        }
        this.releasedAllKeysBecauseOfDisable = false;
    }

    private void detectGlfwVersionAndEnabledFeatures() {
        int[] major = new int[1];
        int[] minor = new int[1];
        int[] rev = new int[1];
        GLFW.glfwGetVersion(major, minor, rev);
        int version = (major[0] * 1000) + (minor[0] * 100) + (rev[0] * 10);
        this.glfwHawWindowTopmost = version >= 3200;
        this.glfwHasWindowAlpha = version >= 3300;
        this.glfwHasPerMonitorDpi = version >= 3300;
        this.glfwHasFocusWindow = version >= 3200;
        this.glfwHasFocusOnShow = version >= 3300;
        this.glfwHasMonitorWorkArea = version >= 3300;
    }

    private void updateMousePosAndButtons() {
        ImGuiIO io2 = ImGui.getIO();
        MouseHandledBy mouseHandledBy = getMouseHandledBy();
        for (int i2 = 0; i2 < 5; i2++) {
            io2.setMouseDown(i2, this.mouseJustPressed[i2] || GLFW.glfwGetMouseButton(this.mainWindowPtr, i2) != 0);
            this.mouseJustPressed[i2] = false;
        }
        io2.getMousePos(this.mousePosBackup);
        io2.setMousePos(-3.4028235E38f, -3.4028235E38f);
        io2.setMouseHoveredViewport(0);
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        for (int n = 0; n < platformIO.getViewportsSize(); n++) {
            ImGuiViewport viewport = platformIO.getViewports(n);
            long windowPtr = viewport.getPlatformHandle();
            boolean focused = GLFW.glfwGetWindowAttrib(windowPtr, 131073) != 0;
            if (focused) {
                for (int i3 = 0; i3 < 5; i3++) {
                    if (!io2.getMouseDown(i3)) {
                        io2.setMouseDown(i3, GLFW.glfwGetMouseButton(windowPtr, i3) != 0);
                    }
                }
            }
            if (io2.getWantSetMousePos() && focused) {
                GLFW.glfwSetCursorPos(windowPtr, this.mousePosBackup.x - viewport.getPosX(), this.mousePosBackup.y - viewport.getPosY());
            }
            if (this.mouseWindowPtr == windowPtr || focused) {
                GLFW.glfwGetCursorPos(windowPtr, this.mouseX, this.mouseY);
                if (io2.hasConfigFlags(1024)) {
                    GLFW.glfwGetWindowPos(windowPtr, this.windowX, this.windowY);
                    io2.setMousePos(((float) this.mouseX[0]) + this.windowX[0], ((float) this.mouseY[0]) + this.windowY[0]);
                } else {
                    io2.setMousePos((float) this.mouseX[0], (float) this.mouseY[0]);
                }
            }
        }
    }

    private void updateMouseCursor() {
        ImGuiIO io2 = ImGui.getIO();
        boolean noCursorChange = io2.hasConfigFlags(32);
        boolean cursorDisabled = GLFW.glfwGetInputMode(this.mainWindowPtr, 208897) == 212995;
        if (noCursorChange || cursorDisabled) {
            return;
        }
        int imguiCursor = ImGui.getMouseCursor();
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        for (int n = 0; n < platformIO.getViewportsSize(); n++) {
            long windowPtr = platformIO.getViewports(n).getPlatformHandle();
            if (imguiCursor == -1 || io2.getMouseDrawCursor()) {
                GLFW.glfwSetInputMode(windowPtr, 208897, 212994);
            } else {
                GLFW.glfwSetCursor(windowPtr, this.mouseCursors[imguiCursor] != 0 ? this.mouseCursors[imguiCursor] : this.mouseCursors[0]);
                GLFW.glfwSetInputMode(windowPtr, 208897, 212993);
            }
        }
    }

    private void updateGamepads() {
        ImGuiIO io2 = ImGui.getIO();
        if (!io2.hasConfigFlags(2)) {
            return;
        }
        io2.setNavInputs(this.emptyNavInputs);
        ByteBuffer buttons = GLFW.glfwGetJoystickButtons(0);
        int buttonsCount = buttons.limit();
        FloatBuffer axis = GLFW.glfwGetJoystickAxes(0);
        int axisCount = axis.limit();
        mapButton(0, 0, buttons, buttonsCount, io2);
        mapButton(1, 1, buttons, buttonsCount, io2);
        mapButton(3, 2, buttons, buttonsCount, io2);
        mapButton(2, 3, buttons, buttonsCount, io2);
        mapButton(4, 13, buttons, buttonsCount, io2);
        mapButton(5, 11, buttons, buttonsCount, io2);
        mapButton(6, 10, buttons, buttonsCount, io2);
        mapButton(7, 12, buttons, buttonsCount, io2);
        mapButton(12, 4, buttons, buttonsCount, io2);
        mapButton(13, 5, buttons, buttonsCount, io2);
        mapButton(14, 4, buttons, buttonsCount, io2);
        mapButton(15, 5, buttons, buttonsCount, io2);
        mapAnalog(8, 0, -0.3f, -0.9f, axis, axisCount, io2);
        mapAnalog(9, 0, 0.3f, 0.9f, axis, axisCount, io2);
        mapAnalog(10, 1, 0.3f, 0.9f, axis, axisCount, io2);
        mapAnalog(11, 1, -0.3f, -0.9f, axis, axisCount, io2);
        if (axisCount > 0 && buttonsCount > 0) {
            io2.addBackendFlags(1);
        } else {
            io2.removeBackendFlags(1);
        }
    }

    private void mapButton(int navNo, int buttonNo, ByteBuffer buttons, int buttonsCount, ImGuiIO io2) {
        if (buttonsCount > buttonNo && buttons.get(buttonNo) == 1) {
            io2.setNavInputs(navNo, 1.0f);
        }
    }

    private void mapAnalog(int navNo, int axisNo, float v0, float v1, FloatBuffer axis, int axisCount, ImGuiIO io2) {
        float v = ((axisCount > axisNo ? axis.get(axisNo) : v0) - v0) / (v1 - v0);
        if (v > 1.0f) {
            v = 1.0f;
        }
        if (io2.getNavInputs(navNo) < v) {
            io2.setNavInputs(navNo, v);
        }
    }

    private void updateMonitors() {
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        PointerBuffer monitors = GLFW.glfwGetMonitors();
        platformIO.resizeMonitors(0);
        for (int n = 0; n < monitors.limit(); n++) {
            long monitor = monitors.get(n);
            GLFW.glfwGetMonitorPos(monitor, this.monitorX, this.monitorY);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
            float mainPosX = this.monitorX[0];
            float mainPosY = this.monitorY[0];
            float mainSizeX = vidMode.width();
            float mainSizeY = vidMode.height();
            if (this.glfwHasMonitorWorkArea) {
                GLFW.glfwGetMonitorWorkarea(monitor, this.monitorWorkAreaX, this.monitorWorkAreaY, this.monitorWorkAreaWidth, this.monitorWorkAreaHeight);
            }
            float workPosX = 0.0f;
            float workPosY = 0.0f;
            float workSizeX = 0.0f;
            float workSizeY = 0.0f;
            if (this.glfwHasMonitorWorkArea && this.monitorWorkAreaWidth[0] > 0 && this.monitorWorkAreaHeight[0] > 0) {
                workPosX = this.monitorWorkAreaX[0];
                workPosY = this.monitorWorkAreaY[0];
                workSizeX = this.monitorWorkAreaWidth[0];
                workSizeY = this.monitorWorkAreaHeight[0];
            }
            if (this.glfwHasPerMonitorDpi) {
                GLFW.glfwGetMonitorContentScale(monitor, this.monitorContentScaleX, this.monitorContentScaleY);
            }
            float dpiScale = this.monitorContentScaleX[0];
            platformIO.pushMonitors(mainPosX, mainPosY, mainSizeX, mainSizeY, workPosX, workPosY, workSizeX, workSizeY, dpiScale);
        }
        this.wantUpdateMonitors = false;
    }

    public void setViewportWindowsHidden(boolean viewportWindowsHidden) {
        if (this.viewportWindowsHidden == viewportWindowsHidden) {
            return;
        }
        if (!this.viewportWindowsHidden) {
            this.viewportWindowsHidden = true;
            ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
            for (int n = 0; n < platformIO.getViewportsSize(); n++) {
                long windowPtr = platformIO.getViewports(n).getPlatformHandle();
                if (windowPtr != this.mainWindowPtr) {
                    GLFW.glfwHideWindow(windowPtr);
                }
            }
            return;
        }
        this.viewportWindowsHidden = false;
        ImGuiPlatformIO platformIO2 = ImGui.getPlatformIO();
        for (int n2 = 0; n2 < platformIO2.getViewportsSize(); n2++) {
            long windowPtr2 = platformIO2.getViewports(n2).getPlatformHandle();
            if (windowPtr2 != this.mainWindowPtr) {
                GLFW.glfwShowWindow(windowPtr2);
            }
        }
    }

    private void windowCloseCallback(long windowId) {
        ImGuiViewport vp = ImGui.findViewportByPlatformHandle(windowId);
        vp.setPlatformRequestClose(true);
    }

    private void windowPosCallback(long windowId, int xPos, int yPos) {
        ImGuiViewport vp = ImGui.findViewportByPlatformHandle(windowId);
        ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
        boolean ignoreEvent = ImGui.getFrameCount() <= data.ignoreWindowPosEventFrame + 1;
        if (ignoreEvent) {
            return;
        }
        vp.setPlatformRequestMove(true);
    }

    private void windowSizeCallback(long windowId, int width, int height) {
        ImGuiViewport vp = ImGui.findViewportByPlatformHandle(windowId);
        ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
        boolean ignoreEvent = ImGui.getFrameCount() <= data.ignoreWindowSizeEventFrame + 1;
        if (ignoreEvent) {
            return;
        }
        vp.setPlatformRequestResize(true);
    }

    public final class CreateWindowFunction extends ImPlatformFuncViewport {
        private CreateWindowFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewport
        public void accept(ImGuiViewport vp) {
            ImGuiViewportDataGlfw data = new ImGuiViewportDataGlfw();
            vp.setPlatformUserData(data);
            GLFW.glfwWindowHint(131076, 0);
            GLFW.glfwWindowHint(131073, 0);
            if (CustomGLFW.this.glfwHasFocusOnShow) {
                GLFW.glfwWindowHint(131084, 0);
            }
            GLFW.glfwWindowHint(131077, vp.hasFlags(8) ? 0 : 1);
            if (CustomGLFW.this.glfwHawWindowTopmost) {
                GLFW.glfwWindowHint(131079, vp.hasFlags(512) ? 1 : 0);
            }
            data.window = GLFW.glfwCreateWindow((int) vp.getSizeX(), (int) vp.getSizeY(), "No Title Yet", 0L, CustomGLFW.this.mainWindowPtr);
            data.windowOwned = true;
            vp.setPlatformHandle(data.window);
            if (CustomGLFW.IS_WINDOWS) {
                vp.setPlatformHandleRaw(GLFWNativeWin32.glfwGetWin32Window(data.window));
            }
            GLFW.glfwSetWindowPos(data.window, (int) vp.getPosX(), (int) vp.getPosY());
            GLFW.glfwMakeContextCurrent(data.window);
            GLFW.glfwSwapInterval(0);
        }
    }

    public final class DestroyWindowFunction extends ImPlatformFuncViewport {
        private DestroyWindowFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewport
        public void accept(ImGuiViewport vp) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            vp.setPlatformUserData(null);
            vp.setPlatformHandle(0L);
        }
    }

    public final class ShowWindowFunction extends ImPlatformFuncViewport {
        private ShowWindowFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewport
        public void accept(ImGuiViewport vp) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            if (CustomGLFW.IS_WINDOWS && vp.hasFlags(16)) {
                ImGuiImplGlfwNative.win32hideFromTaskBar(vp.getPlatformHandleRaw());
            }
            if (!CustomGLFW.this.viewportWindowsHidden) {
                GLFW.glfwShowWindow(data.window);
            }
        }
    }

    public static final class GetWindowPosFunction extends ImPlatformFuncViewportSuppImVec2 {
        private final int[] posX = new int[1];
        private final int[] posY = new int[1];

        private GetWindowPosFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewportSuppImVec2
        public void get(ImGuiViewport vp, ImVec2 dstImVec2) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            GLFW.glfwGetWindowPos(data.window, this.posX, this.posY);
            dstImVec2.x = this.posX[0];
            dstImVec2.y = this.posY[0];
        }
    }

    public static final class SetWindowPosFunction extends ImPlatformFuncViewportImVec2 {
        private SetWindowPosFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewportImVec2
        public void accept(ImGuiViewport vp, ImVec2 imVec2) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            data.ignoreWindowPosEventFrame = ImGui.getFrameCount();
            GLFW.glfwSetWindowPos(data.window, (int) imVec2.x, (int) imVec2.y);
        }
    }

    public static final class GetWindowSizeFunction extends ImPlatformFuncViewportSuppImVec2 {
        private final int[] width = new int[1];
        private final int[] height = new int[1];

        private GetWindowSizeFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewportSuppImVec2
        public void get(ImGuiViewport vp, ImVec2 dstImVec2) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            GLFW.glfwGetWindowSize(data.window, this.width, this.height);
            dstImVec2.x = this.width[0];
            dstImVec2.y = this.height[0];
        }
    }

    public final class SetWindowSizeFunction extends ImPlatformFuncViewportImVec2 {

        /* renamed from: x */
        private final int[] f99x = new int[1];

        /* renamed from: y */
        private final int[] f100y = new int[1];
        private final int[] width = new int[1];
        private final int[] height = new int[1];

        private SetWindowSizeFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewportImVec2
        public void accept(ImGuiViewport vp, ImVec2 imVec2) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            if (CustomGLFW.IS_APPLE && !CustomGLFW.this.glfwHasOsxWindowPosFix) {
                GLFW.glfwGetWindowPos(data.window, this.f99x, this.f100y);
                GLFW.glfwGetWindowSize(data.window, this.width, this.height);
                GLFW.glfwSetWindowPos(data.window, this.f99x[0], (this.f100y[0] - this.height[0]) + ((int) imVec2.y));
            }
            data.ignoreWindowSizeEventFrame = ImGui.getFrameCount();
            GLFW.glfwSetWindowSize(data.window, (int) imVec2.x, (int) imVec2.y);
        }
    }

    public static final class SetWindowTitleFunction extends ImPlatformFuncViewportString {
        private SetWindowTitleFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewportString
        public void accept(ImGuiViewport vp, String str) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            GLFW.glfwSetWindowTitle(data.window, str);
        }
    }

    public final class SetWindowFocusFunction extends ImPlatformFuncViewport {
        private SetWindowFocusFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewport
        public void accept(ImGuiViewport vp) {
            if (CustomGLFW.this.glfwHasFocusWindow) {
                ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
                GLFW.glfwFocusWindow(data.window);
            }
        }
    }

    public static final class GetWindowFocusFunction extends ImPlatformFuncViewportSuppBoolean {
        private GetWindowFocusFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewportSuppBoolean
        public boolean get(ImGuiViewport vp) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            return GLFW.glfwGetWindowAttrib(data.window, 131073) != 0;
        }
    }

    public static final class GetWindowMinimizedFunction extends ImPlatformFuncViewportSuppBoolean {
        private GetWindowMinimizedFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewportSuppBoolean
        public boolean get(ImGuiViewport vp) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            return GLFW.glfwGetWindowAttrib(data.window, 131074) != 0;
        }
    }

    public final class SetWindowAlphaFunction extends ImPlatformFuncViewportFloat {
        private SetWindowAlphaFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewportFloat
        public void accept(ImGuiViewport vp, float f) {
            if (CustomGLFW.this.glfwHasWindowAlpha) {
                ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
                GLFW.glfwSetWindowOpacity(data.window, f);
            }
        }
    }

    public static final class RenderWindowFunction extends ImPlatformFuncViewport {
        private RenderWindowFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewport
        public void accept(ImGuiViewport vp) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            GLFW.glfwMakeContextCurrent(data.window);
        }
    }

    public static final class SwapBuffersFunction extends ImPlatformFuncViewport {
        private SwapBuffersFunction() {
        }

        @Override // imgui.callback.ImPlatformFuncViewport
        public void accept(ImGuiViewport vp) {
            ImGuiViewportDataGlfw data = (ImGuiViewportDataGlfw) vp.getPlatformUserData();
            GLFW.glfwMakeContextCurrent(data.window);
            GLFW.glfwSwapBuffers(data.window);
        }
    }

    private void initPlatformInterface() {
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        platformIO.setPlatformCreateWindow(new CreateWindowFunction());
        platformIO.setPlatformDestroyWindow(new DestroyWindowFunction());
        platformIO.setPlatformShowWindow(new ShowWindowFunction());
        platformIO.setPlatformGetWindowPos(new GetWindowPosFunction());
        platformIO.setPlatformSetWindowPos(new SetWindowPosFunction());
        platformIO.setPlatformGetWindowSize(new GetWindowSizeFunction());
        platformIO.setPlatformSetWindowSize(new SetWindowSizeFunction());
        platformIO.setPlatformSetWindowTitle(new SetWindowTitleFunction());
        platformIO.setPlatformSetWindowFocus(new SetWindowFocusFunction());
        platformIO.setPlatformGetWindowFocus(new GetWindowFocusFunction());
        platformIO.setPlatformGetWindowMinimized(new GetWindowMinimizedFunction());
        platformIO.setPlatformSetWindowAlpha(new SetWindowAlphaFunction());
        platformIO.setPlatformRenderWindow(new RenderWindowFunction());
        platformIO.setPlatformSwapBuffers(new SwapBuffersFunction());
        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGuiViewportDataGlfw data = new ImGuiViewportDataGlfw();
        data.window = this.mainWindowPtr;
        data.windowOwned = false;
        mainViewport.setPlatformUserData(data);
    }

    public static final class ImGuiViewportDataGlfw {
        long window;
        boolean windowOwned = false;
        int ignoreWindowPosEventFrame = -1;
        int ignoreWindowSizeEventFrame = -1;

        private ImGuiViewportDataGlfw() {
        }
    }
}