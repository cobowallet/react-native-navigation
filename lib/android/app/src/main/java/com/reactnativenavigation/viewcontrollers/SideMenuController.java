package com.reactnativenavigation.viewcontrollers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.reactnativenavigation.R;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.SideMenuOptions;
import com.reactnativenavigation.presentation.OptionsPresenter;
import com.reactnativenavigation.presentation.SideMenuOptionsPresenter;
import com.reactnativenavigation.views.Component;

import java.util.ArrayList;
import java.util.Collection;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SideMenuController extends ParentController<SlidingMenu> {

    private ViewController centerController;
    private ViewController leftController;
    private ViewController rightController;

    public SideMenuController(Activity activity, ChildControllersRegistry childRegistry, String id, Options initialOptions, OptionsPresenter presenter) {
        super(activity, childRegistry, id, presenter, initialOptions);
    }

    private static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private static int getWidth(SideMenuOptions sideMenuOptions) {
        int width = MATCH_PARENT;
        if (sideMenuOptions.width.hasValue()) {
            width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sideMenuOptions.width.get(), Resources.getSystem().getDisplayMetrics());
        }
        return width;
    }

    @Override
    protected ViewController getCurrentChild() {
        if (getView().isMenuShowing()) {
            return leftController;
        } else if ((getView().isSecondaryMenuShowing())) {
            return rightController;
        }
        return centerController;
    }

    @NonNull
    @Override
    protected SlidingMenu createView() {
        final SlidingMenu slidingMenu = new SlidingMenu(getActivity());

        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setFadeEnabled(false);
        slidingMenu.setBehindOffset(dp2px(getActivity(), 60));
        slidingMenu.setShadowWidth(dp2px(getActivity(), 50));
        slidingMenu.setShadowDrawable(R.drawable.slidind_menu_shadow);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        return slidingMenu;
    }

    @Override
    public void sendOnNavigationButtonPressed(String buttonId) {
        centerController.sendOnNavigationButtonPressed(buttonId);
    }

    @NonNull
    @Override
    public Collection<ViewController> getChildControllers() {
        ArrayList<ViewController> children = new ArrayList<>();
        if (centerController != null) children.add(centerController);
        if (leftController != null) children.add(leftController);
        if (rightController != null) children.add(rightController);
        return children;
    }

    @Override
    public void applyChildOptions(Options options, Component child) {
        super.applyChildOptions(options, child);
        performOnParentController(parentController ->
                ((ParentController) parentController).applyChildOptions(this.options, child)
        );
    }

    @Override
    public void mergeChildOptions(Options options, Component child) {
        super.mergeChildOptions(options, child);
        new SideMenuOptionsPresenter(getView()).present(options.sideMenuRootOptions);
        performOnParentController(parentController ->
                ((ParentController) parentController).mergeChildOptions(options.copy().clearSideMenuOptions(), child)
        );
    }

    @Override
    public void mergeOptions(Options options) {
        super.mergeOptions(options);
        new SideMenuOptionsPresenter(getView()).present(this.options.sideMenuRootOptions);
    }

    public void setCenterController(ViewController centerController) {
        this.centerController = centerController;
        View childView = centerController.getView();
        getView().setContent(childView);
    }

    public void setLeftController(ViewController controller) {
        this.leftController = controller;
        final int width = getWidth(options.sideMenuRootOptions.left);

        if (width > 0) {
            getView().setBehindWidth(width);
        }

        getView().setMenu(controller.getView());
    }
}
