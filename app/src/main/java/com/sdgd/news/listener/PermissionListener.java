package com.sdgd.news.listener;

import java.util.List;

/**
 * @author mark
 * @description: 权限申请回调的接口
 */
public interface PermissionListener {

    void onGranted();

    void onDenied(List<String> deniedPermissions);
}
