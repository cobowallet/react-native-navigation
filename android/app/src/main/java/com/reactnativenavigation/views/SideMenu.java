package com.reactnativenavigation.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.R;
import com.reactnativenavigation.params.BaseScreenParams;
import com.reactnativenavigation.params.SideMenuParams;
import com.reactnativenavigation.screens.NavigationType;
import com.reactnativenavigation.utils.ViewUtils;

@SuppressLint("ViewConstructor")
public class SideMenu extends SlidingMenu {
    private SideMenuParams menuParams;
    private ContentView menuView;
    private RelativeLayout contentContainer;

    public SideMenu(Context context, SideMenuParams menuParams, SideMenuParams rightMenuParams) {
        super(context);
        setDefaultStyle();

        this.menuParams = menuParams;
        initContentContainer();
        initSideMenu(menuParams);
        setScreenEventListener();
    }

    private void setDefaultStyle() {
        setShadowWidthRes(R.dimen.shadow_width);
        setShadowDrawable(R.drawable.shadow);
        setBehindOffsetRes(R.dimen.slidingmenu_offset);
        setFadeEnabled(false);
    }

    public RelativeLayout getContentContainer() {
        return contentContainer;
    }

    public void destroy() {
        setOnOpenListener(null);
        setOnOpenedListener(null);
        setOnCloseListener(null);
        setOnClosedListener(null);
        destroySideMenu(menuView);
    }

    private void destroySideMenu(ContentView sideMenuView) {
        if (sideMenuView == null) {
            return;
        }

        sideMenuView.unmountReactView();
    }

    public void setVisible(boolean visible, boolean animated, Side side) {
        if (visible) {
            openDrawer(animated, side);
        } else {
            closeDrawer(animated, side);
        }
    }

    public void setEnabled(boolean enabled, Side side) {
        if (enabled) {
            setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        } else {
            setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    public void openDrawer(Side side) {
        showMenu();
    }

    public void openDrawer(boolean animated, Side side) {
        showMenu(animated);
    }

    public void toggleVisible(boolean animated, Side side) {
        toggle(animated);
    }

    public void closeDrawer(boolean animated, Side side) {
        showContent(animated);
    }

    private void initContentContainer() {
        contentContainer = new RelativeLayout(getContext());
        contentContainer.setId(ViewUtils.generateViewId());
        setContent(contentContainer);
    }

    private void initSideMenu(@Nullable SideMenuParams params) {
        if (params == null) {
            return;
        }

        final ContentView sideMenuView = new ContentView(getContext(), params.screenId, params.navigationParams);
        setMenu(sideMenuView);

        if (params.fixedWidth > 0) {
            setBehindWidth(dp2px(params.fixedWidth));
        }

        if (params.disableOpenGesture) {
            setTouchModeAbove(TOUCHMODE_NONE);
        }

        menuView = sideMenuView;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void setScreenEventListener() {
        final OnOpenListener openListener = new OnOpenListener() {
            @Override
            public void onOpen() {
                NavigationApplication.instance.getEventEmitter().sendWillAppearEvent(getVisibleDrawerScreenParams(), NavigationType.OpenSideMenu);
            }
        };

        final OnOpenedListener openedListener = new OnOpenedListener() {
            @Override
            public void onOpened() {
                NavigationApplication.instance.getEventEmitter().sendDidAppearEvent(getVisibleDrawerScreenParams(), NavigationType.OpenSideMenu);
            }

        };

        final OnCloseListener closeListener = new OnCloseListener() {
            @Override
            public void onClose() {
                NavigationApplication.instance.getEventEmitter().sendWillDisappearEvent(getVisibleDrawerScreenParams(), NavigationType.CloseSideMenu);
            }
        };

        final OnClosedListener closedListener = new OnClosedListener() {
            @Override
            public void onClosed() {
                NavigationApplication.instance.getEventEmitter().sendDidDisappearEvent(getVisibleDrawerScreenParams(), NavigationType.CloseSideMenu);
            }
        };

        setOnOpenListener(openListener);
        setOnOpenedListener(openedListener);
        setOnCloseListener(closeListener);
        setOnClosedListener(closedListener);
    }

    private BaseScreenParams getVisibleDrawerScreenParams() {
        return menuParams;
    }

    public void setDrawerLockMode(int i) {
        throw new UnsupportedOperationException();
    }

    public enum Side {
        @SuppressLint("RtlHardcoded") Left(Gravity.LEFT), @SuppressLint("RtlHardcoded") Right(Gravity.RIGHT);

        int gravity;

        Side(int gravity) {
            this.gravity = gravity;
        }

        public static Side fromString(String side) {
            return (side == null) || "left".equals(side.toLowerCase()) ? Left : Right;
        }
    }
}
