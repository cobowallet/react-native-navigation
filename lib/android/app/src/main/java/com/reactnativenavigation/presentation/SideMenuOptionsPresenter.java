package com.reactnativenavigation.presentation;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.reactnativenavigation.parse.SideMenuRootOptions;

public class SideMenuOptionsPresenter {

    private SlidingMenu sideMenu;

    public SideMenuOptionsPresenter(SlidingMenu sideMenu) {
        this.sideMenu = sideMenu;
    }

    public void present(SideMenuRootOptions options) {
        if (options.left.visible.isTrue()) {
            sideMenu.showMenu();
        } else if (options.left.visible.isFalse() && sideMenu.isMenuShowing()) {
            sideMenu.showContent();
        }
    }
}
